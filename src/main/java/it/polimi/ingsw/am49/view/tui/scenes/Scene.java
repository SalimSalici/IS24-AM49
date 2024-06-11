package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
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
        System.out.println("\033[H\033[2J");
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
        else
            System.out.println(AnsiColor.ANSI_RED + errorMessage + AnsiColor.ANSI_RESET);
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
}
