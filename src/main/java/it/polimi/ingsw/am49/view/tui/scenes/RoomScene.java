package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.rmi.RemoteException;
import java.util.Map;

public class RoomScene extends Scene {

    private RoomInfo roomInfo;
    private boolean isReady;

    public RoomScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager, tuiApp);
        this.roomInfo = null;
        this.isReady = false;
    }

    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n\n");
        this.printRoomDetails();
        this.printPrompt();
    }

    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];
        switch (command) {
            case "1":
                if (this.isReady)
                    this.handleUnready(parts);
                else
                    this.handleReady(parts);
                break;
            case "2":
                this.handleLeave();
                break;
            case "":
                this.refreshView();
                break;
            default:
                this.showError("Invalid command, please try again.");
        }
    }

    private AnsiColor colorToAnsiColor(Color color) {
        return switch (color) {
            case YELLOW -> AnsiColor.ANSI_YELLOW;
            case GREEN -> AnsiColor.ANSI_GREEN;
            case BLUE -> AnsiColor.ANSI_BLUE;
            case RED -> AnsiColor.ANSI_RED;
        };
    }

    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        |    Room    |          ");
        System.out.println("        **************          ");
    }

    private void printRoomDetails() {
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
                System.out.print(AnsiColor.ANSI_RESET);

            if (username.equals(this.tuiApp.getUsername()))
                System.out.print(" (you)");

            if (color == null) {
                System.out.print(" (not ready)");
            } else {
                System.out.print(" (ready)");
            }

            System.out.print("\n");

        }
        System.out.println("\n");
    }

    private void printPrompt() {
        this.printInfoOrError();
        if (this.isReady)
            System.out.println("Available commands: (1) not ready | (2) leave ");
        else
            System.out.println("Available commands: (1) ready | (2) leave ");
        System.out.print(">>> ");
    }

    private void handleReady(String[] args) {
        if (args.length < 2) {
            this.showError("Color missing, please try again. Type '1 --help' for more information about this command.");
            return;
        }
        if (args[1].equals("--help")) {
            this.showHelpMessage("The 'ready' command is to notify that you are ready to play. When readying up, you must specify the color of your game token.", "1 [red|blue|green|yellow]");
            return;
        }

        String colorString = args[1];
        try {
            this.showInfoMessage("Readying up...");
            Color color = Color.valueOf(colorString.toUpperCase());
            this.roomInfo = this.tuiApp.getServer().readyUp(this.tuiApp, color);
            this.isReady = true;
            this.refreshView();
        } catch (IllegalArgumentException e) {
            this.showError("Invalid color. Please try again.");
            return;
        } catch (RemoteException | RoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void handleUnready(String[] args) {
        if (args.length > 1 && args[1].equals("--help")) {
            this.showInfoMessage("The 'Not ready' command is to notify that you are no longer ready to play.");
            return;
        }
        try {
            this.showInfoMessage("Unreadying...");
            this.roomInfo = this.tuiApp.getServer().readyDown(this.tuiApp);
            this.isReady = false;
            this.refreshView();
        }catch (RemoteException | RoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void handleLeave() {
        new Thread(() -> {
            try {
                this.tuiApp.getServer().leaveRoom(this.tuiApp);
            } catch (RoomException | RemoteException e) {
                Log.getLogger().severe("Exception while leaving room from RoomScene: " + e.getMessage());
            }
        }).start();
        this.sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }

    public void roomUpdate(RoomInfo roomInfo, String message) {
        this.roomInfo = roomInfo;
        this.showInfoMessage(message);
//        this.refreshView();
    }
}
