package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

public class WelcomeScene extends Scene {

    private String errorMessage = "";

    public WelcomeScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager, tuiApp);
    }

    @Override
    public void printView() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("\n\n");
        System.out.println(AnsiColor.ANSI_RED + errorMessage + AnsiColor.ANSI_RESET);
        System.out.print("Choose a username> ");
    }

    @Override
    public void handleInput(String input) {
        if (input.length() < 2 || input.length() > 20) {
            this.errorMessage = "Username must be between 2 and 20 characters.";
            return;
        }
        this.tuiApp.setUsername(input);
        this.sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }
}
