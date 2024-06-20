package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.util.Pair;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.renderers.TuiBoardRenderer;
import it.polimi.ingsw.am49.view.tui.renderers.TuiPlayerRenderer;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

public class PlayerScene extends Scene implements Observer {

    private final VirtualGame game;
    private final TuiBoardRenderer boardRenderer;
    private final TuiPlayerRenderer tuiPlayerRenderer;
    private final VirtualPlayer player;
    private final VirtualBoard board;
    private int row = 25;
    private int col = 25;
    private final GameController gameController;

    public PlayerScene(SceneManager sceneManager, VirtualGame game, VirtualPlayer player, GameController gameController) {
        super(sceneManager);
        this.game = game;
        this.player = player;
        this.board = player.getBoard();
        this.boardRenderer = new TuiBoardRenderer(player.getBoard());
        this.gameController = gameController;
        boolean hiddenHand = !player.getUsername().equals(ClientApp.getUsername());
        boolean hiddenPersonalObjective = this.game.getGameState() != GameStateType.END_GAME && hiddenHand;
        this.tuiPlayerRenderer = new TuiPlayerRenderer(player, hiddenHand, hiddenPersonalObjective, this.game.getCommonObjectives());
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        this.boardRenderer.drawNeighbourhood(row, col);
        this.boardRenderer.printBoard();
        System.out.println();
        tuiPlayerRenderer.print();
        System.out.println();
        this.printPrompt();
    }

    private void printHeader() {
        System.out.println("Player view\n");
    }

    private void printPrompt() {
        this.printInfoOrError();
        System.out.print("Available commands: ");
        System.out.print("(Q) Move Top Left | ");
        System.out.print("(E) Move Top Right | ");
        System.out.print("(A) Move Bottom Left | ");
        System.out.print("(D) Move Bottom Right | ");
        if (this.canPlace()) {
            System.out.print("(P) Place a card | ");
        }
        System.out.print("(B) Back");
        System.out.print("\n>>> ");
    }

    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];
        if (!command.isEmpty() && List.of('q', 'e', 'a', 'd').contains(command.charAt(0))) {
            this.handleMove(command);
        } else switch (command) {
            case "s", "starter" -> this.moveBoard(25, 25);
            case "p", "place" -> {
                if (this.canPlace())
                    this.handlePlaceCard(parts);
                else
                    this.showError("You cannot place a card now.");
            }
            case "b" -> {
                this.sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
            }
            default -> {
                this.showError("Invalid command, please try again.");
            }
        }
    }

    private void handleMove(String directionsString) {
        LinkedList<RelativePosition> directions = new LinkedList<>();
        try {
            for (int i = 0; i < directionsString.length(); i++) {
                switch (directionsString.charAt(i)) {
                    case 'q' -> directions.add(RelativePosition.TOP_LEFT);
                    case 'e' -> directions.add(RelativePosition.TOP_RIGHT);
                    case 'a' -> directions.add(RelativePosition.BOTTOM_LEFT);
                    case 'd' -> directions.add(RelativePosition.BOTTOM_RIGHT);
                    default -> throw new IllegalArgumentException("Wrong direction: " + directionsString.charAt(i));
                }
            }
            directions.forEach(this::moveBoard);
            this.refreshView();
        } catch (IllegalArgumentException e) {
            this.showError(e.getMessage());
            return;
        }

    }

    private void moveBoard(RelativePosition relativePosition) {
        Pair<Integer, Integer> newCoords = VirtualBoard.getCoords(relativePosition, this.row, this.col);
        if (this.board.getTile(newCoords.first, newCoords.second) != null) {
            this.row = newCoords.first;
            this.col = newCoords.second;
        }
    }

    private void handlePlaceCard(String[] args) {
        String username = ClientApp.getUsername();
        VirtualPlayer player = this.game.getPlayerByUsername(username);
        if (player == null) {
            this.showError("Not your turn.");
            return;
        }

        if (args.length < 3) {
            this.showError("Invalid command. You must specify which card to place, the corner and if it should be flipped.");
            return;
        }

        try {
            int cardIndex = Integer.parseInt(args[1]) - 1;
            int cardId = player.getHand().get(cardIndex);
            CornerPosition cornerPosition = switch (args[2]) {
                case "tl" -> CornerPosition.TOP_LEFT;
                case "tr" -> CornerPosition.TOP_RIGHT;
                case "bl" -> CornerPosition.BOTTOM_LEFT;
                case "br" -> CornerPosition.BOTTOM_RIGHT;
                default -> throw new InvalidActionException("Invalid corner.");
            };
            boolean flipped = false;
            if (args.length == 4) {
                if (args[3].startsWith("f")) flipped = true;
                else {
                    this.showError("Invalid flipped argument.");
                    return;
                }
            }
            this.gameController.placeCard(cardId, this.row, this.col, cornerPosition, flipped);
            this.refreshView();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            this.showError("Invalid card chosen.");
        } catch (InvalidActionException e) {
            this.showError(e.getMessage());
        } catch (NotYourTurnException e) {
            this.showError("You must wait for your turn.");
        } catch (NotInGameException e) {
            this.showError("Failed to place the card. (NotInGameException)");
        } catch (RemoteException e) {
            this.showError("Failed to place the card. Please try again.");
            e.printStackTrace();
        }
    }

    private boolean canPlace() {
        return  this.game.getCurrentPlayer().getUsername().equals(ClientApp.getUsername())
                && this.game.getGameState() == GameStateType.PLACE_CARD;
    }

    private void moveBoard(int row, int col) {
        if (this.board.getTile(row, col) != null) {
            this.row = row;
            this.col = col;
        }
    }

    @Override
    public void focus() {
        this.game.addObserver(this);
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
}
