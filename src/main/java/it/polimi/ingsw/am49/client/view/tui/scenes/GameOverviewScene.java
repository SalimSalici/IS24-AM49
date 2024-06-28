package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.common.enumerations.DrawPosition;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotInGameException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Observer;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.renderers.TuiDrawAreaRenderer;
import it.polimi.ingsw.am49.client.view.tui.renderers.TuiPlayerRenderer;

import java.util.List;

/**
 * Represents the game overview scene in the text-based user interface.
 */
public class GameOverviewScene extends Scene implements Observer {

    /**
     * The virtual game being played.
     */
    private VirtualGame game;

    /**
     * The renderer for the draw area.
     */
    private TuiDrawAreaRenderer drawAreaRenderer;

    /**
     * The renderer for the focused player.
     */
    private TuiPlayerRenderer focusedPlayerRenderer;

    /**
     * The game controller for handling game actions.
     */
    private final GameController gameController;

    /**
     * Constructs a GameOverviewScene with the specified scene manager and game controller.
     *
     * @param sceneManager the scene manager
     * @param gameController the game controller
     */
    public GameOverviewScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
    }

    /**
     * Prints the current view of the game overview scene.
     */
    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println();
        this.printPlayerList();
        System.out.println("\n");
        this.printDrawArea();
        System.out.println("\n");
        this.printFocusedPlayer();
        System.out.println("\n\n");
        this.printPrompt();
    }

    /**
     * Prints the header of the game overview scene.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("     | Game Overview |        ");
        System.out.println("     *****************        ");
        System.out.println("\nCurrent state: " + this.game.getGameState());
        System.out.println("\nRound: " + this.game.getRound() + " - Turn: " + this.game.getTurn());
        System.out.println("\nEnd game: " + this.game.getEndGame() + " - Final round: " + this.game.getFinalRound());
    }

    /**
     * Prints the list of players in the game.
     */
    private void printPlayerList() {
        System.out.println("------ Players " + "-".repeat(95));
        System.out.print("\nPlayers *current* [points]:");
        List<VirtualPlayer> players = this.game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            VirtualPlayer p = players.get(i);
            boolean isCurrentPlayer = p.equals(this.game.getCurrentPlayer());

            System.out.print(" (" + (i+1) + ") ");
            if (ClientApp.getUsername().equals(p.getUsername())) System.out.print("you->");
            if (isCurrentPlayer) System.out.print("*");
            System.out.print(this.getColoredUsername(p));
            if (isCurrentPlayer) System.out.print("*");
            System.out.print(" [" + p.getPoints() + "]    ");
        }
        System.out.print("\n");
    }

    /**
     * Prints the draw area.
     */
    private void printDrawArea() {
        System.out.println("------ Draw Area " + "-".repeat(95));
        System.out.println();
        this.drawAreaRenderer.print();
    }

    /**
     * Prints the focused player's information.
     */
    private void printFocusedPlayer() {
        System.out.println("------ Focused Player " + "-".repeat(95));
        System.out.println();
        this.focusedPlayerRenderer.print();
    }

    /**
     * Prints the prompt for user commands in the game overview scene.
     */
    private void printPrompt() {
        this.printInfoOrError();
        System.out.print("Available commands: ");
        System.out.print("(1) chat | (2) focus player | (3) view player | ");
        if (this.canDraw())
            System.out.print("(4) draw card | ");
        if (this.game.getGameState() == GameStateType.END_GAME)
            System.out.print("(r) show ranking | ");
        System.out.print("leave");
        System.out.print("\n>>> ");
    }

    /**
     * Handles user input in the game overview scene.
     *
     * @param input the user input string
     */
    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];
        switch (command) {
            case "1", "chat":
                this.handleChat(parts);
                break;
            case "2", "focus":
                this.handleFocusPlayer(parts);
                break;
            case "3", "view":
                this.handleViewPlayer(parts);
                break;
            case "4", "draw":
                this.handleDrawCard(parts);
                break;
            case "r", "rankings":
                this.handleRankings(parts);
                break;
            case "leave":
                this.handleLeave(parts);
                break;
            default:
                this.showError("Invalid command, please try again.");
        }
    }

    /**
     * Handles the chat command.
     *
     * @param args the command arguments
     */
    private void handleChat(String[] args) {
        if (args.length == 2 && args[1].equals("--help")) {
            this.showHelpMessage("Shows the chat.", "1");
            return;
        }
        this.sceneManager.switchScene(SceneType.CHAT_SCENE);
    }

    /**
     * Handles the focus player command.
     *
     * @param args the command arguments
     */
    private void handleFocusPlayer(String[] args) {
        if (args.length >= 2 && args[1].equals("--help")) {
            this.showHelpMessage("Shows the hand of the i-th player. If you don't pass a parameter, your hand will be focused" +
                    " You can see the player above, in the 'Players' section.", "focus 2  (or equivalently)  2 2");
            return;
        }
        try {
            VirtualPlayer player = args.length < 2 ?
                    this.getClientPlayer() :
                    this.game.getPlayers().get(Integer.parseInt(args[1]) - 1);
            if (player != null) {
                boolean hiddenHand = !player.getUsername().equals(ClientApp.getUsername());
                boolean hiddenPersonalObjective = this.game.getGameState() != GameStateType.END_GAME && hiddenHand;
                this.focusedPlayerRenderer = new TuiPlayerRenderer(player, hiddenHand, hiddenPersonalObjective, this.game.getCommonObjectives());
            } else
                this.showError("Unexpected error occurred. If it persists, please restart the client.");
            this.refreshView();
        } catch (NumberFormatException e) {
            this.showError("Argument must be a number. Please try again.");
        } catch (IndexOutOfBoundsException e) {
            this.showError("Argument must be between 1 and " + this.game.getPlayers().size());
        }
    }

    /**
     * Handles the view player command.
     *
     * @param args the command arguments
     */
    private void handleViewPlayer(String[] args) {
        if (args.length >= 2 && args[1].equals("--help")) {
            this.showHelpMessage("Shows the board of the i-th player. If you don't pass a parameter, your hand will be focused." +
                    " You can see the player above, in the 'Players' section.", "view 2  (or equivalently)  2 2");
            return;
        }
        try {
            VirtualPlayer player = args.length < 2 ?
                    this.getClientPlayer() :
                    this.game.getPlayers().get(Integer.parseInt(args[1]) - 1);
            if (player != null) {
                this.sceneManager.switchScene(player);
            } else
                this.showError("Unexpected error occurred. If it persists, please restart the client.");
        } catch (NumberFormatException e) {
            this.showError("Argument must be a number. Please try again.");
        } catch (IndexOutOfBoundsException e) {
            this.showError("Argument must be between 1 and " + this.game.getPlayers().size());
        }
    }

    /**
     * Handles the draw card command.
     *
     * @param args the command arguments
     */
    private void handleDrawCard(String[] args) {
        if (args.length >= 2 && args[1].equals("--help")) {
            this.showHelpMessage("Draw the i-th card. Cards are numbered above, in the draw area.", "draw 5  (or equivalently)  4 5");
            return;
        }
        if (!this.canDraw()) {
            this.showError("You cannot draw a card now.");
            return;
        }
        if (args.length < 2) {
            this.showError("You must specify a card.");
            return;
        }
        try {
            int choice = Integer.parseInt(args[1]);
            this.gameController.drawCard(this.getDrawPosition(choice), this.getDrawId(choice));
            this.refreshView();
        } catch (NumberFormatException e) {
            this.showError("Argument must be a number. Please try again.");
        } catch (IndexOutOfBoundsException e) {
            this.showError("Argument must be between 1 and 6.");
        } catch (NotYourTurnException | InvalidActionException e) {
            this.showError(e.getMessage());
        } catch (NotInGameException e) {
            this.showError("It seems like you are not in a game. Please restart the application.");
        }
    }

    /**
     * Retrieves the draw position based on the user's choice.
     *
     * @param choice the user's choice
     * @return the draw position
     * @throws IndexOutOfBoundsException if the choice is invalid
     */
    private DrawPosition getDrawPosition(int choice) throws IndexOutOfBoundsException {
        return switch (choice) {
            case 1 -> DrawPosition.RESOURCE_DECK;
            case 4 -> DrawPosition.GOLD_DECK;
            case 2, 3, 5, 6 -> DrawPosition.REVEALED;
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
        };
    }

    /**
     * Retrieves the draw ID based on the user's choice.
     *
     * @param choice the user's choice
     * @return the draw ID
     * @throws IndexOutOfBoundsException if the choice is invalid
     * @throws InvalidActionException if the draw ID is invalid
     */
    private int getDrawId(int choice) throws IndexOutOfBoundsException, InvalidActionException {
        try {
            return switch (choice) {
                case 1, 4 -> 0;
                case 2 -> this.game.getDrawableArea().getRevealedResourcesIds().getFirst();
                case 3 -> this.game.getDrawableArea().getRevealedResourcesIds().get(1);
                case 5 -> this.game.getDrawableArea().getRevealedGoldsIds().getFirst();
                case 6 -> this.game.getDrawableArea().getRevealedGoldsIds().get(1);
                default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
            };
        } catch (NullPointerException e) {
            throw new InvalidActionException("That revealed card slot is empty.");
        }
    }

    /**
     * Handles the rankings command.
     *
     * @param args the command arguments
     */
    private void handleRankings(String[] args) {
        if (args.length >= 2 && args[1].equals("--help")) {
            this.showHelpMessage("Go to the end game screen.", "r");
            return;
        }
        this.sceneManager.switchScene(SceneType.END_GAME_SCENE);
    }

    /**
     * Handles the leave command.
     *
     * @param args the command arguments
     */
    private void handleLeave(String[] args) {
        if (args.length >= 2 && args[1].equals("--help")) {
            this.showHelpMessage("Leave the game.", "leave");
            return;
        }
        this.gameController.leave();
    }

    /**
     * Focuses on the game overview scene, initializing necessary components.
     */
    @Override
    public void focus() {
        this.game = this.sceneManager.getVirtualGame();
        this.game.addObserver(this);
        this.drawAreaRenderer = new TuiDrawAreaRenderer(this.game.getDrawableArea());
        this.focusedPlayerRenderer = new TuiPlayerRenderer(this.game.getPlayerByUsername(ClientApp.getUsername()), false, false, this.game.getCommonObjectives());
        this.printView();
    }

    /**
     * Unfocuses from the game overview scene, removing it as an observer from the game.
     */
    @Override
    public void unfocus() {
        this.game.deleteObserver(this);
    }

    /**
     * Updates the game overview scene based on game state changes.
     */
    @Override
    public void update() {
        if (this.game.getGameState() == GameStateType.END_GAME)
            this.sceneManager.switchScene(SceneType.END_GAME_SCENE);
        else
            this.refreshView();
    }

    /**
     * Retrieves the {@link VirtualPlayer} associated with this client.
     *
     * @return the VirtualPlayer associated with this client
     */
    private VirtualPlayer getClientPlayer() {
        for (VirtualPlayer p : this.game.getPlayers())
            if (p.getUsername().equals(ClientApp.getUsername()))
                return p;
        return null;
    }

    /**
     * Checks if the current player can draw a card.
     *
     * @return true if the player can draw a card, otherwise false
     */
    private boolean canDraw() {
        return this.game.getCurrentPlayer().getUsername().equals(ClientApp.getUsername())
                && this.game.getGameState() == GameStateType.DRAW_CARD;
    }
}
