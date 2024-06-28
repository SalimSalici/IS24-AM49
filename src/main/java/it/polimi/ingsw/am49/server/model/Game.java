package it.polimi.ingsw.am49.server.model;

import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.server.ServerConfig;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.server.model.decks.GameDeck;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.events.EventEmitter;
import it.polimi.ingsw.am49.server.model.events.EventListener;
import it.polimi.ingsw.am49.server.model.events.EventManager;
import it.polimi.ingsw.am49.server.model.events.GameEvent;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.server.model.states.ChooseStarterSideState;
import it.polimi.ingsw.am49.server.model.states.EndGameState;
import it.polimi.ingsw.am49.server.model.states.GameState;
import it.polimi.ingsw.am49.server.model.states.PlaceCardState;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Main class for managing the state and flow of the game. This class encapsulates all the essential components
 * like players, decks, game state, and other dynamic elements that change throughout the game. It implements
 * Serializable for object serialization, supporting saving and loading game states, and EventEmitter for event
 * handling to manage game-related events dynamically.
 */
public class Game implements Serializable, EventEmitter {

    /**
     * The number of players in the game.
     */
    private final int numPlayers;

    /**
     * The current turn number.
     */
    private int turn;

    /**
     * The current round number.
     */
    private int round;

    /**
     * A list of all players in the game.
     */
    private final List<Player> players;

    /**
     * The player whose turn it is currently.
     */
    private Player currentPlayer;

    /**
     * Whether the game has entered the end game phase.
     */
    private boolean endGame;

    /**
     * Whether the game is paused.
     */
    private boolean paused;

    /**
     * Whether this is the final round of the game.
     */
    private boolean finalRound;

    /**
     * The event manager for the game.
     */
    private transient EventManager eventManager;

    /**
     * The common objectives for the game.
     */
    private final ObjectiveCard[] commonObjectives;

    /**
     * The current game state.
     */
    private GameState gameState;

    /**
     * The revealed resource cards.
     */
    private final ResourceCard[] revealedResources;

    /**
     * The revealed gold cards.
     */
    private final GoldCard[] revealedGolds;

    /**
     * The deck of resource cards.
     */
    private final GameDeck<ResourceCard> resourceGameDeck;

    /**
     * The deck of gold cards.
     */
    private final GameDeck<GoldCard> goldGameDeck;

    /**
     * Constructor of the Game class.
     * @param numPlayers number of players that are playing the current game
     */
    public Game(int numPlayers) {

        Log.getLogger().info("Creating new game with " + numPlayers + " players.");

        this.numPlayers = numPlayers;
        this.eventManager = new EventManager();
        this.turn = 0;
        this.round = 0;
        this.players = new ArrayList<>();
        this.endGame = false;
        this.paused = false;
        this.finalRound = false;
        this.commonObjectives = new ObjectiveCard[2]; // the common objectives are set in the relevant game state
        this.revealedResources = new ResourceCard[2];
        this.revealedGolds = new GoldCard[2];
        this.resourceGameDeck = DeckLoader.getInstance().getNewResourceDeck();
        this.goldGameDeck = DeckLoader.getInstance().getNewGoldDeck();

        for (int i = 0; i < revealedResources.length; i++)
            revealedResources[i] = resourceGameDeck.draw();

        for (int i = 0; i < revealedGolds.length; i++)
            revealedGolds[i] = goldGameDeck.draw();

        Log.getLogger().info("Game created.");
    }

    /**
     * Adds a player to the game with a given username and color.
     *
     * @param username the username of the player
     * @param color the color assigned to the player
     */
    public synchronized void addPlayer(String username, Color color) {
        if (this.players.size() < this.numPlayers) {
            Player newPlayer = new Player(username);
            newPlayer.setColor(color);
            this.players.add(newPlayer);
        }
    }

    /**
     * Adds a player to the game.
     *
     * @param player the player to add
     */
    public synchronized void addPlayer(Player player) {
        if (this.players.size() < this.numPlayers && this.getPlayerByUsername(player.getUsername()) == null) {
            this.players.add(player);
        }
    }

    /**
     * Starts the game by setting the initial game state.
     */
    public synchronized void startGame(){
        this.gameState = new ChooseStarterSideState(this);
        this.gameState.setUp();
    }

    /**
     * Sets the new state of the game.
     * @param state the new game state
     */
    public synchronized void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Retrieves a player by their username.
     *
     * @param username the username of the player to retrieve.
     * @return the player if found, null otherwise.
     */
    public synchronized Player getPlayerByUsername(String username) {
        for (Player p : this.players)
            if (p.getUsername().equals(username)) return p;
        return null;
    }

    /**
     * Executes a game action based on the received message.
     *
     * @param action the action received from a player.
     * @throws InvalidActionException if the action is invalid.
     * @throws NotYourTurnException if it's not the player's turn.
     */
    public synchronized void executeAction(GameAction action) throws InvalidActionException, NotYourTurnException {
        this.gameState.execute(action);
    }

    /**
     * Handles the transition to the next turn, including checking for end game conditions and setting the next player.
     */
    public void handleSwitchToNextTurn() {
        this.incrementTurn();

        do {

            if (this.isFinalRound() && this.currentPlayer.equals(this.getLastPlayer())
                    || this.allPlayersDeadlocked()) {
                this.gameState.goToNextState(new EndGameState(this));
                return;
            }

            this.handleEndGameAndFinalRound();
            this.setCurrentPlayer(this.getNextPlayer());
        } while (!this.getCurrentPlayer().isOnline());

        this.gameState.goToNextState(new PlaceCardState(this));
    }

    /**
     * Handles the logic for determining if the end game phase or final round should start.
     */
    private synchronized void handleEndGameAndFinalRound() {
        System.out.println(this.currentPlayer.getPoints());
        if (this.currentPlayer.getPoints() >= ServerConfig.pointsToStartEndgame || (this.resourceGameDeck.isEmpty() && this.goldGameDeck.isEmpty()))
            this.setEndGame(true);

        if (this.currentPlayer.equals(this.getLastPlayer())) {
            this.incrementRound();
            if (this.isEndGame())
                this.setFinalRound(true);
        }
    }

    /**
     * Disconnects a player from the game.
     * @param username the username of the player to disconnect
     */
    public synchronized void disconnectPlayer(String username) {
        this.gameState.disconnectPlayer(username);
    }

    /**
     * Reconnects a player to the game.
     * @param username the username of the player to reconnect
     * @return true if the player was successfully reconnected, false otherwise
     */
    public synchronized boolean reconnectPlayer(String username) {
        Player player = this.getPlayerByUsername(username);
        if (player == null) return false; // No player with the given username
        if (player.isOnline()) return false; // Player with the given username already online
        player.setIsOnline(true);
        return true;
    }

    /**
     * Ends the game and forfeits the victory to the specified player.
     * @param username the username of the player who won by forfeit
     */
    public synchronized void forfeitWinner(String username) {
        this.gameState.goToNextState(new EndGameState(this, this.getPlayerByUsername(username)));
    }

    /**
     * @return the game deck containing resource cards
     */
    public synchronized GameDeck<ResourceCard> getResourceGameDeck() {
        return this.resourceGameDeck;
    }

    /**
     * @return the game deck containing gold cards
     */
    public synchronized GameDeck<GoldCard> getGoldGameDeck() {
        return this.goldGameDeck;
    }

    /**
     * Increments the turn counter for the game.
     */
    public synchronized void incrementTurn() {
        this.turn++;
    }

    /**
     * Increments the round counter for the game.
     */
    public synchronized void incrementRound() {
        this.round++;
    }

    /**
     * Sets the current player
     * @param player the player who is currently taking their turn
     */
    public synchronized void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Determines and returns the next player to take a turn.
     *
     * @return the next player in the turn order
     */
    public synchronized Player getNextPlayer() {
        if (this.currentPlayer.equals(this.players.getLast()))
            return this.players.getFirst();
        return players.get(players.indexOf(currentPlayer) + 1);
    }

    /**
     * @return the array of common objectives
     */
    public synchronized ObjectiveCard[] getCommonObjectives() {
        return this.commonObjectives;
    }

    /**
     * @return the number of players participating in the game
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * @return the current round number of the game
     */
    public synchronized int getRound() {
        return round;
    }

    /**
     * @return the current turn number within the current round
     */
    public synchronized int getTurn() {
        return turn;
    }

    /**
     * @return the player that will play first in each round
     */
    public Player getStartingPlayer() {
        return this.players.getFirst();
    }

    /**
     * @return the player that will play last in each round
     */
    public Player getLastPlayer() {
        return this.players.getLast();
    }

    /**
     * @return the player who is currently taking their turn
     */
    public synchronized Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return the list of all players currently in the game
     */
    public synchronized List<Player> getPlayers() {
        return this.players;
    }

    /**
     * @return the current state of the game
     */
    public synchronized GameState getGameState() {
        return gameState;
    }

    /**
     * Adds an event listener for a specific game event type.
     * @param gameEventType the type of game event
     * @param eventListener the event listener to add
     */
    @Override
    public synchronized void addEventListener(GameEventType gameEventType, it.polimi.ingsw.am49.server.model.events.EventListener eventListener) {
        this.eventManager.addEventListener(gameEventType, eventListener);
    }

    /**
     * Removes an event listener for a specific game event type.
     * @param gameEventType the type of game event
     * @param eventListener the event listener to remove
     */
    @Override
    public synchronized void removeEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.eventManager.removeEventListener(gameEventType, eventListener);
    }

    /**
     * Triggers a game event.
     * @param gameEvent the game event to trigger
     */
    @Override
    public synchronized void triggerEvent(GameEvent gameEvent) {
        this.eventManager.triggerEvent(gameEvent);
    }

    /**
     * @return true if the game has ended, false otherwise
     */
    public synchronized boolean isEndGame() {
        return endGame;
    }

    /**
     * Sets the end game status.
     * @param endGame true to mark the game as finished, false otherwise
     */
    public synchronized void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    /**
     * @return true if the current round is the final round of the game, false otherwise
     */
    public synchronized boolean isFinalRound() {
        return finalRound;
    }

    /**
     * Sets the final round status.
     * @param finalRound true if this should be the final round, false otherwise
     */
    public synchronized void setFinalRound(boolean finalRound) {
        this.finalRound = finalRound;
    }

    /**
     * @return the array of revealed resource cards
     */
    public synchronized ResourceCard[] getRevealedResources() {
        return revealedResources;
    }

    /**
     * @return the array of revealed gold cards
     */
    public synchronized GoldCard[] getRevealedGolds() {
        return revealedGolds;
    }

    /**
     * @return true if there are no revealed resource cards to draw
     */
    public synchronized boolean emptyRevealedResources() {
        for (ResourceCard res : this.revealedResources)
            if (res != null) return false;
        return true;
    }

    /**
     * @return true if there are no revealed gold cards to draw
     */
    public synchronized boolean emptyRevealedGolds() {
        for (GoldCard gold : this.revealedGolds)
            if (gold != null) return false;
        return true;
    }

    /**
     * @return true if there are cards availabe to draw (both resource and gold decks are empty,
     * and no remaining revealed cards)
     */
    public synchronized boolean drawAreaEmpty() {
        return this.emptyRevealedResources() && this.emptyRevealedGolds()
                && this.resourceGameDeck.isEmpty() && this.goldGameDeck.isEmpty();
    }

    /**
     * @return true if the game is paused, false otherwise
     */
    public synchronized boolean isPaused() {
        return paused;
    }

    /**
     * Sets the paused status of the game.
     * @param paused true to pause the game, false otherwise
     */
    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * @return true if all players are deadlocked, false otherwise
     */
    public synchronized boolean allPlayersDeadlocked() {
        for (Player p : this.players)
            if (!p.getBoard().isDeadlocked()) return false;
        return true;
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.eventManager = new EventManager();
    }
}
