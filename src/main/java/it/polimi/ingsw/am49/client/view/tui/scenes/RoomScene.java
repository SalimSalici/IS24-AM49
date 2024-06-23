package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.exceptions.RoomException;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Scene for managing room interactions in the TUI.
 */
public class RoomScene extends Scene {

    private RoomInfo roomInfo;
    private boolean isReady;
    private final RoomController roomController;

    /**
     * Constructs a RoomScene with a scene manager and room controller.
     * @param sceneManager the scene manager
     * @param roomController the room controller
     */
    public RoomScene(SceneManager sceneManager, RoomController roomController) {
        super(sceneManager);
        this.roomInfo = null;
        this.roomController = roomController;
    }

    /**
     * Sets the room information.
     * @param roomInfo the new room information
     */
    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

    /**
     * Prints the entire view of the room scene.
     */
    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n\n");
        this.printRoomDetails();
        this.printPrompt();
    }

    /**
     * Handles user input within the room scene.
     * @param input the user input string
     */
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

    /**
     * Prints the header of the room scene.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("        |    Room    |          ");
        System.out.println("        **************          ");
    }

    /**
     * Prints details of the room including players and their readiness.
     */
    private void printRoomDetails() {
        System.out.println("Room name: " + this.roomInfo.roomName());
        System.out.println("Players: " + this.roomInfo.playersToColors().size() + "/" + this.roomInfo.maxPlayers());

        System.out.println("\n\nPlayers in the room");
        System.out.println("--------------------------\n");
        for (Map.Entry<String, Color> player : this.roomInfo.playersToColors().entrySet()) {
            String username = player.getKey();
            Color color = player.getValue();

            System.out.print(this.getColoredUsername(username, color));

            if (username.equals(ClientApp.getUsername()))
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

    /**
     * Prints the prompt for user commands in the room scene.
     */
    private void printPrompt() {
        this.printInfoOrError();
        if (this.isReady)
            System.out.println("Available commands: (1) not ready | (2) leave ");
        else
            System.out.println("Available commands: (1) ready | (2) leave ");
        System.out.print(">>> ");
    }

    /**
     * Handles the 'ready' command to mark the user as ready.
     * @param args the command arguments
     */
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
            Color color = switch (colorString) {
                case "b", "blue" -> Color.BLUE;
                case "g", "green" -> Color.GREEN;
                case "y", "yellow" -> Color.YELLOW;
                case "r", "red" -> Color.RED;
                default -> throw new IllegalArgumentException("Invalid color");
            };
            this.roomInfo = this.roomController.readyUp(color);
            this.isReady = true;
            this.refreshView();
        } catch (IllegalArgumentException e) {
            this.showError("Invalid color. Please try again.");
        } catch (RemoteException | RoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the 'not ready' command to mark the user as not ready.
     * @param args the command arguments
     */
    private void handleUnready(String[] args) {
        if (args.length > 1 && args[1].equals("--help")) {
            this.showInfoMessage("The 'Not ready' command is to notify that you are no longer ready to play.");
            return;
        }
        try {
            this.showInfoMessage("Unreadying...");
            this.roomInfo = this.roomController.readyDown();
            this.isReady = false;
            this.refreshView();
        }catch (RemoteException | RoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the 'leave' command to exit the room.
     */
    private void handleLeave() {
        this.backToMainMenu(true);
    }

    /**
     * Updates the room information based on notifications.
     * @param roomInfo the updated room information
     * @param ignored an ignored parameter
     */
    public void roomUpdate(RoomInfo roomInfo, String ignored) {
        this.roomInfo = roomInfo;
        this.refreshView();
    }

    /**
     * Resets the readiness state when the scene gains focus.
     */
    @Override
    public void focus() {
        this.isReady = false;
    }
}
