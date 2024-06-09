package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameStateChangedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.view.tui.renderers.TuiCardRenderer;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.IntStream;

public class ChooseObjectiveCardScene extends Scene {
    private boolean running = true;
    private final Server server;
    private final String username;
    private final List<Integer> objectiveCardIds;
    private boolean objectiveChosen = false;
    private final TuiCardRenderer renderer;

    public ChooseObjectiveCardScene(SceneManager sceneManager, TuiApp tuiApp, List<Integer> objectiveCardIds) {
        super(sceneManager, tuiApp);
        this.server = tuiApp.getServer();
        this.username = tuiApp.getUsername();
        this.objectiveCardIds = objectiveCardIds;
        this.renderer = new TuiCardRenderer(31, 5);
    }

    @Override
    public void play() {
        this.printHeader();
        linesToClear = 3;

        while (!this.objectiveChosen) {
            this.promptCommand();
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Empty command, please try again.");
                linesToClear = 3;
                continue;
            }

            IntStream.range(0, linesToClear).forEach(i -> clearLastLine());

            String command = parts[0];
            try {
                int choice = Integer.parseInt(command);
                if (choice > 0 && choice <= this.objectiveCardIds.size()) {
                    this.handleObjectiveChosen(this.objectiveCardIds.get(choice - 1));
                } else {
                    System.out.println("Invalid command, please try again.");
                    linesToClear = 3;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid command, please try again.");
                linesToClear = 3;
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
            this.tuiApp.getVirtualGame().getPlayerByUsername(this.username).setPersonalObjectiveId(objectiveId);
            this.objectiveChosen = true;
        } catch (InvalidActionException e) {
            throw new RuntimeException(e);
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

    private void printObjecetiveCards() {
        TuiTextureManager textureManager = TuiTextureManager.getInstance();
        this.renderer.draw(textureManager.getTexture(this.objectiveCardIds.getFirst(), false), 7, 2);
        this.renderer.draw(textureManager.getTexture(this.objectiveCardIds.get(1), false), 23, 2);
        this.renderer.print();
    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("   | Choose an objective |    ");
        System.out.println("   *************************    ");
        System.out.println("\n\n");
        System.out.println("Choose your personal objective card:");
        System.out.println("\n");
        System.out.println("(1)             (2)");
        this.printObjecetiveCards();
        System.out.println("\n");
    }

    private void promptCommand() {
        System.out.print("Your choice >>> ");
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
