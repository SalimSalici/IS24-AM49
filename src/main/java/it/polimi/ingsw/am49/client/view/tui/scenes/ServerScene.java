package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;

import java.rmi.RemoteException;

public class ServerScene extends Scene {

    private final MenuController menuController;

    private String host;
    private Integer port;

    public ServerScene(SceneManager sceneManager, MenuController menuController) {
        super(sceneManager);
        this.menuController = menuController;
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n");
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
        if (this.host == null) {
            System.out.println("\n");
            System.out.println("Default if empty: 127.0.0.1");
            System.out.print("Enter the ip address of the server> ");
        }
        else if (this.port == null) {
            System.out.println("Inserted host: " + this.host + "\n");
            System.out.println("Default if empty: 8458");
            System.out.print("Enter the port of the server>");
        }
    }

    @Override
    public void handleInput(String input) {
        if (input.equals("back")) {
            showError("Not available yet.");
        }

        if (this.host == null) {
            this.handleHost(input);
        } else
            this.handlePort(input);
    }

    public void handleHost(String input) {
        if (input.isEmpty()) {
            this.host = "127.0.0.1";
            this.refreshView();
            return;
        }
        else if (!this.isValidIPv4(input)) {
            this.showError("Invalid ip address. Please try again.");
            return;
        }
        this.host = input;
        this.refreshView();
    }

    public void handlePort(String input) {
        try {
            int port;
            if (input.isEmpty())
                port = 8458;
            else {
                port = Integer.parseInt(input);
                if (port < 1 || port > 65535) throw new NumberFormatException();
            }
            this.menuController.connectToServer(this.host, port);
        } catch (NumberFormatException e) {
            this.showError("Invalid port number. Please try again.");
        } catch (RemoteException e) {
            this.host = null;
            this.showError("Connection to server failed. Please try again.");
        }
    }

    private boolean isValidIPv4(String ip) {
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

    @Override
    public void focus() {
        this.host = null;
    }
}
