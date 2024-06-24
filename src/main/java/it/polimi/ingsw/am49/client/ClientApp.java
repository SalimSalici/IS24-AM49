package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.client.connectors.ConnectorType;
import it.polimi.ingsw.am49.client.connectors.ServerConnector;
import it.polimi.ingsw.am49.client.connectors.ServerConnectorRMI;
import it.polimi.ingsw.am49.client.connectors.ServerConnectorSocket;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.sockets.ServerSocketHandler;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.config.StaticConfig;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.common.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.common.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.common.util.IntervalTimer;
import it.polimi.ingsw.am49.client.view.GuiView;
import it.polimi.ingsw.am49.client.view.TuiView;
import it.polimi.ingsw.am49.client.view.View;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.LocalTime;

/**
 * The main client application class that handles the client-side logic for the game.
 */
public class ClientApp implements Client {

    protected static String username;

    protected Server server;
    protected View view;
    protected VirtualGame game;
    protected MenuController menuController;
    protected RoomController roomController;
    protected GameController gameController;

    protected ServerConnector serverConnector;

    private final IntervalTimer heartbeatInterval;

    public ClientApp(boolean socket) throws RemoteException {
        this.serverConnector = socket ? new ServerConnectorSocket() : new ServerConnectorRMI();
        this.heartbeatInterval = new IntervalTimer(this::pingServer, 10, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Initializes controllers and view based on the GUI mode.
     * @param gui true to use GUI, false to use TUI
     */
    protected void initialize(boolean gui) {
        this.menuController = new MenuController(this.server, this);
        this.roomController = new RoomController(this.server, this);
        this.gameController = new GameController(this.server, this);
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

    /**
     * Sends a ping to the server to maintain the connection alive.
     */
    private void pingServer() {
        try { this.server.ping(this); } catch (Exception ignored) {}
    }

    /**
     * Loads a game from the provided game information.
     * @param completeGameInfo the complete game information to load the game from
     */
    public void loadGame(CompleteGameInfo completeGameInfo) {
        this.game = VirtualGame.loadGame(completeGameInfo);
    }

    /**
     * Sets the username for the client.
     * @param username the username to set
     */
    public static void setUsername(String username) {
        ClientApp.username = username;
    }

    /**
     * Returns the username of the client.
     * @return the username
     */
    public static String getUsername() {
        return username;
    }

    public void setServer(String host, int port, ConnectorType type) throws RemoteException {
        this.serverConnector.disconnect(this);
        if (serverConnector.getConnectorType() != type)
            this.serverConnector = type == ConnectorType.RMI ? new ServerConnectorRMI() : new ServerConnectorSocket();
        this.server = this.serverConnector.connect(host, port, this);
        if (this.menuController != null) menuController.setServer(server);
        if (this.roomController != null) roomController.setServer(server);
        if (this.gameController != null) gameController.setServer(server);
    }

    /**
     * Returns the server associated with this client.
     * @return the server
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * Returns the virtual game instance.
     * @return the virtual game
     */
    public VirtualGame getVirtualGame() {
        return this.game;
    }

    /**
     * Sets the view for the client.
     * @param view the view to set
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * The main method to start the client application.
     * @param args the command line arguments
     * @throws IOException if an I/O error occurs
     * @throws NotBoundException if not able to bind to the RMI registry
     */
    public static void main(String[] args) throws IOException, NotBoundException {
        String serverHost = "127.0.0.1";
//        String serverHost = "10.147.20.206";
        int serverPort = 8458;

        ClientApp client;
        Server server;

        List<String> argsList = List.of(args);

        if (argsList.contains("--disable-tui-colors"))
            StaticConfig.disableTuiColors();

//        if (argsList.contains("--socket")) {
//            client = ClientApp.getClient(argsList);
//            server = ClientApp.getSocketServer(serverHost, serverPort + 1, client);
//            System.out.println("Connected to socket server.");
//        } else {
//            server = ClientApp.getRMIServer(serverHost, serverPort);
//            System.setProperty("java.rmi.server.hostname", server.getClientHostAddress());
//            client = ClientApp.getClient(argsList);
//            System.out.println("Connected to RMI server.");
//        }

        client = ClientApp.getClient(argsList);
//        client.setServer(server);
        client.initialize(argsList.contains("--gui"));
    }

    /**
     * Retrieves the RMI server from the registry.
     * @param host the host of the RMI server
     * @param port the port of the RMI server
     * @return the server instance
     * @throws RemoteException if a remote error occurs
     * @throws NotBoundException if the server is not bound in the registry
     */
    private static Server getRMIServer(String host, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (Server) registry.lookup("server.am49.codex_naturalis");
    }

    /**
     * Creates a socket server connection.
     * @param host the host of the socket server
     * @param port the port of the socket server
     * @param client the client to connect
     * @return the server instance
     * @throws IOException if an I/O error occurs
     */
    private static Server getSocketServer(String host, int port, Client client) throws IOException {
        return new ServerSocketHandler(host, port, client);
    }

    private static ClientApp getClient(List<String> argsList) throws RemoteException {
        return argsList.contains("--socket") ? new ClientApp(true) : new ClientApp(false);
    }
//    private static ClientApp getClient(String[] args) throws RemoteException {
//        return List.of(args).contains("--gui") ? new GuiApp(args) : new TuiApp();
//    }
}
