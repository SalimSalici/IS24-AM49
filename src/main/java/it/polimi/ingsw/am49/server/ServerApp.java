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
    Map<Client, Room> clientsToRooms;

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
    public void fetchLobbies(Client client) {

    }

    @Override
    public RoomInfo createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws RemoteException, AlreadyInRoomException, CreateRoomException {

        if (roomName.length() < 2 || roomName.length() > 15)
            throw new IllegalArgumentException("Invalid room name. Room name should be between 2 and 15 charactes.");

        if (creatorUsername.length() < 2 || creatorUsername.length() > 15)
            throw new IllegalArgumentException("Invalid username. Your username should be between 2 and 15 charactes.");

        if (this.clientsToRooms.containsKey(client))
            throw new AlreadyInRoomException(this.clientsToRooms.get(client).getRoomName());

        if (this.getRoomByName(roomName) != null)
            throw new CreateRoomException("The name of the room you are trying to create is not available,please " +
                    "choose a new room name.");

        Room room = new Room(roomName, numPlayers, client, creatorUsername);
        this.rooms.add(room);
        this.clientsToRooms.put(client, room);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException {

        if (username == null)
            throw new JoinRoomException("Username is null.");

        if (username.length() < 2 || username.length() > 15)
            throw new JoinRoomException("Invalid username. Your username should be between 2 and 15 charactes.");

        if (this.clientsToRooms.containsKey(client))
            throw new AlreadyInRoomException(this.clientsToRooms.get(client).getRoomName());

        Room room = this.getRoomByName(roomName);
        if (room == null)
            throw new JoinRoomException("The room you tried to join doesn't exist.");

        room.addNewPlayer(client, username);
        this.clientsToRooms.put(client, room);
        return room.getRoomInfo();
    }

    @Override
    public RoomInfo readyUp(Client client, Color color) throws RemoteException {
        Room room = this.clientsToRooms.get(client);
        try {
            room.clientReady(client, color);
        } catch (Exception e) {
            // TODO: create appropriate exceptions
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return room.getRoomInfo();
    }

    @Override
    public boolean leaveRoom(Client client) throws RemoteException {
        Room room = this.clientsToRooms.get(client);
        if (room == null) return false;

        return room.removePlayer(client);
    }

    @Override
    public void executeAction(Client client, GameAction action) {
        Room room = this.clientsToRooms.get(client);
        try {
            if (room != null) room.executeGameAction(client, action);
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
