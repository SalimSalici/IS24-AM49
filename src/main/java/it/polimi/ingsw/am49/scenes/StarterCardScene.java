package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;

import java.rmi.RemoteException;

public class StarterCardScene extends Scene {
    private final TuiApp tuiApp;
    private final boolean running = true;
    private final Server server;
    private final VirtualGame game;
    private final String username;
    private boolean sideChosen;

    public StarterCardScene(SceneManager sceneManager, TuiApp tuiApp, int starterCardId) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.server = tuiApp.getServer();
        this.game = tuiApp.getVirtualGame();
        this.username = tuiApp.getUsername();
        this.sideChosen = false;
    }

    @Override
    public void play() {
        this.printHeader();

        while (this.running) {
            this.promptCommand();
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
                continue;
            }

            String command = parts[0];
            switch (command) {
                case "1":
                    try {
                        this.server.executeAction(this.tuiApp, new ChooseStarterSideAction(this.username, true));
                    } catch (NotInGameException e) {
                        // TODO: Handle exception
                        throw new RuntimeException(e);
                    } catch (RemoteException e) {
                        // TODO: Handle exception
                        throw new RuntimeException(e);
                    }
                    break;
                case "2":
                    try {
                        this.server.executeAction(this.tuiApp, new ChooseStarterSideAction(this.username, false));
                    } catch (NotInGameException e) {
                        // TODO: Handle exception
                        throw new RuntimeException(e);
                    } catch (RemoteException e) {
                        // TODO: Handle exception
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    System.out.println("Invalid command, please try again.");
            }
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
    }

    private void promptCommand() {
        System.out.println("Do you want to flip your starter card?");
        System.out.println("Available commands: (1) yes | (2) no ");
        System.out.print(">>> ");
    }

    @Override
    public void gameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() != GameUpdateType.GAME_STARTED_UPDATE)
            return;

    }
}
