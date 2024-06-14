package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.config.StaticConfig;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.rmi.RemoteException;

public abstract class Scene {

    protected final SceneManager sceneManager;
    protected final TuiApp tuiApp;
    protected String errorMessage = "";
    protected String infoMessage = "";

    public Scene(SceneManager sceneManager, TuiApp tuiApp) {
        this.sceneManager = sceneManager;
        this.tuiApp = tuiApp;
    }

    public abstract void printView();
    public abstract void handleInput(String input);
    public void focus() {}
    public void unfocus() {}

    protected void clearScreen() {
        System.out.println("\n" + "-".repeat(150));
        if (StaticConfig.tuiColors)
            System.out.println("\033[H\033[2J");
        else System.out.print("\n".repeat(60));
    }

    protected void refreshView() {
        synchronized (this.sceneManager) {
            if (this.sceneManager.isFocused(this))
                this.printView();
        }
    }

    protected void showInfoMessage(String message) {
        this.infoMessage = message;
        this.refreshView();
    }

    protected void showHelpMessage(String message, String example) {
        this.showInfoMessage(message + "\n" + example);
    }

    protected void showError(String message) {
        this.errorMessage = message;
        this.refreshView();
    }

    protected void printInfoOrError() {
        if (this.errorMessage.isEmpty())
            System.out.println(this.infoMessage);
        else if (StaticConfig.tuiColors)
            System.out.println(AnsiColor.ANSI_RED + errorMessage + AnsiColor.ANSI_RESET);
        else
            System.out.println(errorMessage);
        this.infoMessage = "";
        this.errorMessage = "";
    }

    protected void backToMainMenu(boolean leaveRoom) {
        this.unfocus();
        if (leaveRoom) {
            new Thread(() -> {
                try {
                    this.tuiApp.getServer().leaveRoom(this.tuiApp);
                } catch (RoomException | RemoteException e) {
                    Log.getLogger().severe("Exception while leaving room from RoomScene: " + e.getMessage());
                }
            }).start();
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        this.sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }

    protected String getColoredUsername(String username, Color color) {
        if (color == null) return username;
        if (StaticConfig.tuiColors)
            return AnsiColor.fromColor(color) + username + AnsiColor.ANSI_RESET;
        else
            return username + "[" + color.name() + "]";
    }
}
