package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.room.Room;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.exceptions.*;
import it.polimi.ingsw.am49.util.Log;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerApp implements Server {

    List<Room> rooms;
    Map<ClientHandler, Room> clientsToRooms;

    //TODO: remove this so that at the same username can be used for different rooms
    Set<String> usernamesTaken;

    public ServerApp() {
        this.rooms = new LinkedList<>();
        this.clientsToRooms = new HashMap<>();
        this.usernamesTaken = new HashSet<>();
    }

    @Override
    public List<RoomInfo> fetchRooms(Client client) {
        List<RoomInfo> roomInfos = new LinkedList<>();
        for (Room room : this.rooms) {
            roomInfos.add(room.getRoomInfo());
        }
        return roomInfos;
    }

    @Override
    public RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws AlreadyInRoomException, CreateRoomException {

        if (roomName.length() < 2 || roomName.length() > 15)
            throw new CreateRoomException("Invalid room name. Room name should be between 2 and 15 charactes.");

        if (numPlayers < 2 || numPlayers > 4)
            throw new CreateRoomException("Invalid number of players. Must be between 2 and 4.");

        if (creatorUsername.length() < 2 || creatorUsername.length() > 15)
            throw new CreateRoomException("Invalid username. Your username should be between 2 and 15 charactes.");

        if (this.clientsToRooms.containsKey(client))
            throw new AlreadyInRoomException(this.clientsToRooms.get(client).getRoomName());

        if (this.getRoomByName(roomName) != null)
            throw new CreateRoomException("The name of the room you are trying to create is not available, please " +
                    "choose a new room name.");

        ClientHandler clientHandler = new ClientHandler(client);
        Room room = new Room(roomName, numPlayers, clientHandler, creatorUsername);
        this.rooms.add(room);
        this.clientsToRooms.put(clientHandler, room);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws AlreadyInRoomException, JoinRoomException {

//        if (username == null)
//            throw new JoinRoomException("Username is null.");
//
//        if (username.length() < 2 || username.length() > 15)
//            throw new JoinRoomException("Invalid username. Your username should be between 2 and 15 charactes.");
//
//        ClientHandler clientHandler = this.getClientHandlerByClient(client);
//
//        if (clientHandler != null)
//            throw new AlreadyInRoomException(this.clientsToRooms.get(clientHandler).getRoomName());
//
//        Room room = this.getRoomByName(roomName);
//        if (room == null)
//            throw new JoinRoomException("The room you tried to join doesn't exist.");
//
//        clientHandler = new ClientHandler(client);
//        room.addNewPlayer(clientHandler, username);
//        this.clientsToRooms.put(clientHandler, room);
//        return room.getRoomInfo();

        Room room = this.validateNewClientAndGetRoom(client, roomName, username);
        ClientHandler clientHandler = new ClientHandler(client);
        room.addNewPlayer(clientHandler, username);
        this.clientsToRooms.put(clientHandler, room);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo readyUp(Client client, Color color) throws RoomException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        room.clientReady(clientHandler, color);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo readyDown(Client client) throws RoomException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        room.clientNoMoreReady(clientHandler);
        return room.getRoomInfo();
    }

    // TODO: rename this to "leave(...)", as it is used to leave the room and possibly also the game if it started
    @Override
    public boolean leaveRoom(Client client) throws RemoteException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        if (room == null) return false;

        boolean roomLeft = room.removePlayer(clientHandler);
        if (roomLeft) {
            this.clientsToRooms.remove(clientHandler);
        }

        if (room.getCurrentPlayers() == 0)
            this.rooms.remove(room);

        return roomLeft;
    }

    @Override
    public void executeAction(Client client, GameAction action) throws InvalidActionException, NotYourTurnException, NotInGameException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        if (room != null) room.executeGameAction(clientHandler, action);
        else throw new NotInGameException();
    }

    @Override
    public CompleteGameInfo reconnect(Client client, String roomName, String username) throws AlreadyInRoomException, JoinRoomException {
        Room room = this.validateNewClientAndGetRoom(client, roomName, username);
        ClientHandler clientHandler = new ClientHandler(client);
        CompleteGameInfo gameInfo = room.reconnect(clientHandler, username);
        this.clientsToRooms.put(clientHandler, room);
        return gameInfo;
    }

    @Override
    public void ping(Client client) {

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

    @Override
    public String getClientHostAddress() throws RemoteException {
        try {
            System.out.println(UnicastRemoteObject.getClientHost());
            return UnicastRemoteObject.getClientHost();
        } catch (ServerNotActiveException e) {
            Log.getLogger().severe("Couldn't get client host address.");
            return null;
        }
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

    public static void main(String[] args) throws IOException, AlreadyBoundException {
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
//        System.setProperty("java.rmi.server.hostname", "10.147.20.145");

        Log.initializeLogger("server.log", true);

        int port = 8458;
        Server server = new ServerApp();
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind("server.am49.codex_naturalis", UnicastRemoteObject.exportObject(server, port));
        Log.getLogger().info("RMI Server started on port " + port);
        ServerSocketManager serverSocketManager = new ServerSocketManager(server, port + 1);
        Log.getLogger().info("Socket Server started on port " + (port + 1));
    }
}
