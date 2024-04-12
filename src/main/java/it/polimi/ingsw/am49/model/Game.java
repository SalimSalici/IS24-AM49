package it.polimi.ingsw.am49.model;

import it.polimi.ingsw.am49.model.cards.placeables.*;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameState;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

import java.io.Serializable;
import java.lang.reflect.Executable;
import java.util.*;

public class Game implements Serializable {
    private final int gameId;
    private int numPlayers;
    private int turn; //TODO: si può togliere
    private int round;
    private List<Player> players;
    private Player currentPlayer;
    private Player winner;
    private boolean endGame;
    private boolean finalRound;
    private ObjectiveCard[] commonObjectives;
    private GameState gameState;
    private ResourceCard[] drawableResources;
    private GoldCard[] drawableGolds;
    private GameDeck<ResourceCard> resourceGameDeck;
    private GameDeck<GoldCard> goldGameDeck;

    public Game(int gameId) {
        this.gameId = gameId;
        this.numPlayers = 0;
        this.turn = 0;
        this.round = 0;
        this.players = new ArrayList<>();
        this.endGame = false;
        this.finalRound = false;
        this.commonObjectives = new ObjectiveCard[2];
        this.gameState = GameState.PREGAME;
        this.drawableResources = new ResourceCard[2];
        this.drawableGolds = new GoldCard[2];
        this.resourceGameDeck = DeckLoader.getInstance().getNewResourceDeck();
        this.goldGameDeck = DeckLoader.getInstance().getNewGoldDeck();

        for (int i = 0; i < drawableResources.length; i++)
            drawableResources[i] = resourceGameDeck.draw();

        for (int i = 0; i < drawableGolds.length; i++)
            drawableGolds[i] = goldGameDeck.draw();
    }

    private boolean areDecksEmpty(){
        return resourceGameDeck.isEmpty() && goldGameDeck.isEmpty();
    }

    public void addPlayer(Player player) throws Exception {
        if (this.gameState != GameState.PREGAME) throw new Exception("Pregame is over");
        if (numPlayers >= 4) throw new Exception("Max players reached");
        this.players.add(player);
        this.numPlayers++;
    }

    public void startGame() throws Exception {
        if (numPlayers < 2) throw new Exception("Not enough players");
        if (this.gameState != GameState.PREGAME) throw new Exception("Game already started");
        Collections.shuffle(this.players);
        this.currentPlayer = this.players.getFirst();

        GameDeck<StarterCard> starterDeck = DeckLoader.getInstance().getNewStarterDeck();
        for (Player p : this.players)
            p.setStarterCard(starterDeck.draw());

        this.gameState = GameState.CHOOSE_STARTER_SIDE;
    }

    public void chooseStarterSide(Player player, boolean flipped) throws Exception {
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");
        player.chooseStarterSide(flipped);
        if(player.equals(players.getLast())) this.gameState = GameState.CHOOSE_OBJECTIVE;
        this.nextTurn();
    }

    public void chooseObjective(Player player, ObjectiveCard objective) throws Exception{
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");
        currentPlayer.setPersonalObjective(objective);
        if(player.equals(players.getLast())) {
            this.gameState = GameState.PLACE_CARD;
            for (Player p : this.players)
                this.assignInitialHand(p);
        }
        this.nextTurn();
    }

    private void assignInitialHand(Player player) throws Exception {
        player.drawCard(resourceGameDeck.draw());
        player.drawCard(resourceGameDeck.draw());
        player.drawCard(goldGameDeck.draw());
    }

    private void nextTurn() throws Exception {
        if (endGame && finalRound && currentPlayer.equals(players.getLast())) {
            this.gameState = GameState.END_GAME;
            // TODO: calculateWinners should be called by the controller
            calculateWinners();
        }

        if (this.gameState != GameState.END_GAME) {
            if (endGame && currentPlayer.equals(players.getLast())) finalRound = true;

            if (!endGame && (currentPlayer.getPoints() >= 20 || areDecksEmpty())) endGame = true;

            if (currentPlayer.equals(players.getLast())) {
                currentPlayer = players.getFirst();
                round++;
            } else currentPlayer = players.get(players.indexOf(currentPlayer) + 1);
        }
    }

    // TODO: handle variable lenght of drawable revealed resource / gold cards
    // TODO: handle case in which some revealed resources are missing
    public void drawCard(Player player, DrawPosition fromWhere) throws Exception {
        if (this.gameState != GameState.DRAW_CARD) throw new Exception("You can't draw a card now");
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");

        if(!areDecksEmpty()){
            switch (fromWhere) {
                case RESOURCE_DECK -> player.drawCard(resourceGameDeck.draw());
                case GOLD_DECK -> player.drawCard(goldGameDeck.draw());
                case FIRST_RESOURCE -> {
                    player.drawCard(drawableResources[0]);
                    drawableResources[0] = resourceGameDeck.draw();
                }
                case SECOND_RESOURCE -> {
                    player.drawCard(drawableResources[1]);
                    drawableResources[1] = resourceGameDeck.draw();
                }
                case FIRST_GOLD -> {
                    player.drawCard(drawableGolds[0]);
                    drawableGolds[0] = goldGameDeck.draw();
                }
                case SECOND_GOLD -> {
                    player.drawCard(drawableGolds[1]);
                    drawableGolds[1] = goldGameDeck.draw();
                }
            }
        }

        this.gameState = GameState.PLACE_CARD;
        this.nextTurn();
    }

    public void placeCard(Player player, ColouredCard card, BoardTile boardTile, CornerPosition corner) throws Exception {
        if (this.gameState != GameState.PLACE_CARD) throw new Exception("You can't place a card now");
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");

        player.placeCard(card, boardTile, corner);

        this.gameState = GameState.DRAW_CARD;
    }

    private List<Player> calculateWinners() throws Exception{
        List<Player> winners = new ArrayList<>();
        if(this.gameState != GameState.END_GAME) throw new Exception("The game is not over yet");

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

    public int getGameId() {
        return gameId;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getRound() {
        return round;
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
}
