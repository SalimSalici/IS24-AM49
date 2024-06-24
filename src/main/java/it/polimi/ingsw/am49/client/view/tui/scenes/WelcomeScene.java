package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.textures.AnsiColor;

/**
 * The WelcomeScene class represents the initial scene displayed to the user.
 * It prompts the user to choose a username and handles the input validation.
 */
public class WelcomeScene extends Scene {

    private String errorMessage = "";

    /**
     * Constructs a WelcomeScene with the specified SceneManager.
     *
     * @param sceneManager the SceneManager to manage scene transitions
     */
    public WelcomeScene(SceneManager sceneManager) {
        super(sceneManager);
    }

    /**
     * Prints the welcome view to the console, including any error messages.
     */
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

    /**
     * Handles the user input for the username. Validates the input length and
     * sets the username if valid, otherwise sets an error message.
     *
     * @param input the username input provided by the user
     */
    @Override
    public void handleInput(String input) {
        if (input.length() < 2 || input.length() > 20) {
            this.errorMessage = "Username must be between 2 and 20 characters.";
            return;
        }
        ClientApp.setUsername(input);
        this.sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }

    @Override
    public void focus() {
        this.printView();
    }
}
