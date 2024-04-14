package it.polimi.ingsw.am49.model;

import it.polimi.ingsw.am49.messages.mts.MessageToServer;
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

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public Player getPlayerByUsername(String username) {
        for (Player p : this.players)
            if (p.getUsername().equals(username)) return p;
        return null;
    }

    public void executeAction(MessageToServer msg) throws Exception {
        this.gameState.execute(msg);
    }

    public GameDeck<ResourceCard> getResourceGameDeck() {
        return this.resourceGameDeck;
    }

    public GameDeck<GoldCard> getGoldGameDeck() {
        return this.goldGameDeck;
    }

    public void incrementTurn() {
        this.turn++;
    }

    public void incrementRound() {
        this.round++;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public Player getNextPlayer() {
        if (this.currentPlayer.equals(this.players.getLast()))
            return this.players.getFirst();
        return players.get(players.indexOf(currentPlayer) + 1);
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

    public int getGameId() {
        return gameId;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getRound() {
        return round;
    }
    public int getTurn() {
        return turn;
    }

    public Player getStartingPlayer() {
        return this.players.getFirst();
    }

    public Player getLastPlayer() {
        return this.players.getLast();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public ObjectiveCard getFirstCommonObjective() {
        return commonObjectives[0];
    }

    public ObjectiveCard getSecondCommonObjective() {
        return commonObjectives[1];
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
}
