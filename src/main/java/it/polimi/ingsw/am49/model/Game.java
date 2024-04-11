package it.polimi.am49.cn_demo.model;

import it.polimi.am49.cn_demo.model.cards.placeables.GoldCard;
import it.polimi.am49.cn_demo.model.cards.objectives.ObjectiveCard;
import it.polimi.am49.cn_demo.model.cards.placeables.ResourceCard;
import it.polimi.am49.cn_demo.model.cards.placeables.StarterCard;
import it.polimi.am49.cn_demo.model.decks.DeckLoader;
import it.polimi.am49.cn_demo.model.decks.GameDeck;
import it.polimi.am49.cn_demo.model.enumerations.DrawPosition;
import it.polimi.am49.cn_demo.model.enumerations.GameState;
import it.polimi.am49.cn_demo.model.players.Player;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final int gameId;
    private int numPlayers;
    private int turn;
    private int round;
    private List<Player> players;
    private Player startingPlayer;
    private Player currentPlayer;
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
    }

    private void nextTurn() {
        if (endGame && finalRound && currentPlayer.equals(players.getLast())) this.gameState = GameState.END_GAME;

        if (this.gameState != GameState.END_GAME) {
            if (endGame && currentPlayer.equals(players.getLast())) finalRound = true;

            if (!endGame && currentPlayer.getPoints() >= 20) endGame = true;

            if (currentPlayer.equals(players.getLast())) {
                currentPlayer = players.getFirst();
                round++;
            } else currentPlayer = players.iterator().next();
        }
    }

    public void drawCard(Player player, DrawPosition fromWhere) throws Exception {
        if (this.gameState != GameState.DRAW_CARD) throw new Exception("You can't draw a card now");
        if (!player.equals(currentPlayer)) throw new Exception("Not your turn");

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

        this.gameState = GameState.PLACE_CARD;
    }

    public void placeCard() throws Exception {
        if (this.gameState != GameState.PLACE_CARD) throw new Exception("You can't place a card now");
        //da fare
        nextTurn();
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
