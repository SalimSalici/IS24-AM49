package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.CompletePlayerInfo;
import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.controller.gameupdates.*;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.server.exceptions.*;
import it.polimi.ingsw.am49.util.Log;

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
    private final HashMap<String, PlayerInfo> usernamesToPlayers;
    private Game game;
    private boolean gameStarted;
    private Timer pauseTimer;

    /**
     * Constructor for the Room class.
     * @param roomName the name of the room
     * @param maxPlayers the maximum number of players allowed in the room
     * @param creatorClient the client handler of the room creator
     * @param creatorUsername the username of the room creator
     */
    public Room(String roomName, int maxPlayers, ClientHandler creatorClient, String creatorUsername) {
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.usernamesToPlayers = new HashMap<>();
        this.usernamesToPlayers.put(creatorUsername, new PlayerInfo(creatorUsername, creatorClient));

        this.currentPlayers = 1;
        this.gameStarted = false;
    }

    /**
     * Adds a new player to the room.
     * @param playerClient the client handler of the new player
     * @param playerUsername the username of the new player
     * @throws JoinRoomException if the player cannot join the room
     * @throws GameAlreadyStartedException if the game has already started
     */
    public synchronized void addNewPlayer(ClientHandler playerClient, String playerUsername) throws JoinRoomException, GameAlreadyStartedException {

        this.checkIfNewPlayerCanJoin(playerClient, playerUsername); // If not, JoinRoomException is thrown

        this.usernamesToPlayers.put(playerUsername, new PlayerInfo(playerUsername, playerClient));
        this.currentPlayers++;

        this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                .filter(c -> !c.equals(playerClient))
                .forEach(c -> c.roomUpdate(this.getRoomInfo(), "Player '" + playerUsername + "' joined your room."));
    }

    /**
     * Reconnects a player to the game.
     * @param playerClient the client handler of the player
     * @param playerUsername the username of the player
     * @return the complete game information for the player
     * @throws JoinRoomException if the player cannot reconnect to the game
     */
    public synchronized CompleteGameInfo reconnect(ClientHandler playerClient, String playerUsername) throws JoinRoomException {
        if (this.game == null)
            throw new JoinRoomException("Cannot reconnect to a game that hasn't started.");

        boolean success = this.game.reconnectPlayer(playerUsername);
        if (!success) throw new JoinRoomException("Invalid username. Could not reconnect to the game.");

        PlayerInfo pInfo = new PlayerInfo(playerUsername, playerClient);
        pInfo.setColor(this.game.getPlayerByUsername(playerUsername).getColor());
        this.usernamesToPlayers.put(playerUsername, pInfo);
        this.currentPlayers++;

        pInfo.setVirtualView(new VirtualView(this.game, pInfo.getClient(), pInfo.getUsername()));

        if (currentPlayers > 1 && this.game != null && this.game.isPaused()) {
            this.game.setPaused(false);
            this.stopPauseTimer();
        }

        return this.getCompleteGameInfo(playerUsername);
    }

    /**
     * Removes a player from the room.
     * @param client the client handler of the player to be removed
     * @return true if the player was successfully removed, false otherwise
     */
    public synchronized boolean removePlayer(ClientHandler client) {
        String username = this.getUsernameByClient(client);
        PlayerInfo playerInfo = this.usernamesToPlayers.remove(username);
        if (playerInfo != null) {
            this.currentPlayers--;

            if (this.gameStarted)
                this.removePlayerFromGame(playerInfo);

            this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                    .filter(c -> !c.equals(client) )
                    .forEach(c -> c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' left your room."));

            return true;
        }
        return false;
    }

    /**
     * Removes a player from the game.
     * @param playerInfo the player information of the player to be removed
     */
    private synchronized void removePlayerFromGame(PlayerInfo playerInfo) {
        playerInfo.getVirtualView().destroy();

        if (this.currentPlayers >= 1)
            this.game.disconnectPlayer(playerInfo.getUsername());

        if (currentPlayers == 1) {
            Log.getLogger().info("Starting game paused timer Room name: " + this.roomName);
            this.game.setPaused(true);
            this.startPauseTimer();
        }
    }

    /**
     * Starts the pause timer for the game.
     */
    private void startPauseTimer() {
        this.pauseTimer = new Timer();
        this.pauseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                game.forfeitWinner(usernamesToPlayers.keySet().iterator().next());
            }
        }, 1000 * 60); // 60 seconds
    }

    /**
     * Stops the pause timer for the game.
     */
    private void stopPauseTimer() {
        if (this.pauseTimer != null) {
            this.pauseTimer.cancel();
            this.pauseTimer = null;
            Log.getLogger().info("Stopping game paused timer. Room name: " + this.roomName);
        }
    }

    /**
     * The Server will call this method to communicate the room that the specified client is ready to play the game
     * with the chosen color
     * @param client the client readying up
     * @param color the chosen color of the client
     * @throws RoomException if something goes wrong
     */
    public synchronized void clientReady(ClientHandler client, Color color) throws RoomException {

        String username = this.getUsernameByClient(client);
        if (username == null)
            throw new RoomException("Client is not in the room.");

        if(!this.isColorAvailale(color))
            throw new RoomException("Color " + color + " is not available.");

        this.usernamesToPlayers.get(username).setColor(color);
        this.usernamesToPlayers.get(username).setReadyToPlay(true);

        this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                .filter(c -> !c.equals(client))
                .forEach(c -> c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' is ready."));

        if (this.usernamesToPlayers.keySet().size() == maxPlayers && this.allPlayersReady()){
            this.startGame();
        }
    }

    /**
     * Marks a client as no longer ready to play.
     * @param client the client handler of the player
     * @throws RoomException if something goes wrong
     */
    public synchronized void clientNoMoreReady(ClientHandler client) throws RoomException {
        String username = this.getUsernameByClient(client);
        if (username == null)
            throw new RoomException("Client is not in the room.");

        this.usernamesToPlayers.get(username).setNullColor();
        this.usernamesToPlayers.get(username).setReadyToPlay(false);

        this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient)
                .filter(c -> !c.equals(client))
                .forEach(c -> c.roomUpdate(this.getRoomInfo(), "Player '" + username + "' is no longer ready."));
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise
     */
    public synchronized boolean isGameOver() {
        return this.gameStarted && this.game != null && this.game.getGameState().getType() == GameStateType.END_GAME;
    }

    /**
     * Checks if all players are ready to play.
     * @return true if all players are ready, false otherwise
     */
    private synchronized boolean allPlayersReady() {
        for (PlayerInfo pInfo : this.usernamesToPlayers.values())
            if (!pInfo.isReadyToPlay()) return false;
        return true;
    }

    /**
     * Starts the game.
     */
    private synchronized void startGame(){
        this.game = new Game(maxPlayers);
        this.gameStarted = true;

        this.usernamesToPlayers.forEach((username, pInfo) -> {
            pInfo.setVirtualView(new VirtualView(this.game, pInfo.getClient(), username));
            Player newPlayer = new Player(username);
            newPlayer.setColor(pInfo.getColor());
            this.game.getPlayers().add(newPlayer);
        });

        this.game.startGame();
    }

    /**
     * Executes a game action.
     * @param client the client handler of the player
     * @param action the game action to be executed
     * @throws InvalidActionException if the action is invalid
     * @throws NotYourTurnException if it is not the player's turn
     */
    public synchronized void executeGameAction(ClientHandler client, GameAction action) throws InvalidActionException, NotYourTurnException {
        if (this.game == null)
            throw new InvalidActionException("Game hasn't started in this room");
        if (!this.usernameCorrespondsToClient(action.getUsername(), client))
            throw new InvalidActionException("Client and username don't correspond. Aborting.");
        this.game.executeAction(action);
    }

    /**
     * Checks if a client attempting to join the room is allowed to do so. Throws a {@link JoinRoomException} if not
     * This method is mainly used by {@link Room#addNewPlayer(ClientHandler, String)}
     * @param playerClient the client trying to join.
     * @param playerUsername the username that the client chose.
     * @throws JoinRoomException if the client cannot join the room
     */
    private synchronized void checkIfNewPlayerCanJoin(ClientHandler playerClient, String playerUsername) throws JoinRoomException, GameAlreadyStartedException {
        if (!this.isUsernameAvailable(playerUsername))
            throw new JoinRoomException("Username already taken. Please choose another username.");

        if (this.usernamesToPlayers.values().stream().map(PlayerInfo::getClient).anyMatch(c -> c.equals(playerClient)))
            throw new JoinRoomException("Client is already in the room.");

        if (this.gameStarted)
            throw new GameAlreadyStartedException();

        if (this.currentPlayers >= this.maxPlayers)
            throw new JoinRoomException("Room is full.");
    }

    /**
     * Gets the client handler by username.
     * @param username the username of the player
     * @return the client handler of the player
     */
    private synchronized ClientHandler getClientByUsername(String username) {
        PlayerInfo pInfo = this.usernamesToPlayers.get(username);
        if (pInfo != null) return pInfo.getClient();
        return null;
    }

    /**
     * Gets the username by client handler.
     * @param client the client handler of the player
     * @return the username of the player
     */
    private synchronized String getUsernameByClient(ClientHandler client) {
        for (Map.Entry<String, PlayerInfo> entry : this.usernamesToPlayers.entrySet()) {
            if (entry.getValue().getClient().equals(client)) return entry.getKey();
        }
        return null;
    }

    /**
     * Checks if a username is available.
     * @param username the username to check
     * @return true if the username is available, false otherwise
     */
    private synchronized boolean isUsernameAvailable(String username) {
        return !this.usernamesToPlayers.containsKey(username);
    }

    /**
     * Checks if a color is available.
     * @param color the color to check
     * @return true if the color is available, false otherwise
     */
    private synchronized boolean isColorAvailale(Color color) {
        for (PlayerInfo pInfo : this.usernamesToPlayers.values())
            if (pInfo.getColor() != null && pInfo.getColor().equals(color)) return false;
        return true;
    }

    /**
     * Checks if a username corresponds to a client handler.
     * @param username the username to check
     * @param client the client handler to check
     * @return true if the username corresponds to the client handler, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private synchronized boolean usernameCorrespondsToClient(String username, ClientHandler client) {
        // Important to use .equals() when comparing remote objects
        return client != null && client.equals(this.getClientByUsername(username));
    }

    /**
     * Gets the name of the room.
     * @return the name of the room
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Checks if the game has started.
     * @return true if the game has started, false otherwise
     */
    public synchronized boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Gets the room information.
     * @return the room information
     */
    public synchronized RoomInfo getRoomInfo() {
        Map<String, Color> playersToColors = new HashMap<>();
        this.usernamesToPlayers.forEach((username, pInfo) -> playersToColors.put(username, pInfo.getColor()));
        return new RoomInfo(this.roomName, this.maxPlayers, playersToColors);
    }

    /**
     * Gets the current number of players in the room.
     * @return the current number of players
     */
    public synchronized int getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * Closes the room.
     */
    public void close() {
        this.stopPauseTimer();
        for (PlayerInfo pInfo : this.usernamesToPlayers.values())
            pInfo.getVirtualView().destroy();
    }

    /**
     * Gets the complete game information for a player.
     * @param username the username of the player
     * @return the complete game information for the player
     */
    private synchronized CompleteGameInfo getCompleteGameInfo(String username) {
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
                this.game.getCurrentPlayer().getUsername(),
                this.game.getTurn(),
                this.game.getRound(),
                this.game.isEndGame(),
                this.game.isFinalRound()
        );

        return new CompleteGameInfo(
                username,
                drawAreaUpdate,
                gameStateUpdate,
                commonObjectivesIds,
                players
        );
    }
}
