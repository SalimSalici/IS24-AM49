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
        System.out.println("\nA new player (" + username + ") joined your room - " + room.toString());
        System.out.print("> ");
    }

    @Override
    public void playerLeftYourRoom(RoomInfo room, String username) throws RemoteException {
        System.out.println("\nA player (" + username + ") left your room - " + room.toString());
        System.out.print("> ");
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        System.out.println("\nReceived game updated - " + gameUpdate.toString());
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

//        String username = null;
//        while (true) {
//            System.out.print("Choose username: ");
//            username = scanner.nextLine();
//
//            try {
//                if (server.login(client, username)) {
//                    System.out.println("Logged in successfully!");
//                    break;
//                }
//                else
//                    System.out.println("Username already taken... could not log in.");
//            } catch (InvalidUsernameException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//
//        boolean disconnect = false;
//        while (!disconnect) {
//            System.out.print("Command: ");
//            String command = scanner.nextLine();
//            switch (command) {
//                case "create" -> {
//                    System.out.print("Choose room name: ");
//                    String roomName = scanner.nextLine();
//
//                    try {
//                        if (server.createRoom(client, roomName, 3, username))
//                            System.out.println("Created room " + roomName + " successfully");
//                        else
//                            System.out.println("Failed creating room " + roomName);
//                    } catch (Exception e) {
//                        System.out.println(e.getMessage());
//                    }
//                }
//                case "join" -> {
//                    System.out.print("Choose room name: ");
//                    String roomName = scanner.nextLine();
//
//                    try {
//                        RoomInfo roomInfo = server.joinRoom(client, roomName, username);
//                        if (roomInfo != null) {
//                            System.out.println(
//                                    "Joined room " + roomInfo.roomName() + " successfully "
//                                    + " | maxPlayers: " + roomInfo.maxPlayers()
//                                    + " | playerInRoom: " + roomInfo.playersInRoom()
//                            );
//                        }
//                    } catch (Exception e) {
//                        System.out.println(e.getMessage());
//                    }
//                }
//                case "disconnect" -> {
//                    server.disconnectClient(client);
//                    disconnect = true;
//                }
//                default -> System.out.println("Unknown command");
//            }
//        }

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
