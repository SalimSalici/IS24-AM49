package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.controller.Room;
import it.polimi.ingsw.am49.controller.RoomInfo;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.InvalidUsernameException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;

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
    public boolean login(Client client, String username) throws RemoteException, InvalidUsernameException {

        if (username.length() < 2 || username.length() > 15)
            throw new InvalidUsernameException("Invalid username. Choose a username between 2 and 15 characters.");

        if (usernamesTaken.contains(username)) {
            System.out.println("Client with username '" + username + "' tried to join, but the username was not available");
            return false;
        }

        this.clientsToRooms.put(new ClientHandler(client, username), null);
        this.usernamesTaken.add(username);
        System.out.println("Client with username '" + username + "' joined");
        return true;
    }

    @Override
    public void logout(Client client, String username) {
        // TODO: if in a room, notify room that a client disconnected
        this.clientsToRooms.remove(client);
        this.usernamesTaken.remove(username);
        System.out.println("Client with username " + username + " disconnected");
    }

    @Override
    public void fetchLobbies(Client client) {

    }

    @Override
    public boolean createRoom(Client client, String roomName, int numPlayers, String creatorUsername)
            throws RemoteException, AlreadyInRoomException, IllegalArgumentException {

        if (roomName.length() < 2 || roomName.length() > 15)
            throw new IllegalArgumentException("Invalid room name. Room name should be between 2 and 15 charactes.");

        if (creatorUsername.length() < 2 || creatorUsername.length() > 15)
            throw new IllegalArgumentException("Invalid username. Your username should be between 2 and 15 charactes.");

        if (this.clientsToRooms.containsKey(client))
            throw new AlreadyInRoomException(this.clientsToRooms.get(client).getRoomName());

        if (this.getRoomByName(roomName) != null)
            return false;

        Room room = new Room(roomName, numPlayers, client, creatorUsername);
        this.rooms.add(room);
        this.clientsToRooms.put(client, room);
        return true;
    }

    @Override
    public RoomInfo joinRoom(Client client, String roomName, String username)
            throws RemoteException, AlreadyInRoomException, JoinRoomException {
        if (username.length() < 2 || username.length() > 15)
            throw new IllegalArgumentException("Invalid username. Your username should be between 2 and 15 charactes.");

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
    public void executeAction(Client c, GameAction action) {

    }

    @Override
    public void reconnect(Client c, String gameName) {

    }

    @Override
    public void ping(Client c) {

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
