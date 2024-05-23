package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public abstract class ClientApp extends UnicastRemoteObject implements Client {

    protected VirtualGame game;
    protected Server server;
    protected String username;

    public ClientApp() throws RemoteException {}

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException {

    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE) {
            this.game = VirtualGame.newGame((GameStartedUpdate)gameUpdate);
        } else
            this.game.processGameUpdate(gameUpdate);
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

    public String getUsername() {
        return this.username;
    }

    private void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public VirtualGame getVirtualGame() {
        return this.game;
    }

    protected abstract void initialize();

    public static void main(String[] args) throws IOException, NotBoundException, AlreadyInRoomException, NotInGameException {

        System.setProperty("java.rmi.server.hostname", "10.147.20.145");

        ClientApp client;
        if(List.of(args).contains("--gui")) {
            client = new GuiApp(args);
        }
        else {
            client = new TuiApp();
        }

        String host = "10.147.20.145";
        int serverPort = 8458;

        Server server = null;
        String serverType;
        if (List.of(args).contains("--socket")) {
            server = ClientApp.getSocketServer(host, serverPort + 1, client);
            serverType = "socket";
        } else {
            server = ClientApp.getRMIServer(host, serverPort);
            serverType = "RMI";
        }

        client.setServer(server);
        client.initialize();

        System.out.println("Connected to the " + serverType + " server");

        System.exit(0);
    }

    private static Server getRMIServer(String host, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (Server) registry.lookup("server.am49.codex_naturalis");
    }

    private static Server getSocketServer(String host, int port, Client client) throws IOException {
        return new ServerSocketHandler(host, port, client);
    }
}
