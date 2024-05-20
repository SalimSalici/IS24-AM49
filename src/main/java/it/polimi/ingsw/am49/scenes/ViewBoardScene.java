package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.actions.PlaceCardAction;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;
import it.polimi.ingsw.am49.view.tui.TuiBoard;
import it.polimi.ingsw.am49.util.Pair;

import java.rmi.RemoteException;
import java.util.stream.IntStream;

/**
 * This class handles the display of the player board scene and the card placement action
 */
public class ViewBoardScene extends Scene implements Observer {

    private final TuiApp tuiApp;
    private boolean running = true;
    private final Server server;
    private final VirtualGame game;
    private final VirtualBoard virtualBoard;
    private final TuiBoard tuiBoard;
    private int row;
    private int col;

    public ViewBoardScene(SceneManager sceneManager, TuiApp tuiApp, VirtualBoard virtualBoard) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.server = this.tuiApp.getServer();
        this.game = tuiApp.getVirtualGame();
        this.virtualBoard = virtualBoard;
        this.virtualBoard.addObserver(this);
        this.tuiBoard = new TuiBoard(virtualBoard);
        this.row = 25;
        this.col = 25;
    }

    @Override
    public void play() {
        this.printBoard();
        while (this.running) {
            this.promptCommand();
            linesToClear = 2;
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
                linesToClear = 3;
                continue;
            }

            String command = parts[0];
            switch (command) {
                case "q" -> this.moveBoard(RelativePosition.TOP_LEFT);
                case "e" -> this.moveBoard(RelativePosition.TOP_RIGHT);
                case "a" -> this.moveBoard(RelativePosition.BOTTOM_LEFT);
                case "d" -> this.moveBoard(RelativePosition.BOTTOM_RIGHT);
                case "p" -> {
                    if (this.isClientTurn())
                        this.placeCard(parts);
                    else
                        System.out.println("Invalid command, please try again.");
                }
                case "exit" -> {
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
        System.out.println("Board of player: " + tuiApp.getUsername());
    }

    /**
     * Calls al method necessary to construct the output of the player board scene
     */
    private void printBoard() {
        this.clearScreen();
        this.printHeader();
        tuiBoard.drawNeighbourhood(row, col);
        tuiBoard.printBoard();
    }

    /**
     * Shows the available options
     */
    private void promptCommand() {
        System.out.println("Available commands: ");
        System.out.println("(Q) Move Top Left");
        System.out.println("(E) Move Top Right");
        System.out.println("(A) Move Bottom Left");
        System.out.println("(D) Move Bottom Right");
        if (this.isClientTurn()) {
            System.out.println("(P) Place a card");
        }
        System.out.println("Type 'exit' to go back to the Game Overview.");
        System.out.print(">>> ");
    }

    private void placeCard(String[] args) {
        String username = this.tuiApp.getUsername();
        VirtualPlayer player = this.game.getPlayerByUsername(username);
        if (player == null) {
            System.out.println("Not your turn.");
            return;
        }

        System.out.println("Your hand: " + player.getHand());
        int cardId = -1;
        linesToClear = 1;
        while (true) {
            System.out.print("Enter the card ID to place: ");
            try {
                cardId = Integer.parseInt(scanner.nextLine().trim());
                if (player.getHand().contains(cardId)) {
                    IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                    System.out.println("You chose the card number " + cardId);
                    linesToClear = 1;
                    break;
                } else {
                    IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                    System.out.println("Invalid card ID. The card is not in your hand.");
                    linesToClear = 2;
                }
            } catch (NumberFormatException e) {
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("Invalid input. Please enter a valid card ID.");
                linesToClear = 2;
            }
        }

        CornerPosition cornerPosition = null;
        linesToClear = 1;
        while (true) {
            System.out.print("Enter the corner position (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT): ");
            try {
                cornerPosition = CornerPosition.valueOf(scanner.nextLine().trim().toUpperCase());
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("You chose the corner: " + cornerPosition.toString());
                linesToClear = 1;
                break;
            } catch (IllegalArgumentException e) {
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("Invalid input. Please enter a valid corner position.");
                linesToClear = 2;
            }
        }

        System.out.print("Flip the card? (yes/no): ");
        boolean flipped = scanner.nextLine().trim().startsWith("y");

        try {
            this.server.executeAction(this.tuiApp, new PlaceCardAction(username, cardId, this.row, this.col, cornerPosition, flipped));
            System.out.println("Card placed successfully.");
        } catch (NotYourTurnException e) {
            System.out.println("You must wait for your turn.");
        } catch (NotInGameException e) {
            System.out.println("Failed to place the card. (NotInGameException)");
        } catch (RemoteException e) {
            System.out.println("Failed to place the card. Please try again.");
            e.printStackTrace();
        }
    }

    private void moveBoard(RelativePosition relativePosition) {
        Pair<Integer, Integer> newCoords = VirtualBoard.getCoords(relativePosition, this.row, this.col);
        if (this.virtualBoard.getTile(newCoords.first, newCoords.second) != null) {
            this.row = newCoords.first;
            this.col = newCoords.second;
        }
        printBoard();
    }

    @Override
    public void update() {
        this.printBoard();
        this.promptCommand();
        this.linesToClear = 3;
    }

    private boolean isClientTurn() {
        return this.game.getCurrentPlayer().getUsername().equals(this.tuiApp.getUsername());
    }

    private void stop() {
        this.running = false;
        this.game.deleteObserver(this);
    }
}
