package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientConfig;
import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.connectors.ConnectorType;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;

import java.rmi.RemoteException;

public class ServerScene extends Scene {

    private final MenuController menuController;

    private ConnectorType connectorType;
    private String host;

    private boolean startup = true;

    public ServerScene(SceneManager sceneManager, MenuController menuController) {
        super(sceneManager);
        this.menuController = menuController;
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n");
        this.printPrompt();
    }

    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("    |  Server selection  |     ");
        System.out.println("    **********************     ");
    }

    private void printPrompt() {
        this.printInfoOrError();
        if (this.connectorType == null) {
            System.out.println("\n\n");
            System.out.println("Default: RMI");
            System.out.print("Enter the connection type [RMI/socket]> ");
        } else if (this.host == null) {
            System.out.println();
            System.out.println("Connection type: " + this.connectorType + "\n");
            System.out.println("Default if empty: 127.0.0.1");
            System.out.print("Enter the ip address of the server> ");
        } else {
            System.out.println("Connection type: " + this.connectorType);
            System.out.println("Inserted host: " + this.host + "\n");
            if (this.connectorType == ConnectorType.RMI)
                System.out.println("Default if empty: 8458");
            else
                System.out.println("Default if empty: 8459");
            System.out.print("Enter the port of the server>");
        }
    }

    @Override
    public void handleInput(String input) {
        if (input.equals("back")) {
            showError("Not available yet.");
        }

        if (this.connectorType == null) {
          this.handleConnectionType(input);
        } else if (this.host == null) {
            this.handleHost(input);
        } else
            this.handlePort(input);
    }

    public void handleConnectionType(String input) {
        input = input.toLowerCase();
        if (input.isEmpty() || input.startsWith("r"))
            this.connectorType = ConnectorType.RMI;
        else if (input.startsWith("s"))
            this.connectorType = ConnectorType.SOCKET;
        else {
            this.showError("Invalid connection type. Must be rmi or socket.");
            return;
        }
        this.refreshView();
    }

    public void handleHost(String input) {
        if (input.isEmpty()) {
            this.host = "127.0.0.1";
            this.refreshView();
            return;
        }
        else if (!ClientApp.isIpValid(input)) {
            this.showError("Invalid ip address. Please try again.");
            return;
        }
        this.host = input;
        this.refreshView();
    }

    public void handlePort(String input) {
        try {
            int port;
            if (input.isEmpty()) {
                if (this.connectorType == ConnectorType.RMI)
                    port = 8458;
                else
                    port = 8459;
            } else {
                port = Integer.parseInt(input);
                if (!ClientApp.isPortValid(input)) throw new NumberFormatException();
            }
            this.menuController.connectToServer(this.host, port, this.connectorType);
        } catch (NumberFormatException e) {
            this.showError("Invalid port number. Please try again.");
        } catch (RemoteException e) {
            this.host = null;
            this.showError("Connection to server failed. Please try again.");
        }
    }

    @Override
    public void focus() {
        this.host = null;
        this.connectorType = null;

        if (this.startup) {
            this.startup = false;
            this.connectorType = ClientConfig.connectionType;
            this.host = ClientConfig.serverHost;
            if (this.connectorType != null && this.host != null && ClientConfig.serverPort != null) {
                System.out.println("Connection attempt with supplied arguments. Please wait...");
                this.handlePort(String.valueOf(ClientConfig.serverPort));
                return;
            }
        }

        this.printView();
    }
}
