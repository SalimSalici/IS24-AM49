package it.polimi.ingsw.am49.server.controller.room;

import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.gameupdates.IsPlayingUpdate;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.server.ServerApp;
import it.polimi.ingsw.am49.server.ServerConfig;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.server.model.players.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a room that has been restored following a server crash.
 * The room can either be in restoration phase or not. If not in restoration phase, 
 * the overriden methods will fall back to the Room methods, otherwise they will
 * implement the methods to handle returning clients.
 */
public class RestoredRoom extends Room {

    private boolean inRestoration = true;

    private boolean shouldBeDestroyed = true;

    private final List<PlayerInfo> returingPlayers;

    /**
     * Constructs a RestoredRoom with the specified game, room name, and server.
     *
     * @param game the game associated with the room
     * @param roomName the name of the room
     * @param server the server managing the room
     */
    public RestoredRoom(Game game, String roomName, ServerApp server) {
        super(roomName, server, game.getNumPlayers());
        this.game = game;
        this.gameStarted = true;
        this.returingPlayers = new LinkedList<>();

        for (Player p : this.game.getPlayers())
            p.setIsOnline(false);

        new Thread(this::abortRestore).start();
    }

    /**
     * Adds a new player to the room.
     *
     * @param playerClient the client handler of the player
     * @param playerUsername the username of the player
     * @throws JoinRoomException if the player cannot join the room
     * @throws GameAlreadyStartedException if the game has already started
     */
    @Override
    public synchronized void addNewPlayer(ClientHandler playerClient, String playerUsername) throws JoinRoomException, GameAlreadyStartedException {
        if (!this.inRestoration) {
            super.addNewPlayer(playerClient, playerUsername);
            return;
        }

        throw new GameAlreadyStartedException();
    }

    /**
     * Reconnects a player to the room.
     *
     * @param playerClient the client handler of the player
     * @param playerUsername the username of the player
     * @return the complete game information
     * @throws JoinRoomException if the player cannot join the room
     */
    @Override
    public synchronized CompleteGameInfo reconnect(ClientHandler playerClient, String playerUsername) throws JoinRoomException {
        if (!this.inRestoration)
            return super.reconnect(playerClient, playerUsername);

        if (this.isClientAlreadyConnected(playerClient)) throw new JoinRoomException("Client is already connected to the room.");
        if (game.getPlayerByUsername(playerUsername) == null) throw new JoinRoomException("Username chosen is not in the game.");

        PlayerInfo pInfo = new PlayerInfo(playerUsername, playerClient);
        this.returingPlayers.add(pInfo);

        this.game.reconnectPlayer(playerUsername);

        for (PlayerInfo p : this.returingPlayers) {
            p.getClient().receiveGameUpdate(new IsPlayingUpdate(playerUsername, true));
        }

        if (this.returingPlayers.size() == this.maxPlayers)
            this.restartGame();

        this.shouldBeDestroyed = false;

        return this.getCompleteGameInfo(playerUsername);
    }

    /**
     * Executes a game action.
     *
     * @param client the client handler of the player
     * @param action the game action to be executed
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     */
    @Override
    public synchronized void executeGameAction(ClientHandler client, GameAction action) throws InvalidActionException, NotYourTurnException {
        if (!this.inRestoration) {
            super.executeGameAction(client, action);
            return;
        }

        throw new InvalidActionException("The game is being restored. You must wait until all players join back.");
    }

    /**
     * Removes a player from the room.
     *
     * @param client the client handler of the player
     * @return true if the player was removed, false otherwise
     */
    @Override
    public synchronized boolean removePlayer(ClientHandler client) {
        if (!this.inRestoration)
            return super.removePlayer(client);

        PlayerInfo playerInfo = this.getPlayerInfoFromClient(client);
        if (playerInfo == null)
            return false;

        this.game.getPlayerByUsername(playerInfo.getUsername()).setIsOnline(false);
        this.game.disconnectPlayer(playerInfo.getUsername());
        this.returingPlayers.remove(playerInfo);

        for (PlayerInfo p : this.returingPlayers)
            p.getClient().receiveGameUpdate(new IsPlayingUpdate(playerInfo.getUsername(), false));

        return true;
    }

    /**
     * Gets the current number of players in the room.
     *
     * @return the current number of players
     */
    @Override
    public synchronized int getCurrentPlayers() {
        if (!this.inRestoration)
            return super.getCurrentPlayers();
        else
            return this.returingPlayers.size();
    }

    /**
     * Gets the room information.
     *
     * @return the room information
     */
    @Override
    public synchronized RoomInfo getRoomInfo() {
        if (!this.inRestoration)
            return super.getRoomInfo();
        else {
            HashMap<String, Color> playersToColors = new HashMap<>();
            for (PlayerInfo pInfo : this.returingPlayers) {
                playersToColors.put(pInfo.getUsername(), this.game.getPlayerByUsername(pInfo.getUsername()).getColor());
            }
            return new RoomInfo(this.roomName, this.maxPlayers, playersToColors);
        }
    }

    /**
     * Restarts the game.
     *
     * @throws JoinRoomException if a player cannot join the room
     */
    private void restartGame() throws JoinRoomException {
        for (Player p : this.game.getPlayers())
            p.setIsOnline(false);

        for (PlayerInfo p : this.returingPlayers) {
            super.reconnect(p.getClient(), p.getUsername());
        }
        this.currentPlayers = this.returingPlayers.size();
        this.inRestoration = false;
    }

    /**
     * Checks if a client is already connected to the room.
     *
     * @param client the client handler of the player
     * @return true if the client is already connected, false otherwise
     */
    private boolean isClientAlreadyConnected(ClientHandler client) {
        for (PlayerInfo p : this.returingPlayers)
            if (p.getClient().equals(client)) return true;
        return false;
    }

    /**
     * Gets the player information from the client handler.
     *
     * @param client the client handler of the player
     * @return the player information, or null if not found
     */
    private PlayerInfo getPlayerInfoFromClient(ClientHandler client) {
        for (PlayerInfo playerInfo : this.returingPlayers)
            if (playerInfo.getClient().equals(client))
                return playerInfo;
        return null;
    }

    /**
     * Aborts the restoration process if the timeout is reached and nobody connected.
     */
    private void abortRestore() {
        try {
            Thread.sleep(ServerConfig.restoringRoomTimeout * 1000);
        } catch (InterruptedException e) {
            this.server.destroyRoom(this);
        }

        if (this.shouldBeDestroyed)
            this.server.destroyRoom(this);
    }
}
