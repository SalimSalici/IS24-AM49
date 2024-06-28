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

import java.util.List;

/**
 * Scene for choosing an objective card in the game.
 */
public class ChooseObjectiveCardScene extends Scene implements Observer {

    /**
     * The virtual game being played.
     */
    private VirtualGame game;

    /**
     * The list of objective card IDs available for selection.
     */
    private List<Integer> objectiveCardIds;

    /**
     * The ID of the starter card.
     */
    private int starterCardId;

    /**
     * The renderer for displaying cards.
     */
    private final TuiCardRenderer renderer;

    /**
     * Flag indicating if an objective card has been chosen.
     */
    private boolean chosen = false;

    /**
     * The game controller for handling game actions.
     */
    private final GameController gameController;

    /**
     * Constructor for ChooseObjectiveCardScene.
     *
     * @param sceneManager Manages the scenes.
     * @param gameController Controls the game logic.
     */
    public ChooseObjectiveCardScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
        this.renderer = new TuiCardRenderer(31, 11);
    }

    /**
     * Sets the objective card IDs available for selection.
     *
     * @param objectiveCardIds List of objective card IDs.
     */
    public void setObjectiveCardIds(List<Integer> objectiveCardIds) {
        this.chosen = false;
        this.objectiveCardIds = objectiveCardIds;
        this.starterCardId = this.sceneManager.getVirtualGame().getPlayerByUsername(ClientApp.getUsername()).getStarterCard().id();
    }

    /**
     * Prints the current view of the choose objective card scene.
     */
    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n\n");
        this.printChoices();
        this.printPrompt();
    }

    /**
     * Handles user input in the choose objective card scene.
     *
     * @param input the user input string
     */
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

    /**
     * Prints the header of the choose objective card scene.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("   | Choose an objective |    ");
        System.out.println("   *************************    ");
    }

    /**
     * Prints the available choices for objective cards.
     */
    private void printChoices() {
        System.out.println("Choose your personal objective card:");
        System.out.println("\n");
        System.out.println("(1)             (2)");
        this.printObjectiveCards();
    }

    /**
     * Prints the objective cards using the renderer.
     */
    private void printObjectiveCards() {
        TuiTextureManager textureManager = TuiTextureManager.getInstance();
        this.renderer.draw(textureManager.getTexture(this.objectiveCardIds.getFirst(), false), 7, 2);
        this.renderer.draw(textureManager.getTexture(this.objectiveCardIds.get(1), false), 23, 2);
        this.renderer.draw(textureManager.getTexture(this.starterCardId, false), 7, 8);
        this.renderer.print();
        System.out.println("       ^ your starter card");
        System.out.println("\n");
    }

    /**
     * Prints the prompt for user commands in the choose objective card scene.
     */
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
     *
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
        this.gameController.leave();
    }

    /**
     * Focuses on the choose objective card scene, initializing necessary components.
     */
    @Override
    public void focus() {
        this.chosen = false;
        this.game = this.sceneManager.getVirtualGame();
        this.game.addObserver(this);
        this.printView();
    }

    /**
     * Unfocuses from the choose objective card scene, removing it as an observer from the game.
     */
    @Override
    public void unfocus() {
        this.game.deleteObserver(this);
    }

    /**
     * Updates the choose objective card scene based on game state changes.
     */
    @Override
    public void update() {
        if (this.game.getGameState() == GameStateType.PLACE_CARD) {
            this.sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
        }
    }
}
