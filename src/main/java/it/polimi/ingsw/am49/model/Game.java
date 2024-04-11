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

import java.lang.reflect.Executable;
import java.util.*;

public class Game {
    private final int gameId;
    private int numPlayers;
    private int turn; //TODO: si può togliere
    private int round;
    private List<Player> players;
    private Player startingPlayer;
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
        return Arrays.stream(drawableResources).toList().isEmpty() &&
                        Arrays.stream(drawableGolds).toList().isEmpty() &&
                        resourceGameDeck.isEmpty() &&
                        goldGameDeck.isEmpty();
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
    }

    public void chooseObjective(Player player, ObjectiveCard objective) throws Exception{
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");
        currentPlayer.setPersonalObjective(objective);
        if(player.equals(players.getLast())) this.gameState = GameState.PLACE_CARD;
    }

    private void nextTurn() throws Exception {
        if (endGame && finalRound && currentPlayer.equals(players.getLast())) {
            this.gameState = GameState.END_GAME;
            setFinalPoints();
            setWinner();
        }

        if (this.gameState != GameState.END_GAME) {
            if (endGame && currentPlayer.equals(players.getLast())) finalRound = true;

            if (!endGame && (currentPlayer.getPoints() >= 20 || areDecksEmpty())) endGame = true;

            if (currentPlayer.equals(players.getLast())) {
                currentPlayer = players.getFirst();
                round++;
            } else currentPlayer = players.iterator().next();
        }
    }

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
        nextTurn();
    }

    public void placeCard(Player player, PlaceableCard card, BoardTile boardTile, CornerPosition corner) throws Exception {
        if (this.gameState != GameState.PLACE_CARD) throw new Exception("You can't place a card now");
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");

        player.placeCard(card, boardTile, corner);

        this.gameState = GameState.DRAW_CARD;
    }

    private void setFinalPoints() throws Exception{
        if(this.gameState != GameState.END_GAME) throw new Exception("The game is not over yet");

        for(Player p : this.players){
            p.setFinalPoints(commonObjectives[0], commonObjectives[1]);
        }

        this.gameState = GameState.POINTS_CALCULATED;
    }

    private void setWinner() throws Exception{
        if (this.gameState != GameState.POINTS_CALCULATED) throw new Exception("The final points were not calculated");

        Optional<Player> winner = players.stream().max(Comparator.comparing(Player::getFinalPoints));

        if(winner.isPresent()){
            System.out.println("The winner is: " + winner.get());
            this.winner = winner.get();
        } else throw new Exception("It was not possible to find the winner");
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
        return startingPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
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
