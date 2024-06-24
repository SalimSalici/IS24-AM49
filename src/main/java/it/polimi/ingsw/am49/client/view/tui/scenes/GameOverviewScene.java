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

import java.rmi.RemoteException;
import java.util.List;

public class GameOverviewScene extends Scene implements Observer {

    private VirtualGame game;
    private TuiDrawAreaRenderer drawAreaRenderer;
    private TuiPlayerRenderer focusedPlayerRenderer;
    private final GameController gameController;

    public GameOverviewScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
    }

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

    private void printDrawArea() {
        System.out.println("------ Draw Area " + "-".repeat(95));
        System.out.println();
        this.drawAreaRenderer.print();
    }

    private void printFocusedPlayer() {
        System.out.println("------ Focused Player " + "-".repeat(95));
        System.out.println();
        this.focusedPlayerRenderer.print();
    }

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

    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];
        switch (command) {
            case "1", "chat":
                this.sceneManager.switchScene(SceneType.CHAT_SCENE);
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
                this.handleRankings();
                break;
            case "leave":
                this.handleLeave();
                break;
            default:
                this.showError("Invalid command, please try again.");
        }
    }

    private void handleFocusPlayer(String[] args) {
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

    private void handleViewPlayer(String[] args) {
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
            return;
        } catch (IndexOutOfBoundsException e) {
            this.showError("Argument must be between 1 and " + this.game.getPlayers().size());
            return;
        }
    }

    private void handleDrawCard(String[] args) {
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
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private DrawPosition getDrawPosition(int choice) throws IndexOutOfBoundsException {
        return switch (choice) {
            case 1 -> DrawPosition.RESOURCE_DECK;
            case 4 -> DrawPosition.GOLD_DECK;
            case 2, 3, 5, 6 -> DrawPosition.REVEALED;
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
        };
    }

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

//    private DrawCardAction getDrawCardAction(int choice) throws InvalidActionException {
//        DrawPosition drawPosition = switch (choice) {
//            case 1 -> DrawPosition.RESOURCE_DECK;
//            case 4 -> DrawPosition.GOLD_DECK;
//            case 2, 3, 5, 6 -> DrawPosition.REVEALED;
//            default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
//        };
//
//        int drawId = 0;
//        try {
//            drawId = switch (choice) {
//                case 1, 4 -> 0;
//                case 2 -> this.game.getDrawableArea().getRevealedResourcesIds().getFirst();
//                case 3 -> this.game.getDrawableArea().getRevealedResourcesIds().get(1);
//                case 5 -> this.game.getDrawableArea().getRevealedGoldsIds().getFirst();
//                case 6 -> this.game.getDrawableArea().getRevealedGoldsIds().get(1);
//                default -> throw new IndexOutOfBoundsException("Unexpected value: " + choice);
//            };
//        } catch (NullPointerException e) {
//            throw new InvalidActionException("That revealed card slot is empty.");
//        }
//        return new DrawCardAction(ClientApp.getUsername(), drawPosition, drawId);
//    }

    private void handleRankings() {
        this.sceneManager.switchScene(SceneType.END_GAME_SCENE);
    }

    private void handleLeave() {
//        this.sceneManager.destroyPlayerScenes();
        this.gameController.leave();
//        this.backToMainMenu(true);
    }

    @Override
    public void focus() {
        this.game = this.sceneManager.getVirtualGame();
        this.game.addObserver(this);
        this.drawAreaRenderer = new TuiDrawAreaRenderer(this.game.getDrawableArea());
        this.focusedPlayerRenderer = new TuiPlayerRenderer(this.game.getPlayerByUsername(ClientApp.getUsername()), false, false, this.game.getCommonObjectives());
        this.printView();
    }

    @Override
    public void unfocus() {
        this.game.deleteObserver(this);
    }

    @Override
    public void update() {
        if (this.game.getGameState() == GameStateType.END_GAME)
            this.sceneManager.switchScene(SceneType.END_GAME_SCENE);
        else
            this.refreshView();
    }

    /**
     * @return the {@link VirtualPlayer} associated with this client
     */
    private VirtualPlayer getClientPlayer() {
        for (VirtualPlayer p : this.game.getPlayers())
            if (p.getUsername().equals(ClientApp.getUsername()))
                return p;
        return null;
    }

    private boolean canDraw() {
        return  this.game.getCurrentPlayer().getUsername().equals(ClientApp.getUsername())
                && this.game.getGameState() == GameStateType.DRAW_CARD;
    }
}
