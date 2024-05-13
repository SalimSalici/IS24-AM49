package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameStateChangedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.PlaceCard;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;

import java.rmi.RemoteException;
import java.util.List;

public class ChooseObjectiveCardScene extends Scene {
    private final TuiApp tuiApp;
    private boolean running = true;
    private final Server server;
    private final String username;
    private final List<Integer> objectiveCardIds;
    private boolean objectiveChosen = false;

    public ChooseObjectiveCardScene(SceneManager sceneManager, TuiApp tuiApp, List<Integer> objectiveCardIds) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.server = tuiApp.getServer();
        this.username = tuiApp.getUsername();
        this.objectiveCardIds = objectiveCardIds;
    }

    @Override
    public void play() {
        this.printHeader();

        while (!this.objectiveChosen) {
            this.promptCommand();
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
                continue;
            }

            String command = parts[0];
            try {
                int choice = Integer.parseInt(command);
                if (choice > 0 && choice <= this.objectiveCardIds.size()) {
                    this.handleObjectiveChosen(this.objectiveCardIds.get(choice - 1));
                } else {
                    System.out.println("Invalid command, please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid command, please try again.");
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

    private synchronized void handleObjectiveChosen(int objectiveId) {
        try {
            this.server.executeAction(this.tuiApp, new ChooseObjectiveAction(this.username, objectiveId));
            this.objectiveChosen = true;
        } catch (NotYourTurnException e) {
            throw new RuntimeException(e);
        } catch (NotInGameException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("   | Choose an objective |    ");
        System.out.println("   *************************    ");
        System.out.println("\n\n\n");
        System.out.println("Available objective cards:");

        for (int i = 0; i < this.objectiveCardIds.size(); i++) {
            System.out.println((i + 1) + ". Objective Card " + this.objectiveCardIds.get(i));
        }
        System.out.println("\n\n\n");
    }

    private void promptCommand() {
        System.out.println("Choose an objective card by entering the corresponding number:");
        System.out.print(">>> ");
    }

    public synchronized void gameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() == GameUpdateType.GAME_STATE_UPDATE) {
            if (((GameStateChangedUpdate)gameUpdate).gameStateType() == GameStateType.PLACE_CARD) {

                this.sceneManager.setScene(new GameOverviewScene(this.sceneManager, this.tuiApp));
                synchronized (this) {
                    this.running = false;
                    this.notifyAll();
                }
            }
        }
    }
}
