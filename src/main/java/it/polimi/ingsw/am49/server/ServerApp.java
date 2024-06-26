package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.exceptions.*;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.common.Client;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.server.controller.room.RestoredRoom;
import it.polimi.ingsw.am49.server.controller.room.Room;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.server.model.Game;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Server application that handles room management and client interactions.
 */
public class ServerApp implements Server {

    List<Room> rooms;
    Map<ClientHandler, Room> clientsToRooms;

    /**
     * Constructs a new ServerApp instance initializing the rooms and client-room mappings.
     */
    public ServerApp() {
        this.rooms = new LinkedList<>();
        this.clientsToRooms = new HashMap<>();
    }

    /**
     * Fetches the list of available rooms.
     * @param client The client requesting the room list.
     * @return List of RoomInfo objects representing the available rooms.
     */
    @Override
    public List<RoomInfo> fetchRooms(Client client) {
        List<RoomInfo> roomInfos = new LinkedList<>();
        for (Room room : this.rooms)
            roomInfos.add(room.getRoomInfo());
        return roomInfos;
    }

    /**
     * Creates a new room with the specified parameters.
     * @param client The client creating the room.
     * @param roomName The name of the room.
     * @param numPlayers The number of players in the room.
     * @param creatorUsername The username of the room creator.
     * @return RoomInfo object representing the newly created room.
     * @throws AlreadyInRoomException If the client is already in another room.
     * @throws CreateRoomException If the room cannot be created due to invalid parameters or name conflicts.
     */
    @Override
    public synchronized RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws AlreadyInRoomException, CreateRoomException {

        if (roomName.length() < 2 || roomName.length() > 15)
            throw new CreateRoomException("Invalid room name. Room name should be between 2 and 15 charactes.");

        if (numPlayers < 1 || numPlayers > 4)
            throw new CreateRoomException("Invalid number of players. Must be between 2 and 4.");

        if (creatorUsername.length() < 2 || creatorUsername.length() > 15)
            throw new CreateRoomException("Invalid username. Your username should be between 2 and 15 charactes.");

        ClientHandler ch = this.getClientHandlerByClient(client);
        if (ch != null && this.clientsToRooms.containsKey(ch))
            throw new AlreadyInRoomException(this.clientsToRooms.get(ch).getRoomName());

        if (this.getRoomByName(roomName) != null)
            throw new CreateRoomException("The name of the room you are trying to create is not available, please " +
                    "choose a new room name.");

        ClientHandler clientHandler = new ClientHandler(client, this);
        Room room = new Room(roomName, numPlayers, clientHandler, creatorUsername, this);
        this.rooms.add(room);
        this.clientsToRooms.put(clientHandler, room);
        clientHandler.initializeHeartbeat();
        return room.getRoomInfo();
    }

    /**
     * Allows a client to join an existing room.
     * @param client The client joining the room.
     * @param roomName The name of the room to join.
     * @param username The username of the joining client.
     * @return RoomInfo object representing the room joined.
     * @throws AlreadyInRoomException If the client is already in another room.
     * @throws JoinRoomException If the room cannot be joined due to invalid parameters or if the room does not exist.
     * @throws GameAlreadyStartedException If the game in the room has already started.
     */
    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws AlreadyInRoomException, JoinRoomException, GameAlreadyStartedException {
        Room room = this.validateNewClientAndGetRoom(client, roomName, username);
        ClientHandler clientHandler = new ClientHandler(client, this);
        room.addNewPlayer(clientHandler, username);
        this.clientsToRooms.put(clientHandler, room);
        clientHandler.initializeHeartbeat();
        return room.getRoomInfo();
    }

    /**
     * Reconnects a client to their existing game session in a room.
     * @param client The client reconnecting.
     * @param roomName The name of the room.
     * @param username The username of the reconnecting client.
     * @return CompleteGameInfo object containing the state of the game the client is reconnecting to.
     * @throws AlreadyInRoomException If the client is already in another room.
     * @throws JoinRoomException If the room cannot be joined.
     */
    @Override
    public CompleteGameInfo reconnect(Client client, String roomName, String username) throws AlreadyInRoomException, JoinRoomException {
        Room room = this.validateNewClientAndGetRoom(client, roomName, username);
        ClientHandler clientHandler = new ClientHandler(client, this);
        CompleteGameInfo gameInfo = room.reconnect(clientHandler, username);
        this.clientsToRooms.put(clientHandler, room);
        clientHandler.initializeHeartbeat();
        return gameInfo;
    }

    /**
     * Marks a client as ready in their current room.
     * @param client The client marking as ready.
     * @param color The chosen color of the client.
     * @return RoomInfo object representing the updated room state.
     * @throws RoomException If the client is not in any room.
     */
    @Override
    public RoomInfo readyUp(Client client, Color color) throws RoomException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        if (room != null) room.clientReady(clientHandler, color);
        else throw new RoomException("You are not in a room.");
        return room.getRoomInfo();
    }

    /**
     * Marks a client as not ready in their current room.
     * @param client The client marking as not ready.
     * @return RoomInfo object representing the updated room state.
     * @throws RoomException If the client is not in any room.
     */
    @Override
    public RoomInfo readyDown(Client client) throws RoomException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        if (room != null) room.clientNoMoreReady(clientHandler);
        else throw new RoomException("You are not in a room.");
        return room.getRoomInfo();
    }

    /**
     * Allows a client to leave their current room.
     * @param client The client leaving the room.
     * @return true if the client successfully left the room, false otherwise.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public boolean leaveRoom(Client client) throws RemoteException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        if (clientHandler == null) return false;
        clientHandler.close();

        Room room = this.clientsToRooms.get(clientHandler);
        if (room == null) {
            this.clientsToRooms.remove(clientHandler);
            return false;
        }

        boolean roomLeft = room.removePlayer(clientHandler);
        if (roomLeft) {
            this.clientsToRooms.remove(clientHandler);
        }

        if (room.getCurrentPlayers() == 0 || room.isGameOver())
            this.destroyRoom(room);

        return roomLeft;
    }

    /**
     * Executes a game action for a client in their current room.
     * @param client The client performing the action.
     * @param action The game action to be executed.
     * @throws InvalidActionException If the action is invalid.
     * @throws NotYourTurnException If it is not the client's turn.
     * @throws NotInGameException If the client is not in a game.
     */
    @Override
    public void executeAction(Client client, GameAction action) throws InvalidActionException, NotYourTurnException, NotInGameException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        if (room != null) room.executeGameAction(clientHandler, action);
        else throw new NotInGameException();

        if (room.isGameOver()) this.destroyRoom(room);
    }

    /**
     * Processes a heartbeat signal from a client to keep their connection alive.
     * @param client The client sending the heartbeat.
     */
    @Override
    public void ping(Client client) {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        if (clientHandler != null)
            clientHandler.heartbeat();
    }

    /**
     * Destroys a room and cleans up associated resources.
     * @param room The room to be destroyed.
     */
    public void destroyRoom(Room room) {
        room.close();
        GameRestorer.deleteGame(room.getRoomName());
        this.clientsToRooms.forEach((key, value) -> { if (value.equals(room)) key.close(); });
        this.clientsToRooms.entrySet().removeIf(entry -> entry.getValue().equals(room));
        this.rooms.remove(room);
    }

    private Room getRoomByName(String roomName) {
        for (Room room : this.rooms)
            if (room.getRoomName().equals(roomName))
                return room;
        return null;
    }

    private ClientHandler getClientHandlerByClient(Client client) {
        for (ClientHandler clientHandler : this.clientsToRooms.keySet()) {
            if (clientHandler.getClient().equals(client))
                return clientHandler;
        }
        return null;
    }

    /**
     * Retrieves the host address of the client making the current request.
     * @return The client's host address.
     * @throws RemoteException If unable to retrieve the client's host address.
     */
    @Override
    public String getClientHostAddress() throws RemoteException {
        try {
            return UnicastRemoteObject.getClientHost();
        } catch (ServerNotActiveException e) {
            Log.getLogger().severe("Couldn't get client host address.");
            return null;
        }
    }

    /**
     * Processes a chat message sent by a client in a room.
     * @param client The client sending the message.
     * @param msg The chat message being sent.
     * @throws RemoteException If an RMI error occurs.
     */
    @Override
    public void chatMessage(Client client, ChatMSG msg) throws RemoteException { //TODO: create custom exeption
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        if (room != null) room.newChatMSG(msg);
        // TODO: throw exception
    }

    private Room validateNewClientAndGetRoom(Client client, String roomName, String username) throws AlreadyInRoomException, JoinRoomException {
        if (username == null)
            throw new JoinRoomException("Username is null.");

        if (username.length() < 2 || username.length() > 15)
            throw new JoinRoomException("Invalid username. Your username should be between 2 and 15 characters.");

        ClientHandler clientHandler = this.getClientHandlerByClient(client);

        if (clientHandler != null)
            throw new AlreadyInRoomException(this.clientsToRooms.get(clientHandler).getRoomName());

        Room room = this.getRoomByName(roomName);
        if (room == null)
            throw new JoinRoomException("The room you tried to join doesn't exist.");

        return room;
    }

    private void restoreGames() {
        Map<String, Game> games = GameRestorer.loadAllGames();
        games.forEach((roomName, game) -> {
            this.rooms.add(new RestoredRoom(game, roomName, this));
            System.out.println("Room restored: " + roomName);
        });
    }

    public static void main(String[] args) throws IOException, AlreadyBoundException {

        String host = getHostFromArgs(args);
        if (host == null) {
            System.out.println("Missing host address. Terminating.");
            System.exit(1);
            return;
        }

        int rmiPort;
        int socketPort;
        try {
            rmiPort = getRMIPortFromArgs(args);
            socketPort = getSocketPortFromArgs(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return;
        }

        try {
            System.setProperty("java.rmi.server.hostname", host);
            Log.initializeLogger("server.log", true);
            ServerApp server = new ServerApp();
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.bind("server.am49.codex_naturalis", UnicastRemoteObject.exportObject(server, rmiPort));
            Log.getLogger().info("RMI Server started on port " + rmiPort);
            new ServerSocketManager(server, socketPort);
            Log.getLogger().info("Socket Server started on port " + socketPort);

            ServerConfig.persistence = Arrays.asList(args).contains("--persistence");
            if (ServerConfig.persistence) server.restoreGames();
        } catch (Exception e) {
            System.out.println("Could not initialize server: " + e.getMessage());
            System.out.println("Terminating.");
            System.exit(1);
        }
    }

    public static String getHostFromArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equals("--host") || args[i].equals("--h"))
                return args[i+1];
        return null;
    }

    public static int getRMIPortFromArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equals("--r"))
                return Integer.parseInt(args[i+1]);
        throw new NumberFormatException("Missing RMI port.");
    }

    public static int getSocketPortFromArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equals("--s"))
                return Integer.parseInt(args[i+1]);
        throw new NumberFormatException("Missing socket port.");
    }
}
