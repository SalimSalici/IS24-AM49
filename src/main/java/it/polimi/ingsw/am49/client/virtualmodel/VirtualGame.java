package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.gameupdates.*;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.util.Observable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VirtualGame extends Observable {
    private final List<VirtualPlayer> players;
    private int round;
    private int turn;
    private boolean endGame;
    private boolean finalRound;
    private GameStateType gameState;
    private VirtualPlayer currentPlayer;
    private List<Integer> commonObjectives;
    private VirtualDrawable drawableArea;
    private VirtualGame() {
        this.players = new ArrayList<>();
    }

    public static VirtualGame newGame(GameStartedUpdate update) {
        Map<String, Color> players = update.playersToColors();
        VirtualGame game = new VirtualGame();
        players.forEach((username, color) -> game.players.add(new VirtualPlayer(username, color)));
        game.handleGameStartedUpdate(update);
        Log.getLogger().info("Creating new VirtualGame - " + update.toString());
        return game;
    }

     public static VirtualGame loadGame(CompleteGameInfo gameInfo) {
        VirtualGame game = new VirtualGame();
        gameInfo.players().forEach(completePlayerInfo -> {
            VirtualPlayer player = new VirtualPlayer(completePlayerInfo.username(), completePlayerInfo.color());
            player.setPoints(completePlayerInfo.points());
            player.setPersonalObjectiveId(completePlayerInfo.personalObjectiveId());
            player.setHand(completePlayerInfo.hand().handIds());
            player.setHiddenHand(completePlayerInfo.hiddenHand().hiddenHand());
            player.setActiveSymbols(completePlayerInfo.activeSymbols());
            completePlayerInfo.tiles().forEach(
                    tile -> player.getBoard().placeCard(
                            new VirtualCard(tile.cardId(), tile.flipped()),
                            tile.row(),
                            tile.col()
                    )
            );
            game.players.add(player);
        });
        game.commonObjectives = gameInfo.commonObjectiveIds();
        game.drawableArea = new VirtualDrawable(
                gameInfo.drawArea().remainingResources(),
                gameInfo.drawArea().remainingGolds(),
                gameInfo.drawArea().deckTopResource(),
                gameInfo.drawArea().deckTopGold(),
                gameInfo.drawArea().revealedResources(),
                gameInfo.drawArea().revealedGolds()
        );
        game.handleGameStateUpdate(gameInfo.gameState());
        return game;
     }

    public List<VirtualPlayer> getPlayers() {
        return players;
    }

    public VirtualPlayer getPlayerByUsername(String username) {
        for (VirtualPlayer p : this.players)
            if (p.getUsername().equals(username)) return p;
        return null;
    }

    public void processGameUpdate(GameUpdate gameUpdate) {
        Log.getLogger().info("Received GameUpdate - " + gameUpdate.toString());
        switch (gameUpdate.getType()) {
            case GAME_STARTED_UPDATE -> this.handleGameStartedUpdate((GameStartedUpdate) gameUpdate);
            case GAME_STATE_UPDATE -> this.handleGameStateUpdate((GameStateChangedUpdate) gameUpdate);
            case CARD_PLACED_UPDATE -> this.handleCardPlacedUpdate((CardPlacedUpdate) gameUpdate);
            case HAND_UPDATE -> this.handleHandUpdate((HandUpdate) gameUpdate);
            case HIDDEN_HAND_UPDATE -> this.handleHiddenHandUpdate((HiddenHandUpdate) gameUpdate);
            case DRAW_AREA_UPDATE -> this.handleDrawAreaUpdate((DrawAreaUpdate) gameUpdate);
            case END_GAME_UPDATE -> this.handleEndGameUpdate((EndGameUpdate) gameUpdate);
        }
    }

    private void handleGameStateUpdate(GameStateChangedUpdate update) {
        this.gameState = update.gameStateType();
        this.turn = update.turn();
        this.round = update.round();
        this.endGame = update.endGame();
        this.finalRound = update.finalRound();
        this.currentPlayer = this.getPlayerByUsername(update.currentPlayer());
        this.notifyObservers();
    }

    public void handleCardPlacedUpdate(CardPlacedUpdate update) {
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        VirtualCard card = new VirtualCard(update.cardId(), update.flipped());
        VirtualBoard board = player.getBoard();
        board.placeCard(card, update.row(), update.col());
        player.setPoints(update.points());
        player.setActiveSymbols(update.activeSymbols());
        player.notifyObservers();
        board.notifyObservers();
    }

    private void handleHandUpdate(HandUpdate update) {
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        player.setHand(update.handIds());
        player.notifyObservers();
    }

    private void handleHiddenHandUpdate(HiddenHandUpdate update) {
        VirtualPlayer player = this.getPlayerByUsername(update.username());
        player.setHiddenHand(update.hiddenHand());
        player.notifyObservers();
    }

    private void handleGameStartedUpdate(GameStartedUpdate update){
        this.drawableArea = new VirtualDrawable(update.remainingResources(), update.remainingGolds(), update.deckTopResource(), update.deckTopGold(), update.revealedResourcesIds(), update.revealedGoldsIds());
        this.commonObjectives = update.commonObjectivesIds();
        this.notifyObservers();
        //this.drawableArea.notifyObservers();
    }

    private void handleDrawAreaUpdate(DrawAreaUpdate update){
        this.drawableArea.setRemainingResources(update.remainingResources());
        this.drawableArea.setRemainingGolds(update.remainingGolds());
        this.drawableArea.setDeckTopResource(update.deckTopResource());
        this.drawableArea.setDeckTopGold(update.deckTopGold());
        this.drawableArea.setRevealedResourcesIds(update.revealedResources());
        this.drawableArea.setRevealedGoldsIds(update.revealedGolds());
        this.drawableArea.notifyObservers();
    }

    private void handleEndGameUpdate(EndGameUpdate update){
        this.gameState = GameStateType.END_GAME;
        for (Map.Entry<String, Integer[]> entry : update.playerToPoints().entrySet()) {
            String username = entry.getKey();
            Integer[] pointsAndObjectives = entry.getValue();
            VirtualPlayer player = this.getPlayerFromUsername(username);

            if (player == null) {
                Log.getLogger().severe("Received invalid EndGameUpdate: player username mismatch");
                continue;
            }

            if (pointsAndObjectives.length < 2) {
                Log.getLogger().severe("Received invalid EndGameUpdate: missing points and/or completed objectives");
                player.setPoints(0);
                player.setCompletedObjectives(0);
            } else {
                player.setPoints(pointsAndObjectives[0]);
                player.setCompletedObjectives(pointsAndObjectives[1]);
            }
        }
        this.notifyObservers();
    }

    public int getRound() {
        return round;
    }
    public int getTurn() {
        return turn;
    }
    public boolean getEndGame() {
        return endGame;
    }
    public boolean getFinalRound() {
        return finalRound;
    }
    public GameStateType getGameState() {
        return gameState;
    }

    private VirtualPlayer getPlayerFromUsername(String username) {
        for (int i = 0; i < this.players.size(); i++)
            if (this.players.get(i).getUsername().equals(username))
                return this.players.get(i);
        return null;
    }

    public VirtualPlayer getCurrentPlayer() { return currentPlayer; }
    public List<Integer> getCommonObjectives() { return commonObjectives; }
    public VirtualDrawable getDrawableArea() { return drawableArea; }

    public List<VirtualPlayer> getRanking() {
        return this.players.stream()
                .sorted((p1, p2) -> {
                    if (p1.getPoints() > p2.getPoints())
                        return -1;
                    else if (p1.getPoints() < p2.getPoints())
                        return 1;
                    else if (p1.getCompletedObjectives() > p2.getCompletedObjectives())
                        return -1;
                    else if (p1.getCompletedObjectives() < p2.getCompletedObjectives())
                        return 1;
                    return 0;
                })
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
