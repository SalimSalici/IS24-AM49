package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.common.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.common.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.common.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;

import java.util.List;

/**
 * Represents the main menu scene in the text-based user interface.
 */
public class MainMenuScene extends Scene {

    /**
     * The menu controller for handling menu-related actions.
     */
    private final MenuController menuController;

    /**
     * The list of available rooms.
     */
    private List<RoomInfo> rooms;

    /**
     * Flag indicating if the scene is currently loading.
     */
    private boolean isLoading = false;

    /**
     * Constructs a MainMenuScene with the specified scene manager and menu controller.
     *
     * @param sceneManager the scene manager
     * @param menuController the menu controller
     */
    public MainMenuScene(SceneManager sceneManager, MenuController menuController) {
        super(sceneManager);
        this.menuController = menuController;
    }

    /**
     * Prints the current view of the main menu scene.
     */
    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n\n");
        System.out.println("Your username is: " + ClientApp.getUsername());
        System.out.println("\n\n\n");
        this.printRoomList();
        System.out.println("\n\n");
        this.printInfoOrError();
        this.printPrompt();
    }

    /**
     * Prints the prompt for user commands in the main menu scene.
     */
    private void printPrompt() {
        System.out.print("Available commands: ");
        if (this.isLoading)
            System.out.println("exit");
        else
            System.out.println("(1) username | (2) create | (3) join | (4) reconnect | (5) refresh rooms | (6) server selection | exit");
        System.out.print(">>> ");
    }

    /**
     * Prints the header of the main menu scene.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        | Main menu |          ");
        System.out.println("        *************          ");
    }

    /**
     * Prints the list of available rooms.
     */
    private void printRoomList() {
        System.out.println("Available rooms");
        System.out.println("--------------------------\n");
        if (this.rooms.isEmpty()) {
            System.out.println("There are no rooms.");
        } else {
            for (RoomInfo roomInfo : this.rooms) {
                System.out.println(roomInfo);
            }
        }
    }

    /**
     * Handles user input in the main menu scene.
     *
     * @param input the user input string
     */
    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];

        if (this.isLoading && !input.equals("exit")) {
            this.showError("While loading you can only exit.");
            return;
        }

        switch (command) {
            case "1":
                handleUsername(parts);
                break;
            case "2":
                handleCreateRoom(parts);
                break;
            case "3":
                handleJoinRoom(parts);
                break;
            case "4":
                handleReconnectRoom(parts);
                break;
            case "5":
                handleRefreshRooms();
                break;
            case "6":
                this.sceneManager.forceServerSelection(null);
                break;
            case "exit":
                System.exit(0);
            case "":
                this.refreshView();
                break;
            default:
                this.showError("Invalid command, please try again.");
        }
    }

    /**
     * Focuses on the main menu scene, fetching the list of available rooms.
     */
    public void focus() {
        this.rooms = this.menuController.fetchRooms();
        this.printView();
    }

    /**
     * Handles the username change command.
     *
     * @param args the command arguments
     */
    private void handleUsername(String[] args) {
        if (args.length < 2) {
            this.showError("Username missing, please try again. Type '1 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            this.showHelpMessage("The 'username' command is used to change your username. The username must be between 2 and 20 characters long", "1 [username]");
            return;
        }
        String username = args[1];
        if (username.length() < 2 || username.length() > 20) {
            this.showError("Username must be between 2 and 20 characters, please try again.");
            return;
        }
        this.menuController.changeUsername(username);
        this.refreshView();
    }

    /**
     * Handles the create room command.
     *
     * @param args the command arguments
     */
    private void handleCreateRoom(String[] args) {
        if (args.length > 1 && args[1].equals("--help")) {
            this.showHelpMessage("The 'create' command is used to create a new room. When creating a room, you must specify its name and amount of players.", "Example usage: 2 [room name] [number of players]");
            return;
        }
        if (args.length < 3) {
            this.showError("Missing parameters, please try again. Type '2 --help' for more information about this command.");
            return;
        }

        String roomName = args[1];
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            this.showError("Invalid room number. It must be an integer.");
            return;
        }

        try {
            this.isLoading = true;
            this.showInfoMessage("Loading room...");
            this.menuController.createRoom(roomName, numPlayers);
            this.isLoading = false;
        } catch (CreateRoomException | AlreadyInRoomException e) {
            this.isLoading = false;
            this.showError("Failed to create room. " + e.getMessage());
        }
    }

    /**
     * Handles the join room command.
     *
     * @param args the command arguments
     */
    private void handleJoinRoom(String[] args) {
        if (args.length < 2) {
            this.showError("Missing parameters, please try again. Type '3 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            this.showHelpMessage("The 'join' command is used to join an existing room. When joining a room, you must specify its name.", "3 [room name]");
            return;
        }
        String roomName = args[1];

        try {
            this.isLoading = true;
            this.showInfoMessage("Loading room...");
            this.menuController.joinRoom(roomName);
            this.isLoading = false;
        } catch (JoinRoomException | AlreadyInRoomException | GameAlreadyStartedException  e) {
            this.isLoading = false;
            this.showError("Failed to join room. " + e.getMessage());
        }
    }

    /**
     * Handles the reconnect room command.
     *
     * @param args the command arguments
     */
    private void handleReconnectRoom(String[] args) {
        if (args.length < 2) {
            this.showError("Missing parameters, please try again. Type '3 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            this.showHelpMessage("The 'join' command is used to join an existing room. When joining a room, you must specify its name.", "3 [room name]");
            return;
        }

        String roomName = args[1];

        try {
            this.isLoading = true;
            this.showInfoMessage("Loading game...");
            this.menuController.reconnect(roomName);
            this.isLoading = false;
        } catch (JoinRoomException | AlreadyInRoomException  e) {
            this.isLoading = false;
            this.showError("Failed to join room. " + e.getMessage());
        }
    }

    /**
     * Handles the refresh rooms command, updating the list of available rooms.
     */
    public void handleRefreshRooms() {
        this.showInfoMessage("Updating list of available rooms. Please wait...");
        this.rooms = this.menuController.fetchRooms();
        this.refreshView();
    }
}
