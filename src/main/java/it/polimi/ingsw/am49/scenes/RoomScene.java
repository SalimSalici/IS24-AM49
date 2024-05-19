package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.stream.IntStream;

public class RoomScene extends Scene {
    private final TuiApp tuiApp;
    private Boolean running = true;
    private final Server server;
    private RoomInfo roomInfo;
    private boolean isUserReady;

    private Thread inputThread;

    public RoomScene(SceneManager sceneManager, TuiApp tuiApp, RoomInfo roomInfo) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.server = this.tuiApp.getServer();
        this.roomInfo = roomInfo;
        this.isUserReady = false;
    }

    @Override
    public void play() {
        this.printHeader();
        this.printRoomInfo();
        this.linesToClear = 3;

        while (this.running) {

            if (!this.isUserReady) {
                this.promptCommand();
                String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
                if (parts.length == 0) {
                    this.showError("Invalid command, please try again.");
                    continue;
                }

                this.clearLines(this.linesToClear);

                String command = parts[0];
                switch (command) {
                    case "1":
                        this.handleReady(parts);
                        break;
                    case "2":
                        this.handleLeave();
                        break;
                    default:
                        this.showError("Invalid command, please try again.");
                }
            } else {
                this.promptReadyCommand();
                String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
                if (parts.length == 0) {
                    this.showError("Invalid command, please try again.");
                    continue;
                }

                String command = parts[0];
                if (command.equals("1")) {
                    this.handleLeave();
                } else {
                    this.showError("Invalid command, please try again.");
                }
            }
        }


    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        |    Room    |          ");
        System.out.println("        **************          ");
        System.out.println("\n\n\n");
    }

    private void promptCommand() {
        if (this.isUserReady) this.promptReadyCommand();
        else this.promptNotReadyCommand();
    }

    private void promptNotReadyCommand() {
        System.out.println("Available commands: (1) ready | (2) leave ");
        System.out.print(">>> ");
    }

    private void promptReadyCommand() {
        System.out.println("Available commands: (1) leave ");
        System.out.print(">>> ");
    }

    private void printRoomInfo() {
        System.out.println("Room name: " + this.roomInfo.roomName());
        System.out.println("Players: " + this.roomInfo.playersToColors().size() + "/" + this.roomInfo.maxPlayers());

        System.out.println("\n\nPlayers in the room");
        System.out.println("--------------------------\n");
        for (Map.Entry<String, Color> player : this.roomInfo.playersToColors().entrySet()) {
            String username = player.getKey();
            Color color = player.getValue();
            if (color != null)
                System.out.print(this.colorToAnsiColor(color).toString());

            System.out.print(username);
            if (color != null)
                System.out.print(AnsiColor.ANSI_RESET.toString());

            if (username.equals(this.tuiApp.getUsername()))
                System.out.print(" (you)");

            if (color == null) {
                System.out.print(" (not ready)");
            } else {
                System.out.print(" (ready)");
            }

            System.out.print("\n");

        }
        System.out.println("\n\n");
    }

    private AnsiColor colorToAnsiColor(Color color) {
        return switch (color) {
            case YELLOW -> AnsiColor.ANSI_YELLOW;
            case GREEN -> AnsiColor.ANSI_GREEN;
            case BLUE -> AnsiColor.ANSI_BLUE;
            case RED -> AnsiColor.ANSI_RED;
        };
    }

    private void handleReady(String[] args) {
        if (args.length < 2) {
            this.showError("Color missing, please try again. Type '1 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            this.showHelp("The 'ready' command is to notify that you are ready to play. When readying up, you must specify the color of your game token.", "1 [red|blue|green|yellow]");
            return;
        }

        String colorString = args[1];
        try {
            Color color = Color.valueOf(colorString.toUpperCase());
            this.roomInfo = this.server.readyUp(this.tuiApp, color);
            this.isUserReady = true;
        } catch (IllegalArgumentException e) {
            this.showError("Invalid color. Please try again.");
            return;
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }

        this.printHeader();
        this.printRoomInfo();
    }

    private void handleLeave() {
        try{
            this.server.leaveRoom(this.tuiApp);
            this.sceneManager.setScene(new MainMenuScene( this.sceneManager, this.tuiApp));
            this.running = false;
        } catch (RemoteException e) {
            System.out.println("Failed to leave the room");
            e.printStackTrace();
        }
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        this.roomInfo = roomInfo;
        this.printHeader();
        this.printRoomInfo();
        this.promptCommand();
        this.linesToClear = 3;
    }

    @Override
    public void gameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE) {
            GameStartedUpdate update = (GameStartedUpdate) gameUpdate;
            int starterCardId = update.starterCardId();
            this.sceneManager.setScene(new StarterCardScene(this.sceneManager, this.tuiApp, starterCardId));
            this.running = false;
            this.clearLines(this.linesToClear - 1);
            System.out.println(AnsiColor.ANSI_GREEN + "\rAll players are ready! Press ENTER to continue.");
        }
    }
}
