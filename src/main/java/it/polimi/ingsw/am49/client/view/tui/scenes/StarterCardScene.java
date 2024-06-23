package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotInGameException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.renderers.TuiCardRenderer;
import it.polimi.ingsw.am49.client.view.tui.textures.TuiTextureManager;

import java.rmi.RemoteException;

/**
 * Scene for choosing whether to flip the starter card in the game.
 */
public class StarterCardScene extends Scene {

    private int starterCardId;
    private final TuiCardRenderer renderer;
    private boolean chosen = false;
    private final GameController gameController;

    /**
     * Constructs a StarterCardScene with the specified scene manager and game controller.
     * @param sceneManager the scene manager
     * @param gameController the game controller
     */
    public StarterCardScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
        this.renderer = new TuiCardRenderer(31, 5);
    }

    /**
     * Sets the ID of the starter card.
     * @param id the starter card ID
     */
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

    /**
     * Prints the header of the scene.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("       | Starter card |        ");
        System.out.println("       ****************        ");
    }

    /**
     * Prints the choices available for flipping the starter card.
     */
    private void printChoices() {
        System.out.println("Choose if you want to flip your starter card:");
        System.out.println("\n");
        System.out.println("(1) No          (2) Yes");
        this.printStarterCard();
    }

    /**
     * Prints the visual representation of the starter card.
     */
    private void printStarterCard() {
        TuiTextureManager textureManager = TuiTextureManager.getInstance();
        this.renderer.draw(textureManager.getTexture(this.starterCardId, false), 7, 2);
        this.renderer.draw(textureManager.getTexture(this.starterCardId, true), 23, 2);
        this.renderer.print();
        System.out.println("\n");
    }

    /**
     * Prints the prompt for user input.
     */
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

    /**
     * Handles the choice of flipping the starter card.
     * @param flipped whether the card should be flipped
     */
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

    /**
     * Handles the action to leave the current scene.
     */
    private void handleLeave() {
        this.backToMainMenu(true);
    }

    @Override
    public void focus() {
        this.chosen = false;
    }
}
