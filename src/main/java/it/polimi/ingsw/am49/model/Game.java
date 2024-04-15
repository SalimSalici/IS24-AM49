package it.polimi.ingsw.am49.model;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.cards.placeables.*;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.events.EventEmitter;
import it.polimi.ingsw.am49.model.events.EventListener;
import it.polimi.ingsw.am49.model.events.EventManager;
import it.polimi.ingsw.am49.model.events.GameEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.model.states.GameState;
import it.polimi.ingsw.am49.model.states.PregameState;

import java.io.Serializable;
import java.util.*;

/**
 * Main class for managing the state and flow of the game. This class encapsulates all the essential components
 * like players, decks, game state, and other dynamic elements that change throughout the game. It implements
 * Serializable for object serialization, supporting saving and loading game states, and EventEmitter for event
 * handling to manage game-related events dynamically.
 */
public class Game implements Serializable, EventEmitter {
    private final int gameId;
    private int numPlayers;
    private int turn;
    private int round;
    private final List<Player> players;
    private Player currentPlayer;
    private Player winner;
    private boolean endGame;
    private boolean finalRound;
    private final EventManager eventManager;
    private final ObjectiveCard[] commonObjectives;
    private GameState gameState;
    private ResourceCard[] drawableResources;
    private GoldCard[] drawableGolds;
    private final GameDeck<ResourceCard> resourceGameDeck;
    private final GameDeck<GoldCard> goldGameDeck;

    /**
     * Constructor of the Game class.
     * @param gameId is unique to each game
     * @param numPlayers number of players that are playing the current game
     */
    public Game(int gameId, int numPlayers) {
        this.gameId = gameId;
        this.numPlayers = numPlayers;
        this.eventManager = new EventManager();
        this.turn = 0;
        this.round = 0;
        this.players = new ArrayList<>();
        this.endGame = false;
        this.finalRound = false;
        this.commonObjectives = new ObjectiveCard[2]; // the common objectives are set in the relevant game state
        this.drawableResources = new ResourceCard[2];
        this.drawableGolds = new GoldCard[2];
        this.resourceGameDeck = DeckLoader.getInstance().getNewResourceDeck();
        this.goldGameDeck = DeckLoader.getInstance().getNewGoldDeck();

        for (int i = 0; i < drawableResources.length; i++)
            drawableResources[i] = resourceGameDeck.draw();

        for (int i = 0; i < drawableGolds.length; i++)
            drawableGolds[i] = goldGameDeck.draw();

        this.gameState = new PregameState(this, this.numPlayers);
        this.gameState.setUp();
    }

    private boolean areDecksEmpty(){
        return resourceGameDeck.isEmpty() && goldGameDeck.isEmpty();
    }

    /**
     * @param state sets the new state of the game
     */
    public void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * Retrieves a player by their username.
     *
     * @param username the username of the player to retrieve.
     * @return the player if found, null otherwise.
     */
    public Player getPlayerByUsername(String username) {
        for (Player p : this.players)
            if (p.getUsername().equals(username)) return p;
        return null;
    }

    /**
     * Executes a game action based on the received message.
     *
     * @param action the action received from a player.
     * @throws Exception if the action cannot be executed.
     */
    public void executeAction(GameAction action) throws Exception {
        this.gameState.execute(action);
    }

    /**
     * @return the game deck containing resource cards
     */
    public GameDeck<ResourceCard> getResourceGameDeck() {
        return this.resourceGameDeck;
    }

    /**
     * @return the game deck containing gold cards
     */
    public GameDeck<GoldCard> getGoldGameDeck() {
        return this.goldGameDeck;
    }

    /**
     * Increments the turn counter for the game.
     */
    public void incrementTurn() {
        this.turn++;
    }

    /**
     * Increments the round counter for the game.
     */
    public void incrementRound() {
        this.round++;
    }

    /**
     * @param player sets the player who is currently taking their turn
     */
    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Determines and returns the next player to take a turn.
     *
     * @return the next player in the turn order
     */
    public Player getNextPlayer() {
        if (this.currentPlayer.equals(this.players.getLast()))
            return this.players.getFirst();
        return players.get(players.indexOf(currentPlayer) + 1);
    }

    /**
     * @return the array of common objectives
     */
    public ObjectiveCard[] getCommonObjectives() {
        return this.commonObjectives;
    }

    /*
    private List<Player> calculateWinners() throws Exception{
        List<Player> winners = new ArrayList<>();
        if(this.gameStateType != GameStateType.END_GAME) throw new Exception("The game is not over yet");

        Map<Player, Integer> objectivesAchievedByPlayers = new HashMap<>();
        for(Player p : this.players){
            objectivesAchievedByPlayers.put(p, p.calculateFinalPoints(Arrays.asList(commonObjectives)));
        }

        int maxPoints = this.players.stream().mapToInt(Player::getPoints).max().orElse(0);
        List<Player> maxPointsPlayers = this.players.stream().filter(player -> player.getPoints() == maxPoints).toList();

        if (maxPointsPlayers.size() > 1) {
            for (Player player : this.players) {
                if (!maxPointsPlayers.contains(player)) objectivesAchievedByPlayers.remove(player);
            }
            int maxAchieved = maxPointsPlayers.stream()
                    .mapToInt(objectivesAchievedByPlayers::get)
                    .max()
                    .orElse(0);
            return maxPointsPlayers.stream().filter(player -> player.getPoints() == maxAchieved).toList();
        } else
            return maxPointsPlayers;
    }
    */

    /**
     * @return the unique identifier of the game
     */
    public int getGameId() {
        return gameId;
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
    public int getRound() {
        return round;
    }

    /**
     * @return the current turn number within the current round
     */
    public int getTurn() {
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
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return the list of all players currently in the game
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * @return the current state of the game
     */
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void addEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.eventManager.addEventListener(gameEventType, eventListener);
    }

    @Override
    public void removeEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.eventManager.removeEventListener(gameEventType, eventListener);
    }

    @Override
    public void triggerEvent(GameEvent gameEvent) {
        this.eventManager.triggerEvent(gameEvent);
    }

    /**
     * @return true if the game has ended, false otherwise
     */
    public boolean isEndGame() {
        return endGame;
    }

    /**
     * @param endGame sets true to mark the game as finished, false otherwise
     */
    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    /**
     * @return true if the current round is the final round of the game, false otherwise
     */
    public boolean isFinalRound() {
        return finalRound;
    }

    /**
     * @param finalRound sets true if this should be the final round, false otherwise
     */
    public void setFinalRound(boolean finalRound) {
        this.finalRound = finalRound;
    }
}
