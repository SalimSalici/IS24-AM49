package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.client.connectors.ConnectorType;
import it.polimi.ingsw.am49.client.connectors.ServerConnector;
import it.polimi.ingsw.am49.client.connectors.ServerConnectorRMI;
import it.polimi.ingsw.am49.client.connectors.ServerConnectorSocket;
import it.polimi.ingsw.am49.common.Client;
import it.polimi.ingsw.am49.common.exceptions.RoomException;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.common.reconnectioninfo.CompleteGameInfo;
import it.polimi.ingsw.am49.common.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.common.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.common.util.IntervalTimer;
import it.polimi.ingsw.am49.client.view.GuiView;
import it.polimi.ingsw.am49.client.view.TuiView;
import it.polimi.ingsw.am49.client.view.View;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

    public ClientApp() throws RemoteException {
        this.serverConnector = ClientConfig.connectionType == ConnectorType.SOCKET ?
                new ServerConnectorSocket() :
                new ServerConnectorRMI();
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

        if (gui) this.setView(new GuiView(menuController, roomController, gameController));
        else this.setView(new TuiView(menuController, roomController, gameController));

        this.menuController.setView(view);
        this.roomController.setView(view);
        this.gameController.setView(view);
        this.view.initialize();
    }

    @Override
    public synchronized void roomUpdate(RoomInfo roomInfo, String message) throws RemoteException {
        this.view.roomUpdate(roomInfo, message);
    }

    @Override
    public synchronized void receiveGameUpdate(GameUpdate gameUpdate) {
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
    public synchronized void receiveChatMessage(ChatMSG msg){
        game.getPlayerByUsername(username).setMessage(msg.text(), msg.sender(), msg.recipient(), LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        this.view.receiveChatMessage(msg);
    }

    /**
     * Sends a ping to the server to maintain the connection alive.
     */
    private void pingServer() {
        try {
            this.server.ping(this);
        } catch (Exception ignored) {
            this.backToServerChoice();
        }
    }

    public void backToServerChoice() {
        new Thread(() -> {
            try { server.leaveRoom(this); } catch (RemoteException | RoomException ignored) {}
        }).start();
        this.stopHeartbeat();
        if (this.game != null)
            this.game.clearAllObservers();
        this.view.showServerSelection("Connection to server lost.");
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

    /**
     * Sets the server connection with the given host, port, and connection type.
     * @param host the server host
     * @param port the server port
     * @param type the type of connection (RMI or SOCKET)
     * @throws RemoteException if an RMI error occurs
     */
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
        ClientApp client;

        List<String> argsList = List.of(args);

        ClientApp.parseArgs(argsList);

        client = new ClientApp();
        client.initialize(argsList.contains("--gui"));
    }

    /**
     * Parses command line arguments to configure the client application.
     * @param argsList the list of command line arguments
     */
    private static void parseArgs(List<String> argsList) {
        if (argsList.contains("--disable-tui-colors"))
            ClientConfig.disableColors();

        // If both port and host are set, then use RMI by default
        if (argsList.contains("--port") && argsList.contains("--host"))
            ClientConfig.connectionType = ConnectorType.RMI;

        // But if socket is set, then use that
        if (argsList.contains("--socket"))
            ClientConfig.connectionType = ConnectorType.SOCKET;

        // Otherwise ClientConfig.connectionType is null and user will be requested which one to use in the
        // server selection scene

        for (int i = 0; i < argsList.size() - 1; i++) {
            String arg = argsList.get(i);
            if (arg.equals("--host") || arg.equals("--h")) {
                String host = argsList.get(i+1);
                if (isIpValid(host))
                    ClientConfig.serverHost = host;
                else {
                    System.out.println("Invalid host. Terminating.");
                    System.exit(0);
                }
            } else if (arg.equals("--port") || arg.equals("--p")) {
                String portString = argsList.get(i+1);
                if (isPortValid(portString))
                    ClientConfig.serverPort = Integer.parseInt(portString);
                else {
                    System.out.println("Invalid port. Terminating.");
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Validates the given IP address.
     * @param ip the IP address to validate
     * @return true if the IP address is valid, false otherwise
     */
    public static boolean isIpValid(String ip) {
        if (ip == null || ip.isEmpty())
            return false;

        if (ip.equals("localhost")) return true;

        String[] parts = ip.split("\\.");
        if (parts.length != 4)
            return false;

        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255)
                    return false;
                if (part.length() > 1 && part.startsWith("0"))
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the given port number.
     * @param input the port number to validate
     * @return true if the port number is valid, false otherwise
     */
    public static boolean isPortValid(String input) {
        int port;
        try {
            port = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return !(port < 1 || port > 65535);
    }
}
