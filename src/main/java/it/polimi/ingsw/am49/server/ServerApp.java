package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.room.Room;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.exceptions.*;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    public void disconnectClient(Client client) {

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
            throws RemoteException, AlreadyInRoomException, CreateRoomException {

        if (roomName.length() < 2 || roomName.length() > 15)
            throw new CreateRoomException("Invalid room name. Room name should be between 2 and 15 charactes.");

        if (numPlayers < 2 || numPlayers > 4)
            throw new CreateRoomException("Invalid number of players. Must be between 2 and 4.");

        if (creatorUsername.length() < 2 || creatorUsername.length() > 15)
            throw new CreateRoomException("Invalid username. Your username should be between 2 and 15 charactes.");

        if (this.clientsToRooms.containsKey(client))
            throw new AlreadyInRoomException(this.clientsToRooms.get(client).getRoomName());

        if (this.getRoomByName(roomName) != null)
            throw new CreateRoomException("The name of the room you are trying to create is not available,please " +
                    "choose a new room name.");

        ClientHandler clientHandler = new ClientHandler(client);
        Room room = new Room(roomName, numPlayers, clientHandler, creatorUsername);
        this.rooms.add(room);
        this.clientsToRooms.put(clientHandler, room);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException {

        if (username == null)
            throw new JoinRoomException("Username is null.");

        if (username.length() < 2 || username.length() > 15)
            throw new JoinRoomException("Invalid username. Your username should be between 2 and 15 charactes.");

        ClientHandler clientHandler = this.getClientHandlerByClient(client);

        if (clientHandler != null)
            throw new AlreadyInRoomException(this.clientsToRooms.get(clientHandler).getRoomName());

        Room room = this.getRoomByName(roomName);
        if (room == null)
            throw new JoinRoomException("The room you tried to join doesn't exist.");

        clientHandler = new ClientHandler(client);
        room.addNewPlayer(clientHandler, username);
        this.clientsToRooms.put(clientHandler, room);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo readyUp(Client client, Color color) throws RemoteException {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        try {
            room.clientReady(clientHandler, color);
        } catch (Exception e) {
            // TODO: create appropriate exceptions
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return room.getRoomInfo();
    }

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
    public void executeAction(Client client, GameAction action) {
        ClientHandler clientHandler = this.getClientHandlerByClient(client);
        Room room = this.clientsToRooms.get(clientHandler);
        try {
            if (room != null) room.executeGameAction(clientHandler, action);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void reconnect(Client client, String gameName) {

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

    public static void main(String[] args) throws IOException, AlreadyBoundException {
        int port = 8458;
        Server server = new ServerApp();
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind("server.am49.codex_naturalis", UnicastRemoteObject.exportObject(server, port));
        System.out.println("RMI Server started on port " + port);
        ServerSocketManager serverSocketManager = new ServerSocketManager(server, port + 1);
        System.out.println("Socket Server started on port " + (port + 1));
    }
}
