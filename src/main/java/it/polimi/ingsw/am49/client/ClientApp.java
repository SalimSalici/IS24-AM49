package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.controller.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.view.tui.TUIApp;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientApp extends UnicastRemoteObject implements Client {

    private String username = "default";

    public ClientApp() throws RemoteException {}

    @Override
    public void playerJoinedYourRoom(RoomInfo room, String username) throws RemoteException {
        System.out.println("\rA new player (" + username + ") joined your room - " + room.toString());
        System.out.print("> ");
    }

    @Override
    public void playerLeftYourRoom(RoomInfo room, String username) throws RemoteException {
        System.out.println("\rA player (" + username + ") left your room - " + room.toString());
        System.out.print("> ");
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate != null)
            System.out.println("\rReceived game updated - " + gameUpdate.toString());
        else
            System.out.println("\rReceived yet unsupported game update.");
        System.out.print("> ");
    }

    @Override
    public void playerDisconnected(String username) throws RemoteException {

    }

    @Override
    public void ping() {

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void main(String[] args) throws IOException, NotBoundException, AlreadyInRoomException, NotInGameException {
        Client client = new ClientApp();

        String host = "127.0.0.1";
        int serverPort = 8458;

        Server server;
        String serverType;
        if (List.of(args).contains("--socket")) {
            server = ClientApp.getSocketServer(host, serverPort + 1, client);
            serverType = "socket";
        } else {
            server = ClientApp.getRMIServer(host, serverPort);
            serverType = "RMI";
        }

        System.out.println("Connected to the " + serverType + " server");

        new TUIApp(client, server).startTUI();

        System.exit(0);
    }

    private static Server getRMIServer(String host, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", port);
        return (Server) registry.lookup("server.am49.codex_naturalis");
    }

    private static Server getSocketServer(String host, int port, Client client) throws IOException {
        return new ServerSocketHandler(host, port, client);
    }
}
