package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.model.actions.GameAction;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerApp implements Server {

    Map<Client, Room> clientsToRooms;
    Set<String> usernamesTaken;

    public ServerApp() {
        this.clientsToRooms = new HashMap<>();
        this.usernamesTaken = new HashSet<>();
    }

    @Override
    public void login(Client client, String username) throws RemoteException {
        if (usernamesTaken.contains(username)) {
            client.loginOutcome(false);
            System.out.println("Client with username '" + username + "' tried to join, but the username was not available");
            return;
        }

        this.clientsToRooms.put(client, null);
        this.usernamesTaken.add(username);
        client.loginOutcome(true);
        System.out.println("Client with username '" + username + "' joined");
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
    public void createGame(Client client, String gameName, int numPlayers) {

    }

    @Override
    public void joinGame(Client client, String gameName) {

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
