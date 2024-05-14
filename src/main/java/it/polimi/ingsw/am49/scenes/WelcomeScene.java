package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;

import java.util.stream.IntStream;

public class WelcomeScene extends Scene {

    private final TuiApp tuiApp;

    public WelcomeScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager);
        this.tuiApp = tuiApp;
    }

    @Override
    public void play() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("\n\n\n");

        boolean valid = false;

        String username = null;
        while (!valid) {
            System.out.print("Choose a username> ");
            linesToClear = 2;
            username = this.scanner.nextLine();
            valid = this.isUsernameValid(username);
            if (!valid)
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("Invalid username, please choose a username between 2 and 20 characters.");
        }

        this.tuiApp.setUsername(username);
        this.sceneManager.setScene(new MainMenuScene(this.sceneManager, this.tuiApp));
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }
}
