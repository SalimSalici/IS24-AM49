package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.util.List;
import java.util.Scanner;

public abstract class Scene {
    protected final SceneManager sceneManager;
    protected final TuiApp tuiApp;
    protected final Scanner scanner;

    protected int linesToClear;

    public Scene(SceneManager sceneManager, TuiApp tuiApp) {
        this.sceneManager = sceneManager;
        this.tuiApp = tuiApp;
        this.scanner = new Scanner(System.in);
    }

    public abstract void play();

    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException {}

    public void gameUpdate(GameUpdate gameUpdate) throws InvalidSceneException {}

    protected void clearScreen() {
        System.out.println("-".repeat(150));
        System.out.println("\033[H\033[2J");
    }

    protected void clearLastLine() {
        System.out.print("\033[1A"); // Move cursor up by one line
        System.out.print("\033[2K"); // Erase the line content

    }

    protected void clearLine() {
        System.out.print("\033[2K"); // Erase the line content
    }

    protected void moveCursorUp(int n) {
        for (int i = 0; i < n; i++)
            System.out.print("\033[1A"); // Move cursor up by one line
    }

    protected void moveCursorUp() {
        System.out.print("\033[1A"); // Move cursor up by one line
    }

    protected void clearLines(int n) {
        if (n <= 0) return;
        this.clearLine();
        n--;
        while (n > 0) {
            this.moveCursorUp();
            this.clearLine();
            n--;
        }
    }

    protected void showHelp(String message, String example) {
        showHelp(message, example, 5);
    }

    protected void showHelp(String message, String example, int linesToClear) {
        System.out.println(message);
        System.out.println("Example usage: " + example);
        this.linesToClear = linesToClear;
    }

    protected void showError(String message) {
        this.showError(message, 4);
    }

    protected void showError(String message, int linesToClear) {
        System.out.println(AnsiColor.ANSI_RED + message + AnsiColor.ANSI_RESET);
        this.linesToClear = linesToClear;
    }

    protected void printEndGameContent() {
        List<VirtualPlayer> ranking = this.tuiApp.getVirtualGame().getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            VirtualPlayer player = ranking.get(i);
            System.out.println(
                    (i+1) + " " + AnsiColor.fromColor(player.getColor()) + player.getUsername() + AnsiColor.ANSI_RESET + ": " +
                    player.getPoints() + " points and " +
                    player.getCompletedObjectives() + " completed objectives."
            );
        }
    }
}
