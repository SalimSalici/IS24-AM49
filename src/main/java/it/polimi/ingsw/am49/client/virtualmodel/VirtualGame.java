package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.controller.gameupdates.*;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.util.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VirtualGame extends Observable {
    private final List<VirtualPlayer> players;
    private int round;
    private int turn;
    private GameStateType gameState;
    private VirtualPlayer currentPlayer;

    private VirtualGame() {
        this.players = new ArrayList<>();
    }

    public static VirtualGame newGame(Map<String, Color> players) {
        VirtualGame game = new VirtualGame();
        players.forEach((username, color) -> game.players.add(new VirtualPlayer(username, color)));
        return game;
    }

    // TODO:
    // public VirtualGame loadGame(CompleteGameInfo gameInfo) { ... }

    public List<VirtualPlayer> getPlayers() {
        return players;
    }

    public VirtualPlayer getPlayerByUsername(String username) {
        for (VirtualPlayer p : this.players)
            if (p.getUsername().equals(username)) return p;
        return null;
    }

    public void processGameUpdate(GameUpdate gameUpdate) {
        switch (gameUpdate.getType()) {
            case GAME_STATE_UPDATE -> this.handleGameStateUpdate((GameStateChangedUpdate) gameUpdate);
            case CARD_PLACED_UPDATE -> this.handleCardPlacedUpdate((CardPlacedUpdate) gameUpdate);
            case HAND_UPDATE -> this.handleHandUpdate((HandUpdate) gameUpdate);
            case HIDDEN_HAND_UPDATE -> this.handleHiddenHandUpdate((HiddenHandUpdate) gameUpdate);
        }
    }

    private void handleGameStateUpdate(GameStateChangedUpdate update) {
        this.gameState = update.gameStateType();
        this.turn = update.turn();
        this.round = update.round();
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

    public int getRound() {
        return round;
    }

    public int getTurn() {
        return turn;
    }

    public GameStateType getGameState() {
        return gameState;
    }

    public VirtualPlayer getCurrentPlayer() {
        return currentPlayer;
    }
}
