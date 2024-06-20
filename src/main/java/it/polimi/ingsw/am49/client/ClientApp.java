package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.sockets.ServerSocketHandler;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.config.StaticConfig;
import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.util.IntervalTimer;
import it.polimi.ingsw.am49.view.GuiView;
import it.polimi.ingsw.am49.view.TuiView;
import it.polimi.ingsw.am49.view.View;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.LocalTime;

public class ClientApp extends UnicastRemoteObject implements Client {

    protected static String username;

    protected Server server;
    protected View view;
    protected VirtualGame game;
    protected MenuController menuController;
    protected RoomController roomController;
    protected GameController gameController;

    private final IntervalTimer heartbeatInterval;

    public ClientApp() throws RemoteException {
        this.heartbeatInterval = new IntervalTimer(this::pingServer, 10, 1000, TimeUnit.MILLISECONDS);
    }

    protected void initialize(boolean gui) {
        this.menuController = new MenuController(this.server, this);
        this.roomController = new RoomController(this.server, this);
        this.gameController = new GameController(this.server, this);
        // TODO: tui vs gui
//        this.setView(new TuiView(menuController, roomController, gameController));
        try {
            if (gui)
                this.setView(new GuiView(menuController, roomController, gameController));
            else
                this.setView(new TuiView(menuController, roomController, gameController));
        } catch (RemoteException ignored) {}
        this.menuController.setView(view);
        this.roomController.setView(view);
        this.gameController.setView(view);
        this.view.initialize();
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException {
        this.view.roomUpdate(roomInfo, message);
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate == null) {
            Log.getLogger().severe("Received a null GameUpdate from the server.");
            return;
        }

        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE) {
            GameStartedUpdate gameStartedUpdate = (GameStartedUpdate) gameUpdate;
            this.game = VirtualGame.newGame(gameStartedUpdate);
            this.view.setVirtualGame(this.game);
            this.view.showStarterChoice(gameStartedUpdate.starterCardId());
        } else if (gameUpdate.getType() == GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE) {
            ChoosableObjectivesUpdate choosableObjectivesUpdate = (ChoosableObjectivesUpdate) gameUpdate;
            this.view.showObjectiveChoice(choosableObjectivesUpdate.objectiveCards());
        } else if (this.game != null)
            this.game.processGameUpdate(gameUpdate);
    }

    @Override
    public void startHeartbeat() {this.heartbeatInterval.start();}

    @Override
    public void stopHeartbeat() {
        this.heartbeatInterval.stop();
    }

    @Override
    public void receiveChatMessage(ChatMSG msg){
        game.getPlayerByUsername(username).setMessage(msg.text(), msg.sender(), msg.recipient(), LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        this.view.receiveChatMessage(msg);
    }

    private void pingServer() {
        try { this.server.ping(this); } catch (Exception ignored) {}
    }

    public void loadGame(CompleteGameInfo completeGameInfo) {
        this.game = VirtualGame.loadGame(completeGameInfo);
    }

    public static void setUsername(String username) {
        ClientApp.username = username;
    }

    public static String getUsername() {
        return username;
    }

    protected void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public VirtualGame getVirtualGame() {
        return this.game;
    }

    public void setView(View view) {
        this.view = view;
    }



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
        client.initialize(argsList.contains("--gui"));
    }

    private static Server getRMIServer(String host, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (Server) registry.lookup("server.am49.codex_naturalis");
    }

    private static Server getSocketServer(String host, int port, Client client) throws IOException {
        return new ServerSocketHandler(host, port, client);
    }

    private static ClientApp getClient(String[] args) throws RemoteException {
        return new ClientApp();
    }
//    private static ClientApp getClient(String[] args) throws RemoteException {
//        return List.of(args).contains("--gui") ? new GuiApp(args) : new TuiApp();
//    }
}
