package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.util.Log;

import java.util.Random;
import java.util.stream.IntStream;

public class WelcomeScene extends Scene {

    public WelcomeScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager, tuiApp);
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
            if (!valid) {
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("Invalid username, please choose a username between 2 and 20 characters.");
            }
        }

        // TODO: remove logger
        Log.initializeLogger(username + new Random().nextInt(100000) + ".log", false);
        Log.getLogger().info("Logging in with username '" + username + "'");

        this.tuiApp.setUsername(username);
        this.sceneManager.setScene(new MainMenuScene(this.sceneManager, this.tuiApp));
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }
}
