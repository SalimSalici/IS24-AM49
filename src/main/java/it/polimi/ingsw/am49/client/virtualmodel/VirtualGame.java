package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VirtualGame {
    private final List<VirtualPlayer> players;
    private int round;
    private int turn;
    private GameStateType gameState;
    private VirtualPlayer currentPlayer;
    private static VirtualGame instance;

    private VirtualGame() {
        this.players = new ArrayList<>();
    }

    public VirtualGame newGame(Map<String, Color> players) {
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
//        switch (gameUpdate.getType()) {
//            case GAME_STARTED_UPDATE ->
//        }
    }
}
