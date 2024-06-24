package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientConfig;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.config.StaticConfig;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.exceptions.RoomException;
import it.polimi.ingsw.am49.common.util.Log;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.textures.AnsiColor;

import java.rmi.RemoteException;

/**
 * Abstract class representing a scene in the TUI.
 */
public abstract class Scene {

    protected final SceneManager sceneManager;
    protected String errorMessage = "";
    protected String infoMessage = "";

    /**
     * Constructor for Scene.
     * @param sceneManager The scene manager that manages scene transitions.
     */
    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Abstract method to print the view of the scene.
     */
    public abstract void printView();

    /**
     * Abstract method to handle user input within the scene.
     * @param input The user input as a String.
     */
    public abstract void handleInput(String input);

    /**
     * Method called when the scene gains focus.
     */
    public void focus() {}

    /**
     * Method called when the scene loses focus.
     */
    public void unfocus() {}

    /**
     * Clears the screen by printing new lines or escape characters based on configuration.
     */
    protected void clearScreen() {
        System.out.println("\n" + "-".repeat(150));
        if (ClientConfig.getColors())
            System.out.println("\033[H\033[2J");
        else System.out.print("\n".repeat(60));
    }

    /**
     * Refreshes the view if the current scene is focused.
     */
    protected void refreshView() {
        synchronized (this.sceneManager) {
            if (this.sceneManager.isFocused(this))
                this.printView();
        }
    }

    /**
     * Displays an informational message and refreshes the view.
     * @param message The message to display.
     */
    protected void showInfoMessage(String message) {
        this.infoMessage = message;
        this.refreshView();
    }

    /**
     * Displays a help message with an example and refreshes the view.
     * @param message The help message.
     * @param example An example to illustrate the message.
     */
    protected void showHelpMessage(String message, String example) {
        this.showInfoMessage(message + "\n" + example);
    }

    /**
     * Displays an error message and refreshes the view.
     * @param message The error message.
     */
    protected void showError(String message) {
        this.errorMessage = message;
        this.refreshView();
    }

    /**
     * Prints either an informational or error message based on the current state.
     */
    protected void printInfoOrError() {
        if (this.errorMessage.isEmpty())
            System.out.println(this.infoMessage);
        else if (ClientConfig.getColors())
            System.out.println(AnsiColor.ANSI_RED + errorMessage + AnsiColor.ANSI_RESET);
        else
            System.out.println(errorMessage);
        this.infoMessage = "";
        this.errorMessage = "";
    }

    /**
     * Handles the action to go back to the main menu, optionally leaving the room.
     * @param leaveRoom Specifies whether to leave the room.
     */
    protected void backToMainMenu(boolean leaveRoom) {
        this.unfocus();
        if (leaveRoom) {
            new Thread(() -> {
                try {
                    this.sceneManager.getRoomController().leaveRoom();
                } catch (RoomException | RemoteException e) {
                    Log.getLogger().severe("Exception while leaving room from RoomScene: " + e.getMessage());
                }
            }).start();
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        // TODO: stop heartbeat when leaving
//        this.tuiApp.stopHeartbeat();
        this.sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }

    /**
     * Returns the username colored based on the player's color setting.
     * @param player The virtual player whose username is to be colored.
     * @return The colored username or null if the player is null.
     */
    protected String getColoredUsername(VirtualPlayer player) {
        if (player == null) return null;
        String offline = player.getPlaying() ? "" : " (offline)";
        return this.getColoredUsername(player.getUsername(), player.getColor()) + offline;
    }

    /**
     * Returns the username colored based on the specified color.
     * @param username The username to color.
     * @param color The color to apply.
     * @return The colored username or the username with the color name appended if colors are disabled.
     */
    protected String getColoredUsername(String username, Color color) {
        if (color == null) return username;
        if (ClientConfig.getColors())
            return AnsiColor.fromColor(color) + username + AnsiColor.ANSI_RESET;
        else
            return username + "[" + color.name() + "]";
    }
}
