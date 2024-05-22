package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class MainMenuScene extends Scene {

    private final TuiApp tuiApp;
    private boolean running = true;
    private final Server server;

    private List<RoomInfo> rooms;

    public MainMenuScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager, tuiApp);
        this.tuiApp = tuiApp;
        this.server = tuiApp.getServer();
        this.rooms = new LinkedList<>();
    }

    @Override
    public void play() {
        this.printHeader();
        System.out.println("Loading...");
        try {
            this.rooms = this.server.fetchRooms(this.tuiApp);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }

        this.printScene();
        this.linesToClear = 3;

        while (this.running) {
            this.promptCommand();
            String input = this.scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                this.clearLines(3);
                continue;
            }

            this.clearLines(linesToClear);
            this.handleUserInput(input);
        }
    }

    private void handleUserInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];

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
                // TODO: Handle reconnect
                linesToClear = 3;
                break;
            case "5":
                // TODO: Handle help
                linesToClear = 3;
                break;
            case "6":
                refreshRooms();
                break;
            case "7":
                System.exit(0);
            default:
                showError("Invalid command, please try again.");
        }
    }

    private void promptCommand() {
        System.out.println("Available commands: (1) username | (2) create | (3) join | (4) reconnect | (5) help | (6) refresh rooms | (7) exit");
        System.out.print(">>> ");
    }

    private void printScene() {
        this.printHeader();
        this.printRooms();
    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        | Main menu |          ");
        System.out.println("        *************          ");
        System.out.println("\n\n\n");
        System.out.println("You username is: " + this.tuiApp.getUsername()) ;
        System.out.println("\n\n\n");
    }

    private void printRooms() {
        System.out.println("Available rooms");
        System.out.println("--------------------------\n");
        if (this.rooms.isEmpty()) {
            System.out.println("There are no rooms.");
        } else {
            for (RoomInfo roomInfo : this.rooms) {
                System.out.println(roomInfo);
            }
        }
        System.out.println("\n\n\n");
    }

    private void handleUsername(String[] args) {
        if (args.length < 2) {
            showError("Username missing, please try again. Type '1 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            showHelp("The 'username' command is used to change your username. The username must be between 2 and 20 characters long", "1 [username]");
            return;
        }
        String username = args[1];
        if (!isUsernameValid(username)) {
            showError("Username must be between 2 and 20 characters, please try again.");
            return;
        }
        this.tuiApp.setUsername(username);
        this.printHeader();
        this.printRooms();
    }

    private void handleCreateRoom(String[] args) {
        if (args.length > 1 && args[1].equals("--help")) {
            showHelp("The 'create' command is used to create a new room. When creating a room, you must specify its name and amount of players.", "2 [room name] [number of players]");
            return;
        }
        if (args.length < 3) {
            showError("Missing parameters, please try again. Type '2 --help' for more information about this command.", 3);
            return;
        }

        String roomName = args[1];
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            showError("Invalid room number. It must be an integer.", 3);
            return;
        }

        try {
            System.out.println("Loading...");
            RoomInfo roomInfo = this.server.createRoom(this.tuiApp, roomName, numPlayers, this.tuiApp.getUsername());
            this.sceneManager.setScene(new RoomScene(this.sceneManager, this.tuiApp, roomInfo));
            this.running = false;
        } catch (CreateRoomException | AlreadyInRoomException e) {
            this.clearLines(2); // Delete "Loading..."
            showError("Failed to create room. " + e.getMessage());
        } catch (RemoteException e) {
            this.clearLines(2); // Delete "Loading..."
            showError("RemoteException. " + e.getMessage());
        }
    }

    private void handleJoinRoom(String[] args) {
        if (args.length < 2) {
            showError("Missing parameters, please try again. Type '3 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            showHelp("The 'join' command is used to join an existing room. When joining a room, you must specify its name.", "3 [room name]");
            return;
        }
        String roomName = args[1];

        try {
            System.out.println("Loading...");
            RoomInfo roomInfo = this.server.joinRoom(this.tuiApp, roomName, this.tuiApp.getUsername());
            this.sceneManager.setScene(new RoomScene(this.sceneManager, this.tuiApp, roomInfo));
            this.running = false;
        } catch (JoinRoomException | AlreadyInRoomException  e) {
            this.clearLines(2); // Delete "Loading..."
            showError("Failed to join room." + e.getMessage());
        } catch (RemoteException e) {
            this.clearLines(2); // Delete "Loading..."
            showError("RemoteException. " + e.getMessage());
        }
    }

    private void refreshRooms() {
        try {
            System.out.println("Loading...");
            this.rooms = this.server.fetchRooms(this.tuiApp);
            this.printScene();
        } catch (RemoteException e) {
            showError("RemoteException" + e.getMessage());
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }
}
