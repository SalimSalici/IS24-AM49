package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.util.BiMap;

import java.util.*;

public class Room {

    /**
     * Name of the room. This should be unique within the server.
     */
    private final String roomName;

    /**
     * Number of players of the game being played in this room. This number is not equal to the number of
     * connected clients (some clients may have crashed)
     */
    private final int maxPlayers;
    private int onlinePlayers;
    private int currentPlayers;
    private final BiMap<String, Client> usernamesToClients;
    private final Game game;
    private boolean gameStarted;

    public Room(String roomName, int maxPlayers, Client creatorClient, String creatorUsername) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.usernamesToClients = new BiMap<>();

        // TODO: remove gameId (?) (maybe substitute with gameName for resilience to server crash?)
        this.game = new Game(0, maxPlayers);
        this.gameStarted = false;
        this.usernamesToClients.put(creatorUsername, creatorClient);
        this.onlinePlayers = 1;
        this.currentPlayers = 1;

        System.out.println("User '" + creatorUsername + "' created room '" + this.roomName
                + "' | maxPlayers: " + this.maxPlayers);
    }

    public void addNewPlayer(Client playerClient, String playerUsername) throws JoinRoomException {
        if (this.gameStarted)
            throw new JoinRoomException("Game already started");

        if (this.currentPlayers >= this.maxPlayers)
            throw new JoinRoomException("Max player amount for this room reached");

        this.usernamesToClients.put(playerUsername, playerClient);

        this.currentPlayers++;
        this.onlinePlayers++;

        if (this.currentPlayers >= this.maxPlayers)
            this.gameStarted = true;

        System.out.println("User '" + playerUsername + "' joined room '" + this.roomName
                + "' | currentPlayers: " + this.currentPlayers
                + " | onlinePlayers: " + this.onlinePlayers
                + " | maxPlayers: " + this.maxPlayers
        );
    }

    public void executeGameAction(Client client, GameAction action) throws Exception {
        if (!this.usernameCorrespondsToClient(action.getUsername(), client))
            throw new Exception("Username of action does not correspond to associated client");

        this.game.executeAction(action);
    }

    public void onClientDisconnect(Client client, String username) throws Exception {
        if (!this.usernameCorrespondsToClient(username, client))
            throw new Exception("Username of action does not correspond to associated client");

        // TODO: communicate to controller in both cases when a game has already started or when a game is starting
    }

    private Client getClientByUsername(String username) {
        return this.usernamesToClients.getValue(username);
    }

    private String getUsernameByClient(Client client) {
        return this.usernamesToClients.getKey(client);
    }

    private boolean isUsernameAvailable(String username) {
        return !this.usernamesToClients.containsKey(username);
    }

    private boolean usernameCorrespondsToClient(String username, Client client) {
        return client != null && client == this.usernamesToClients.getValue(username);
    }

    public String getRoomName() {
        return roomName;
    }

    public RoomInfo getRoomInfo() {
        return new RoomInfo(this.roomName, this.maxPlayers, this.usernamesToClients.keySet().stream().toList());
    }
}
