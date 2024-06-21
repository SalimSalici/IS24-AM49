package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.util.Log;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.renderers.TuiCardRenderer;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.rmi.RemoteException;

public class StarterCardScene extends Scene {

    private int starterCardId;
    private final TuiCardRenderer renderer;
    private boolean chosen = false;
    private final GameController gameController;

    public StarterCardScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
        this.renderer = new TuiCardRenderer(31, 5);
    }

    public void setStarterCardId(int id) {
        this.starterCardId = id;
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n\n");
        this.printChoices();
        this.printPrompt();
    }

    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];

        if (this.chosen && !input.equals("leave")) {
            this.showError("You have already made your choice. Please, wait while the other players are choosing.");
            return;
        }

        switch (command) {
            case "1":
                this.handleChoice(false);
                break;
            case "2":
                this.handleChoice(true);
                break;
            case "leave":
                this.handleLeave();
                break;
            case "":
                this.refreshView();
                break;
            default:
                this.showError("Invalid command, please try again.");
        }
    }

    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("       | Starter card |        ");
        System.out.println("       ****************        ");
    }

    private void printChoices() {
        System.out.println("Choose if you want to flip your starter card:");
        System.out.println("\n");
        System.out.println("(1) No          (2) Yes");
        this.printStarterCard();
    }

    private void printStarterCard() {
        TuiTextureManager textureManager = TuiTextureManager.getInstance();
        this.renderer.draw(textureManager.getTexture(this.starterCardId, false), 7, 2);
        this.renderer.draw(textureManager.getTexture(this.starterCardId, true), 23, 2);
        this.renderer.print();
        System.out.println("\n");
    }

    private void printPrompt() {
        if (this.chosen)
            this.infoMessage = "Please, wait while the other players are choosing.";
        this.printInfoOrError();

        if (this.chosen)
            System.out.println("Available commands: leave");
        else
            System.out.println("Available commands: (1) Not flipped | (2) Flipped | leave ");
        System.out.print(">>> ");
    }

    private void handleChoice(boolean flipped) {
        try {
            this.chosen = true;
            this.gameController.chooseStarterSide(flipped);
            this.refreshView();
        } catch (InvalidActionException | NotInGameException | NotYourTurnException e) {
            this.chosen = false;
            this.showError(e.getMessage());
        } catch (RemoteException e) {
            this.chosen = false;
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void handleLeave() {
        this.backToMainMenu(true);
    }

    @Override
    public void focus() {
        this.chosen = false;
    }
}
