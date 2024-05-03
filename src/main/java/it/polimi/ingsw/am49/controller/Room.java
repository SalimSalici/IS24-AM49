package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.events.PlayerJoinedEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.util.BiMap;

import java.rmi.RemoteException;
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
    private final BiMap<Client, Color> clientsToColors;
    private Game game;
    private boolean gameStarted;

    private GameEventsHandler gameEventsHandler;

    public Room(String roomName, int maxPlayers, Client creatorClient, String creatorUsername) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.usernamesToClients = new BiMap<>();
        this.clientsToColors = new BiMap<>();

        this.gameStarted = false;

        this.usernamesToClients.put(creatorUsername, creatorClient);
        this.onlinePlayers = 1;
        this.currentPlayers = 1;

        System.out.println("User '" + creatorUsername + "' created room '" + this.roomName
                + "' | maxPlayers: " + this.maxPlayers);
    }

    public void addNewPlayer(Client playerClient, String playerUsername) throws JoinRoomException {

        this.checkIfNewPlayerCanJoin(playerClient, playerUsername); // If not, JoinRoomException is thrown

        this.usernamesToClients.put(playerUsername, playerClient);

        this.currentPlayers++;
        this.onlinePlayers++;

        if (this.currentPlayers >= this.maxPlayers)
            this.gameStarted = true;

        this.usernamesToClients.values().stream()
                .filter(c -> c != playerClient)
                .forEach(c -> {
                    try {
                        c.playerJoinedYourRoom(this.getRoomInfo(), playerUsername);
                    } catch (RemoteException e) {
                        // TODO: Handle this exception
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                    }
                });

        System.out.println("User '" + playerUsername + "' joined room '" + this.roomName
                + "' | currentPlayers: " + this.currentPlayers
                + " | onlinePlayers: " + this.onlinePlayers
                + " | maxPlayers: " + this.maxPlayers
        );
    }

    // TODO: maybe think more about this method (?)
    public boolean removePlayer(Client client) {
        Map.Entry<String, Client> entry = this.usernamesToClients.removeKey(this.getUsernameByClient(client));
        if (entry.getKey() != null && entry.getValue() != null) {
            this.currentPlayers--;
            this.onlinePlayers--;

            this.usernamesToClients.values().stream()
                    .filter(c -> c != client)
                    .forEach(c -> {
                        try {
                            c.playerLeftYourRoom(this.getRoomInfo(), entry.getKey());
                        } catch (RemoteException e) {
                            // TODO: Handle this exception
                            System.err.println(e.getMessage());
                            e.printStackTrace();
                        }
                    });

            return true;
        }
        return false;
    }

    public void chosenColor(Client client, Color color) throws Exception{
        if(clientsToColors.containsValue(color)){
            if(clientsToColors.getKey(color).equals(client))
                throw new Exception(color + "is alredy your choise");
            throw new Exception("Color " + color + "is not available");
        }

        //TODO : put returns the value that has been replaced, so it's possible to implemen a messge to notify that a color is available again
        this.clientsToColors.put( client, color);

        if(this.clientsToColors.keySet().size() == maxPlayers){
            this.startGame();


        }
    }

    private void startGame(){
        // TODO: remove gameId (?) (maybe substitute with gameName for resilience to server crash?) //
        this.game = new Game(0, maxPlayers);
        this.gameEventsHandler = new GameEventsHandler(this, game);
        this.gameStarted = true;

        for(Client c : clientsToColors.keySet()){
            this.joinGame(getUsernameByClient(c), clientsToColors.getValue(c));
        }

        this.game.startGame();

    }

    private void joinGame(String username, Color color){
        Player newPlayer = new Player(username);
        newPlayer.setColor(color);
        this.game.getPlayers().add(newPlayer);
        this.game.triggerEvent(new PlayerJoinedEvent(newPlayer, this.game.getPlayers()));
    }

    public void executeGameAction(Client client, GameAction action) throws Exception {
        if (!this.usernameCorrespondsToClient(action.getUsername(), client))
            throw new Exception("Username of action does not correspond to associated client."
                    + "\nExpected: " + this.getUsernameByClient(client)
                    + " - Received: " + action.getUsername());

        System.out.println("Player '" + action.getUsername() + "' executed action " + action.toString());
        this.game.executeAction(action);
    }

    public void onClientDisconnect(Client client, String username) throws Exception {
        if (!this.usernameCorrespondsToClient(username, client))
            throw new Exception("Username of action does not correspond to associated client");

        // TODO: communicate to controller in both cases when a game has already started or when a game is starting
    }

    // TODO: handle exception
    public void notifyGameUpdateTo(String username, GameUpdate gameUpdate) {
        try {
            this.usernamesToClients.getValue(username).receiveGameUpdate(gameUpdate);
        } catch (RemoteException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: handle exception
    public void broadcastGameUpdate(GameUpdate gameUpdate) {
        this.usernamesToClients.values().forEach(c -> {
            try {
                c.receiveGameUpdate(gameUpdate);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void broadcastGameUpdateExcept(GameUpdate gameUpdate, String except) {
        this.usernamesToClients.values().forEach(c -> {
            try {
                if (!getUsernameByClient(c).equals(except))
                    c.receiveGameUpdate(gameUpdate);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Checks if a client attempting to join the room is allowed to do so. Throws a {@link JoinRoomException} if not
     * This method is mainly used by {@link Room#addNewPlayer(Client, String)}
     * @param playerClient the client trying to join.
     * @param playerUsername the username that the client chose.
     * @throws JoinRoomException if the client cannot join the room
     */
    private void checkIfNewPlayerCanJoin(Client playerClient, String playerUsername) throws JoinRoomException {
        if (!this.isUsernameAvailable(playerUsername))
            throw new JoinRoomException("Username already taken. Please choose another username.");

        if (this.usernamesToClients.containsValue(playerClient))
            throw new JoinRoomException("Client is already in the room.");

        if (this.gameStarted)
            throw new JoinRoomException("Game already started");

        if (this.currentPlayers >= this.maxPlayers)
            throw new JoinRoomException("Max player amount for this room reached.");
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
        // Important to use .equals() when comparing remote objects
        return client != null && client.equals(this.usernamesToClients.getValue(username));
    }

    public String getRoomName() {
        return roomName;
    }

    public RoomInfo getRoomInfo() {
        return new RoomInfo(this.roomName, this.maxPlayers, this.usernamesToClients.keySet().stream().toList());
    }
}
