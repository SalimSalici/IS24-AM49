package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.client.sockets.ServerSocketHandler;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.config.StaticConfig;
import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.util.IntervalTimer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ClientApp extends UnicastRemoteObject implements Client {

    protected VirtualGame game;
    protected Server server;
    protected String username;
    private final IntervalTimer heartbeatInterval;

    public ClientApp() throws RemoteException {
        this.heartbeatInterval = new IntervalTimer(this::pingServer, 10, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException {

    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate == null) {
            Log.getLogger().severe("Received a null GameUpdate from the server.");
            return;
        }
        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE) {
            this.game = VirtualGame.newGame((GameStartedUpdate)gameUpdate);
        } else
            this.game.processGameUpdate(gameUpdate);
    }

    @Override
    public void playerDisconnected(String username) throws RemoteException {

    }

    @Override
    public void startHeartbeat() {
        this.heartbeatInterval.start();
    }

    @Override
    public void stopHeartbeat() {
        this.heartbeatInterval.stop();
        System.out.println("Heartbeat stopped");
    }

    private void pingServer() {
        try {
            this.server.ping(this);
        } catch (Exception e) {
            // TODO: handle
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void loadGame(CompleteGameInfo completeGameInfo) {
        this.game = VirtualGame.loadGame(completeGameInfo);
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

    // TODO: handle exceptions
    public static void main(String[] args) throws IOException, NotBoundException {
//        String serverHost = "10.147.20.145";
        String serverHost = "127.0.0.1";
        int serverPort = 8458;

        ClientApp client;
        Server server;

        List<String> argsList = List.of(args);

        if (argsList.contains("--disable-tui-colors"))
            StaticConfig.disableTuiColors();

        if (argsList.contains("--socket")) {
            client = ClientApp.getClient(args);
            server = ClientApp.getSocketServer(serverHost, serverPort + 1, client);
            System.out.println("Connected to socket server.");
        } else {
            server = ClientApp.getRMIServer(serverHost, serverPort);
            System.setProperty("java.rmi.server.hostname", server.getClientHostAddress());
            client = ClientApp.getClient(args);
            System.out.println("Connected to RMI server.");
        }

        client.setServer(server);
        client.initialize();
    }

    private static Server getRMIServer(String host, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (Server) registry.lookup("server.am49.codex_naturalis");
    }

    private static Server getSocketServer(String host, int port, Client client) throws IOException {
        return new ServerSocketHandler(host, port, client);
    }

    private static ClientApp getClient(String[] args) throws RemoteException {
        return List.of(args).contains("--gui") ? new GuiApp(args) : new TuiApp();
    }
}
