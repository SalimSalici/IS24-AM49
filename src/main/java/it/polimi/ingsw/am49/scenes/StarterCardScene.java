package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;

import java.rmi.RemoteException;
import java.util.stream.IntStream;
import java.util.List;

public class StarterCardScene extends Scene {
    private final TuiApp tuiApp;
    private boolean running = true;
    private final Server server;
    private final String username;
    private boolean sideChosen = false;

    private final int starterCardId;

    public StarterCardScene(SceneManager sceneManager, TuiApp tuiApp, int starterCardId) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.server = tuiApp.getServer();
        this.username = tuiApp.getUsername();
        this.starterCardId = starterCardId;
    }

    @Override
    public void play() {
        this.printHeader();
        linesToClear = 2;

        while (!this.sideChosen) {
            this.promptCommand();
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Empty command, please try again.");
                linesToClear = 2;
                continue;
            }

            IntStream.range(0, linesToClear).forEach(i -> clearLastLine());

            String command = parts[0];
            switch (command) {
                case "1":
                    this.handleStarterSideChosen(true);
                    break;
                case "2":
                    this.handleStarterSideChosen(false);
                    break;
                default:
                    System.out.println("Invalid command, please try again.");
                    linesToClear = 5;
            }
        }

        this.printHeader();
        System.out.println("Waiting for other players...");
        synchronized (this) {
            while (this.running) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void handleStarterSideChosen(boolean flipped) {
        try {
            this.server.executeAction(this.tuiApp, new ChooseStarterSideAction(this.username, flipped));
            this.sideChosen = true;
        } catch (NotInGameException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (NotYourTurnException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("       | Starter card |        ");
        System.out.println("       ****************        ");
        System.out.println("\n\n\n");
        System.out.println("Your starter card is: " + this.starterCardId);
        System.out.println("\n\n\n");

    }

    private void promptCommand() {
        System.out.println("Do you want to flip your starter card?\n");
        System.out.println("Available commands: (1) yes | (2) no ");
        System.out.print(">>> ");
    }

    public void gameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() == GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE) {
            ChoosableObjectivesUpdate update = (ChoosableObjectivesUpdate) gameUpdate;
            List<Integer> objectiveCardIds = update.objectiveCards();
            this.sceneManager.setScene(new ChooseObjectiveCardScene(this.sceneManager, this.tuiApp, objectiveCardIds));
            synchronized (this) {
                this.running = false;
                this.notifyAll();
            }
        }
    }
}
