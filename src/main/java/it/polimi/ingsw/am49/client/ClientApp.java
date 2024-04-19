package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.messages.mtc.MessageToClient;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidUsernameException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class ClientApp extends UnicastRemoteObject implements Client {

    private String username = "default";

    public ClientApp() throws RemoteException {}

    @Override
    public void receiveGameUpdate(MessageToClient msg) {

    }

    @Override
    public void ping() {

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void main(String[] args) throws IOException, NotBoundException {
        Client client = new ClientApp();

        int serverPort = 8458;
        Scanner scanner = new Scanner(System.in);

        Server server = null;
        String serverType = "";
        if (List.of(args).contains("--socket")) {
            server = ClientApp.getSocketServer("127.0.0.1", serverPort + 1, client);
            serverType = "socket";
        } else {
            server = ClientApp.getRMIServer("127.0.0.1", serverPort);
            serverType = "RMI";
        }

        System.out.println("Connected to the " + serverType + " server");

        String username = null;
        while (true) {
            System.out.print("Choose username: ");
            username = scanner.nextLine();

            try {
                if (server.login(client, username)) {
                    System.out.println("Logged in successfully!");
                    break;
                }
                else
                    System.out.println("Username already taken... could not log in.");
            } catch (InvalidUsernameException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            String command = scanner.nextLine();
            if (command.equals("disconnect")) {
                server.logout(client, username);
                break;
            }
        }

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
