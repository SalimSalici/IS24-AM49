package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
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
    private int currentPlayers;
    private final HashMap<String, PlayerInfo> usernamesToPlayers;
    private Game game;
    private boolean gameStarted;

//    private GameEventsHandler gameEventsHandler;

    public Room(String roomName, int maxPlayers, ClientHandler creatorClient, String creatorUsername) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.usernamesToPlayers = new HashMap<>();
        this.usernamesToPlayers.put(creatorUsername, new PlayerInfo(creatorUsername, creatorClient));

        this.currentPlayers = 1;
        this.gameStarted = false;

        System.out.println("User '" + creatorUsername + "' created room '" + this.roomName
                + "' | maxPlayers: " + this.maxPlayers);
    }

    public void addNewPlayer(ClientHandler playerClient, String playerUsername) throws JoinRoomException {

        this.checkIfNewPlayerCanJoin(playerClient, playerUsername); // If not, JoinRoomException is thrown

        this.usernamesToPlayers.put(playerUsername, new PlayerInfo(playerUsername, playerClient));
        this.currentPlayers++;

        this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                .filter(c -> !c.equals(playerClient))
                .forEach(c -> {
                    c.roomUpdate(this.getRoomInfo(), "Player '" + playerUsername + "' joined your room.");
                });

        System.out.println("User '" + playerUsername + "' joined room '" + this.roomName
                + "' | currentPlayers: " + this.currentPlayers
                + " | maxPlayers: " + this.maxPlayers
        );
    }

    // TODO: maybe think more about this method (?)
    public boolean removePlayer(ClientHandler client) {
        String username = this.getUsernameByClient(client);
        PlayerInfo pInfo = this.usernamesToPlayers.remove(username);
        if (pInfo != null) {
            this.currentPlayers--;

            this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                    .filter(c -> !c.equals(client) )
                    .forEach(c -> {
                        c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' left your room.");
                    });

            return true;
        }
        return false;
    }

    /**
     * The Server will call this method to communicate the room that the specified client is ready to play the game
     * with the chosen color
     * @param client the client readying up
     * @param color the chosen color of the client
     * @throws Exception
     */
    public void clientReady(ClientHandler client, Color color) throws Exception{

        String username = this.getUsernameByClient(client);
        if (username == null)
            throw new Exception("Client is not in the room.");

        if(!this.isColorAvailale(color))
            throw new Exception("Color " + color + "is not available.");

        this.usernamesToPlayers.get(username).setColor(color);
        this.usernamesToPlayers.get(username).setReadyToPlay(true);

        this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                .filter(c -> !c.equals(client))
                .forEach(c -> {
                    c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' is ready.");
                });

        if (this.usernamesToPlayers.keySet().size() == maxPlayers && this.allPlayersReady()){
            this.startGame();
        }
    }

    private boolean allPlayersReady() {
        for (PlayerInfo pInfo : this.usernamesToPlayers.values())
            if (!pInfo.isReadyToPlay()) return false;
        return true;
    }

    private void startGame(){
        // TODO: remove gameId (?) (maybe substitute with gameName for resilience to server crash?) //
        this.game = new Game(0, maxPlayers);
        this.gameStarted = true;

        this.usernamesToPlayers.forEach((username, pInfo) -> {
            pInfo.setVirtualView(new VirtualView(this.game, pInfo.getClient(), username));
            Player newPlayer = new Player(username);
            newPlayer.setColor(pInfo.getColor());
            this.game.getPlayers().add(newPlayer);
        });

        this.game.startGame();
    }

    public void executeGameAction(ClientHandler client, GameAction action) throws Exception {
        if (!this.usernameCorrespondsToClient(action.getUsername(), client))
            throw new Exception("Username of action does not correspond to associated client."
                    + "\nExpected: " + this.getUsernameByClient(client)
                    + " - Received: " + action.getUsername());

        System.out.println("Player '" + action.getUsername() + "' executed action " + action.toString());
        this.game.executeAction(action);
    }

    public void onClientDisconnect(ClientHandler client, String username) throws Exception {
        if (!this.usernameCorrespondsToClient(username, client))
            throw new Exception("Username of action does not correspond to associated client");

        // TODO: communicate to controller in both cases when a game has already started or when a game is starting
    }

    /**
     * Checks if a client attempting to join the room is allowed to do so. Throws a {@link JoinRoomException} if not
     * This method is mainly used by {@link Room#addNewPlayer(ClientHandler, String)}
     * @param playerClient the client trying to join.
     * @param playerUsername the username that the client chose.
     * @throws JoinRoomException if the client cannot join the room
     */
    private void checkIfNewPlayerCanJoin(ClientHandler playerClient, String playerUsername) throws JoinRoomException {
        if (!this.isUsernameAvailable(playerUsername))
            throw new JoinRoomException("Username already taken. Please choose another username.");

        if (this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient).anyMatch(c -> c.equals(playerClient)))
            throw new JoinRoomException("Client is already in the room.");

        if (this.gameStarted)
            throw new JoinRoomException("Game already started");

        if (this.currentPlayers >= this.maxPlayers)
            throw new JoinRoomException("Max player amount for this room reached.");
    }

    private ClientHandler getClientByUsername(String username) {
        PlayerInfo pInfo = this.usernamesToPlayers.get(username);
        if (pInfo != null) return pInfo.getClient();
        return null;
    }

    private String getUsernameByClient(ClientHandler client) {
        for (Map.Entry<String, PlayerInfo> entry : this.usernamesToPlayers.entrySet()) {
            if (entry.getValue().getClient().equals(client)) return entry.getKey();
        }
        return null;
    }

    private boolean isUsernameAvailable(String username) {
        return !this.usernamesToPlayers.containsKey(username);
    }

    private boolean isColorAvailale(Color color) {
        for (PlayerInfo pInfo : this.usernamesToPlayers.values())
            if (pInfo.getColor() != null && pInfo.getColor().equals(color)) return false;
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean usernameCorrespondsToClient(String username, ClientHandler client) {
        // Important to use .equals() when comparing remote objects
        return client != null && client.equals(this.getClientByUsername(username));
    }

    public String getRoomName() {
        return roomName;
    }

    public RoomInfo getRoomInfo() {
        Map<String, Color> playersToColors = new HashMap<>();
        this.usernamesToPlayers.forEach((username, pInfo) -> {
            playersToColors.put(username, pInfo.getColor());
        });
        return new RoomInfo(this.roomName, this.maxPlayers, playersToColors);
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }
}
