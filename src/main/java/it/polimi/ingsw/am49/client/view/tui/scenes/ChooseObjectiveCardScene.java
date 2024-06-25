package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotInGameException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Observer;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.renderers.TuiCardRenderer;
import it.polimi.ingsw.am49.client.view.tui.textures.TuiTextureManager;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Scene for choosing an objective card in the game.
 */
public class ChooseObjectiveCardScene extends Scene implements Observer {

    private VirtualGame game;
    private List<Integer> objectiveCardIds;
    private final TuiCardRenderer renderer;
    private boolean chosen = false;
    private final GameController gameController;

    /**
     * Constructor for ChooseObjectiveCardScene.
     * @param sceneManager Manages the scenes.
     * @param gameController Controls the game logic.
     */
    public ChooseObjectiveCardScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
        this.renderer = new TuiCardRenderer(31, 5);
    }

    /**
     * Sets the objective card IDs available for selection.
     * @param objectiveCardIds List of objective card IDs.
     */
    public void setObjectiveCardIds(List<Integer> objectiveCardIds) {
        this.chosen = false;
        this.objectiveCardIds = objectiveCardIds;
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
            case "1", "2":
                try {
                    int choice = Integer.parseInt(command);
                    if (choice > 0 && choice <= this.objectiveCardIds.size()) {
                        this.handleObjectiveChosen(this.objectiveCardIds.get(choice - 1));
                    } else {
                        this.showError("Invalid choice, please try again.");
                    }
                } catch (NumberFormatException e) {
                    this.showError("Invalid choice, please try again.");
                }
                break;
            case "leave":
                this.handleLeave();
                break;
            case "":
                this.refreshView();
                break;
            default:
                this.showError("Invalid choice, please try again.");
        }
    }

    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("   | Choose an objective |    ");
        System.out.println("   *************************    ");
    }

    private void printChoices() {
        System.out.println("Choose your personal objective card:");
        System.out.println("\n");
        System.out.println("(1)             (2)");
        this.printObjecetiveCards();
    }

    private void printObjecetiveCards() {
        TuiTextureManager textureManager = TuiTextureManager.getInstance();
        this.renderer.draw(textureManager.getTexture(this.objectiveCardIds.getFirst(), false), 7, 2);
        this.renderer.draw(textureManager.getTexture(this.objectiveCardIds.get(1), false), 23, 2);
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
            System.out.println("Available commands: (1) first choice | (2) second choice | leave ");
        System.out.print(">>> ");
    }

    /**
     * Handles the selection of an objective card.
     * @param objectiveId The ID of the chosen objective card.
     */
    private void handleObjectiveChosen(int objectiveId) {
        try {
            this.chosen = true;
            this.sceneManager.getVirtualGame().getPlayerByUsername(ClientApp.getUsername()).setPersonalObjectiveId(objectiveId);
            this.gameController.chooseObjective(objectiveId);
            this.refreshView();
        } catch (InvalidActionException | NotInGameException | NotYourTurnException e) {
            this.chosen = false;
            this.showError(e.getMessage());
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
        this.game = this.sceneManager.getVirtualGame();
        this.game.addObserver(this);
        this.printView();
    }

    @Override
    public void unfocus() {
        this.game.deleteObserver(this);
    }

    @Override
    public void update() {
        if (this.game.getGameState() == GameStateType.PLACE_CARD) {
            this.sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
        }
    }
}
