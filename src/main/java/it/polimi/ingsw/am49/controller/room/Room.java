package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.CompletePlayerInfo;
import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.controller.gameupdates.*;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.server.exceptions.RoomException;

import java.util.*;
import java.util.stream.Collectors;

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
    private Timer pauseTimer;
    private final HashMap<String, PlayerInfo> usernamesToPlayers;
    private Game game;
    private boolean gameStarted;

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

    public CompleteGameInfo reconnect(ClientHandler playerClient, String playerUsername) throws JoinRoomException {
        if (this.game == null)
            throw new JoinRoomException("Cannot reconnect to a game that hasn't started.");

        boolean success = this.game.reconnectPlayer(playerUsername);
        if (!success) throw new JoinRoomException("Invalid username. Could not reconnect to the game.");

        PlayerInfo pInfo = new PlayerInfo(playerUsername, playerClient);
        this.usernamesToPlayers.put(playerUsername, pInfo);
        this.currentPlayers++;

        pInfo.setVirtualView(new VirtualView(this.game, pInfo.getClient(), pInfo.getUsername()));

        if (currentPlayers > 1 && this.game != null && this.game.isPaused()) {
            this.game.setPaused(false);
            this.stopPauseTimer();
        }

        return this.getCompleteGameInfo(playerUsername);
    }

    public boolean removePlayer(ClientHandler client) {
        String username = this.getUsernameByClient(client);
        PlayerInfo playerInfo = this.usernamesToPlayers.remove(username);
        if (playerInfo != null) {
            this.currentPlayers--;

            if (this.gameStarted)
                this.removePlayerFromGame(playerInfo);

            this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                    .filter(c -> !c.equals(client) )
                    .forEach(c -> {
                        c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' left your room.");
                    });

            return true;
        }
        return false;
    }

    private void removePlayerFromGame(PlayerInfo playerInfo) {
        playerInfo.getVirtualView().destroy();
        this.game.disconnectPlayer(playerInfo.getUsername());

        if (currentPlayers == 1) {
            System.out.println("Starting timer!");
            this.game.setPaused(true);
            this.startPauseTimer();
        }
    }

    private void startPauseTimer() {
        this.pauseTimer = new Timer();
        this.pauseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                game.forfeitWinner(usernamesToPlayers.keySet().iterator().next());
            }
        }, 1000 * 20); // 60 seconds
    }

    private void stopPauseTimer() {
        if (this.pauseTimer != null) {
            this.pauseTimer.cancel();
            this.pauseTimer = null;
        }
    }

    /**
     * The Server will call this method to communicate the room that the specified client is ready to play the game
     * with the chosen color
     * @param client the client readying up
     * @param color the chosen color of the client
     * @throws RoomException if something goes wrong
     */
    public void clientReady(ClientHandler client, Color color) throws RoomException {

        String username = this.getUsernameByClient(client);
        if (username == null)
            throw new RoomException("Client is not in the room.");

        if(!this.isColorAvailale(color))
            throw new RoomException("Color " + color + " is not available.");

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

    public void clientNoMoreReady(ClientHandler client) throws RoomException {
        String username = this.getUsernameByClient(client);
        if (username == null)
            throw new RoomException("Client is not in the room.");

        this.usernamesToPlayers.get(username).setNullColor();
        this.usernamesToPlayers.get(username).setReadyToPlay(false);

        this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                .filter(c -> !c.equals(client))
                .forEach(c -> {
                    c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' is not ready.");
                });
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

    public void executeGameAction(ClientHandler client, GameAction action) throws InvalidActionException, NotYourTurnException {
        if (!this.usernameCorrespondsToClient(action.getUsername(), client)) {
            System.err.println("Username of action does not correspond to associated client."
                    + "\nExpected: " + this.getUsernameByClient(client)
                    + " - Received: " + action.getUsername());
            throw new InvalidActionException("Client and username don't correspond. Aborting.");
        }
        this.game.executeAction(action);
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
            throw new JoinRoomException("Game already started.");

        if (this.currentPlayers >= this.maxPlayers)
            throw new JoinRoomException("Room is full.");
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

    private CompleteGameInfo getCompleteGameInfo(String username) {
        LinkedList<Integer> commonObjectivesIds = Arrays.stream(this.game.getCommonObjectives()).map(Card::getId).collect(Collectors.toCollection(java.util.LinkedList::new));
        LinkedList<CompletePlayerInfo> players = this.game.getPlayers().stream()
                .map(player -> {
                    boolean hidden = !player.getUsername().equals(username);
                    return player.toCompletePlayerInfo(hidden);
                })
                .collect(Collectors.toCollection(LinkedList::new));
        DrawAreaUpdate drawAreaUpdate = new DrawAreaUpdate(
                this.game.getResourceGameDeck().size(),
                this.game.getGoldGameDeck().size(),
                this.game.getResourceGameDeck().peek().getResource(),
                this.game.getGoldGameDeck().peek().getResource(),
                Arrays.stream(this.game.getRevealedResources()).map(Card::getId).collect(Collectors.toCollection(LinkedList::new)),
                Arrays.stream(this.game.getRevealedGolds()).map(Card::getId).collect(Collectors.toCollection(LinkedList::new))
        );
        GameStateChangedUpdate gameStateUpdate = new GameStateChangedUpdate(
                this.game.getGameState().getType(),
                this.game.getTurn(),
                this.game.getRound(),
                this.game.getCurrentPlayer().getUsername());

        return new CompleteGameInfo(
                username,
                drawAreaUpdate,
                gameStateUpdate,
                commonObjectivesIds,
                players
        );
    }
}
