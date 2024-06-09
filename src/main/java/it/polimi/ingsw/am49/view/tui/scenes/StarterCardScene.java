package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.view.tui.renderers.TuiCardRenderer;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.rmi.RemoteException;
import java.util.stream.IntStream;
import java.util.List;

public class StarterCardScene extends Scene {
    private boolean running = true;
    private final Server server;
    private final String username;
    private boolean sideChosen = false;
    private final int starterCardId;
    private final TuiCardRenderer renderer;

    public StarterCardScene(SceneManager sceneManager, TuiApp tuiApp, int starterCardId) {
        super(sceneManager, tuiApp);
        this.server = tuiApp.getServer();
        this.username = tuiApp.getUsername();
        this.starterCardId = starterCardId;
        this.renderer = new TuiCardRenderer(31, 5);
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
                    this.handleStarterSideChosen(false);
                    break;
                case "2":
                    this.handleStarterSideChosen(true);
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
        } catch (InvalidActionException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
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
        System.out.println("\n\n");
        System.out.println("Choose if you want to flip your starter card:");
        System.out.println("\n");
        System.out.println("(1) No          (2) Yes");
        this.printStarterCard();
        System.out.println("\n");
    }

    private void printStarterCard() {
        TuiTextureManager textureManager = TuiTextureManager.getInstance();
        this.renderer.draw(textureManager.getTexture(this.starterCardId, false), 7, 2);
        this.renderer.draw(textureManager.getTexture(this.starterCardId, true), 23, 2);
        this.renderer.print();
    }

    private void promptCommand() {
        System.out.print("Your choice >>> ");
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
