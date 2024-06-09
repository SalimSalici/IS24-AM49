package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.actions.PlaceCardAction;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.view.tui.renderers.TuiBoardRenderer;
import it.polimi.ingsw.am49.util.Pair;
import it.polimi.ingsw.am49.view.tui.renderers.TuiPlayerRenderer;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class handles the display of the player board scene and the card placement action
 */
public class ViewPlayerScene extends Scene implements Observer {

    private boolean running = true;
    private final Server server;
    private final VirtualGame game;
    private final VirtualBoard board;
    private final TuiBoardRenderer tuiBoardRenderer;
    private final TuiPlayerRenderer tuiPlayer;
    private final Object renderLock;
    private int row;
    private int col;
    private String errorMessage;

    public ViewPlayerScene(SceneManager sceneManager, TuiApp tuiApp, VirtualPlayer player) {
        super(sceneManager, tuiApp);
        this.server = this.tuiApp.getServer();
        this.game = tuiApp.getVirtualGame();
        this.board = player.getBoard();
//        this.board.addObserver(this);
        this.game.addObserver(this);
        this.tuiPlayer = new TuiPlayerRenderer(player, !player.getUsername().equals(tuiApp.getUsername()), tuiApp.getVirtualGame().getCommonObjectives());
        this.tuiBoardRenderer = new TuiBoardRenderer(this.board);
        this.row = 25;
        this.col = 25;
        this.errorMessage = "";
        this.renderLock = new Object();
    }

    @Override
    public void play() {
        while (this.running) {
            synchronized (renderLock) {
                this.printView();
                this.promptCommand();
            }
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
                continue;
            }

            String command = parts[0];
            if (!command.isEmpty() && List.of('q', 'e', 'a', 'd').contains(command.charAt(0))) {
                this.handleMove(command);
            } else switch (command) {
                case "s", "starter" -> moveBoard(25, 25);
                case "p", "place" -> {
                    if (this.canPlace())
                        this.handlePlaceCard(parts);
                    else
                        System.out.println("Invalid command, please try again.");
                }
                case "b" -> {
                    this.sceneManager.setScene(new GameOverviewScene(this.sceneManager, this.tuiApp));
                    this.stop();
                }
                default -> {
                    System.out.println("Invalid command, please try again.");
                    linesToClear = 3;
                }
            }
        }
    }

    private void printHeader() {
        System.out.println("Player view\n");
    }

    /**
     * Calls al method necessary to construct the output of the player board scene
     */
    private void printView() {
        this.clearScreen();
        this.printHeader();
        tuiBoardRenderer.drawNeighbourhood(row, col);
        tuiBoardRenderer.printBoard();
        System.out.println();
        tuiPlayer.print();
        System.out.println("\n");
    }

    /**
     * Shows the available options
     */
    private void promptCommand() {
        System.out.println(AnsiColor.ANSI_RED + this.errorMessage + AnsiColor.ANSI_RESET);
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
        this.errorMessage = "";
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
        } catch (IllegalArgumentException e) {
            this.errorMessage = e.getMessage();
            return;
        }

    }

    private void handlePlaceCard(String[] args) {
        String username = this.tuiApp.getUsername();
        VirtualPlayer player = this.game.getPlayerByUsername(username);
        if (player == null) {
            System.out.println("Not your turn.");
            return;
        }

        if (args.length < 3) {
            this.errorMessage = "Invalid command. You must specify which card to place, the corner and if it should be flipped.";
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
                    this.errorMessage = "Invalid flipped argument.";
                    return;
                }
            }
            this.server.executeAction(this.tuiApp, new PlaceCardAction(username, cardId, this.row, this.col, cornerPosition, flipped));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            this.errorMessage = "Invalid card chosen.";
        } catch (InvalidActionException e) {
            this.errorMessage = e.getMessage();
        } catch (NotYourTurnException e) {
            this.errorMessage = "You must wait for your turn.";
        } catch (NotInGameException e) {
            this.errorMessage = "Failed to place the card. (NotInGameException)";
        } catch (RemoteException e) {
            this.errorMessage = "Failed to place the card. Please try again.";
            e.printStackTrace();
        }
    }

    private void moveBoard(RelativePosition relativePosition) {
        Pair<Integer, Integer> newCoords = VirtualBoard.getCoords(relativePosition, this.row, this.col);
        if (this.board.getTile(newCoords.first, newCoords.second) != null) {
            this.row = newCoords.first;
            this.col = newCoords.second;
        }
    }

    private void moveBoard(int row, int col) {
        if (this.board.getTile(row, col) != null) {
            this.row = row;
            this.col = col;
        }
    }

    private boolean canPlace() {
        return  this.game.getCurrentPlayer().getUsername().equals(this.tuiApp.getUsername())
                && this.game.getGameState() == GameStateType.PLACE_CARD;
    }

    // APPROCCIO VECCHIO: update dell'interfaccia
    @Override
    public void update() {
        synchronized (renderLock) {
            this.printView();
            this.promptCommand();
        }
    }

    public void handlePlayerUpdate() {

    }

    private void stop() {
        this.running = false;
//        this.board.deleteObserver(this);
        this.game.deleteObserver(this);
    }
}
