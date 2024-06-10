package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

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
        System.out.println("-".repeat(150));
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

//    protected void clearLine() {
//        System.out.print("\033[2K");
//    }
//
//    protected void moveCursorUp(int n) {
//        for (int i = 0; i < n; i++)
//            System.out.print("\033[1A");
//    }
//
//    protected void moveCursorUp() {
//        this.moveCursorUp(1);
//    }
//
//    protected void clearLines(int n) {
//        if (n <= 0) return;
//        this.clearLine();
//        n--;
//        while (n > 0) {
//            this.moveCursorUp();
//            this.clearLine();
//            n--;
//        }
//    }
}
