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
        super(sceneManager);
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

        this.printHeader();
        this.printRooms();
        linesToClear = 2;

        while (this.running) {
            this.promptCommand();
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("Empty, please try again.");
                linesToClear = 1;
                continue;
            }

            IntStream.range(0, linesToClear).forEach(i -> clearLastLine());

            String command = parts[0];
            switch (command) {
                case "1":
                    this.handleUsername(parts);
                    break;
                case "2":
                    this.handleCreateRoom(parts);
                    break;
                case "3":
                    this.handleJoinRoom(parts);
                    break;
                case "4":
                    linesToClear = 2;
                    break;
                case "5":
                    linesToClear = 2;
                    break;
                case "6":
                    try {
                        System.out.println("Loading...");
                        this.rooms = this.server.fetchRooms(this.tuiApp);
                        this.printScene();
                    } catch (RemoteException e) {
                        // TODO: Handle exception
                        throw new RuntimeException(e);
                    }
                    break;
                case "7":
                    System.exit(0);
                default:
                    System.out.println("Invalid command, please try again.");
                    linesToClear = 3;
            }
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
            return;
        } else {
            for (RoomInfo roomInfo : this.rooms) {
                System.out.println(roomInfo);
            }
        }
        System.out.println("\n\n\n");
    }

    private void handleUsername(String[] args) {
        if (args.length < 2) {
            System.out.println("Username missing, please try again. Type '1 --help' for more information about this command. ");
            linesToClear = 3;
            return;
        }
        if (args[1].equals("--help")) {
            System.out.println("The 'username' command is used to change your username. The username must be between 2 and 20 and characters long");
            System.out.println("Example usage:");
            System.out.println("1 [username]");
            linesToClear = 5;
            return;
        }
        String username = args[1];
        if (!this.isUsernameValid(username)) {
            System.out.println("Username must be between 2 and 20 characters, please try again.");
            linesToClear = 3;
            return;
        }
        this.tuiApp.setUsername(username);
        this.printHeader();
        this.printRooms();
    }

    private void handleCreateRoom(String[] args) {
        if (args.length > 1 && args[1].equals("--help")) {
            System.out.println("The 'create' command is used to craete a new room. When creating a room, you must specify its name and amount of players.");
            System.out.println("Example usage:");
            System.out.println("2 [room name] [number of players]");
            linesToClear = 5;
            return;
        }
        if (args.length < 3) {
            System.out.println("Missing parameters, please try again. Type '2 --help' for more information about this command. ");
            linesToClear = 3;
            return;
        }
        String roomName = args[1];
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number. It must be an integer.");
            linesToClear = 3;
            return;
        }

        try {
            System.out.println("Loading...");
            RoomInfo roomInfo = this.server.createRoom(this.tuiApp, roomName, numPlayers, this.tuiApp.getUsername());
            this.sceneManager.setScene(new RoomScene(this.sceneManager, this.tuiApp, roomInfo));
            this.running = false;
        } catch (CreateRoomException e) {
            // TODO: Handle exception
            System.out.println(e.getMessage());
            linesToClear = 4;
            return;
        } catch (AlreadyInRoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void handleJoinRoom(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing parameters, please try again. Type '2 --help' for more information about this command. ");
            linesToClear = 3;
            return;
        }
        if (args[1].equals("--help")) {
            System.out.println("The 'join' command is used to join an existing room. When joining a room, you must specify its name.");
            System.out.println("Example usage:");
            System.out.println("3 [room name]");
            linesToClear = 5;
            return;
        }
        String roomName = args[1];

        try {
            System.out.println("Loading...");
            RoomInfo roomInfo = this.server.joinRoom(this.tuiApp, roomName, this.tuiApp.getUsername());
            this.running = false;
            this.sceneManager.setScene(new RoomScene(this.sceneManager, this.tuiApp, roomInfo));
        } catch (JoinRoomException e) {
            // TODO: Handle exception
            System.out.println(e.getMessage());
            return;
        } catch (AlreadyInRoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }
}
