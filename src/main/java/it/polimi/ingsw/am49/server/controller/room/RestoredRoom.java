package it.polimi.ingsw.am49.server.controller.room;

import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.server.ServerApp;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.server.model.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a room that has been restored following a server crash.
 */
public class RestoredRoom extends Room {

    private boolean inRestoration = true;

    private final List<PlayerInfo> returingPlayers;

    /**
     * Constructor for the Room class.
     *
     * @param roomName        the name of the room
     * @param server          the server application
     */
    public RestoredRoom(Game game, String roomName, ServerApp server) {
        super(roomName, server, game.getNumPlayers());
        this.game = game;
        this.gameStarted = true;
        this.returingPlayers = new LinkedList<>();

        for (Player p : this.game.getPlayers())
            p.setIsOnline(false);
    }

    @Override
    public synchronized void addNewPlayer(ClientHandler playerClient, String playerUsername) throws JoinRoomException, GameAlreadyStartedException {
        if (!this.inRestoration) {
            super.addNewPlayer(playerClient, playerUsername);
            return;
        }

        throw new GameAlreadyStartedException();
    }

    @Override
    public synchronized CompleteGameInfo reconnect(ClientHandler playerClient, String playerUsername) throws JoinRoomException {
        if (!this.inRestoration)
            return super.reconnect(playerClient, playerUsername);

        System.out.println(playerUsername + " attempt restore.");

        if (this.isClientAlreadyConnected(playerClient)) throw new JoinRoomException("Client is already connected to the room.");
        if (game.getPlayerByUsername(playerUsername) == null) throw new JoinRoomException("Username chosen is not in the game.");

        PlayerInfo pInfo = new PlayerInfo(playerUsername, playerClient);
        this.returingPlayers.add(pInfo);

        this.game.reconnectPlayer(playerUsername);

        if (this.returingPlayers.size() == this.maxPlayers)
            this.restartGame();

        return this.getCompleteGameInfo(playerUsername);
    }

    @Override
    public synchronized void executeGameAction(ClientHandler client, GameAction action) throws InvalidActionException, NotYourTurnException {
        if (!this.inRestoration) {
            super.executeGameAction(client, action);
            return;
        }

        throw new InvalidActionException("The game is being restored. You must wait until all players join back.");
    }

    @Override
    public synchronized boolean removePlayer(ClientHandler client) {
        if (!this.inRestoration)
            return super.removePlayer(client);

        for (PlayerInfo pInfo : this.returingPlayers)
            if (pInfo.getClient().equals(client)) {
                this.game.disconnectPlayer(pInfo.getUsername());
                this.returingPlayers.remove(pInfo);
                return true;
            }

        return false;
    }

    private void restartGame() throws JoinRoomException {
        for (Player p : this.game.getPlayers())
            p.setIsOnline(false);

        for (PlayerInfo p : this.returingPlayers) {
            super.reconnect(p.getClient(), p.getUsername());
        }
        this.currentPlayers = this.returingPlayers.size();
        this.inRestoration = false;
    }

    private boolean isClientAlreadyConnected(ClientHandler client) {
        for (PlayerInfo p : this.returingPlayers)
            if (p.getClient().equals(client)) return true;
        return false;
    }
}
