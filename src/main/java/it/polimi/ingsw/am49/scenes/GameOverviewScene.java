package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualTile;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.actions.PlaceCard;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;

import java.rmi.RemoteException;
import java.util.stream.IntStream;

public class GameOverviewScene extends Scene implements Observer {
    private final TuiApp tuiApp;
    private final VirtualGame game;
    private boolean running = true;

    public GameOverviewScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.game = tuiApp.getVirtualGame();
        this.game.addObserver(this);
    }

    @Override
    public void play() {
        while (this.running) {
            this.printHeader();
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
                case "1":
                    this.showBoard();
                    break;
                case "2":
                    this.showPlayersStatus();
                    break;
//                case "3":
//                    if (this.isClientTurn()) {
//                        this.placeCard();
//                    } else {
//                        System.out.println("It's not your turn.");
//                        linesToClear = 3;
//                    }
//                    break;
                case "exit":
                    this.running = false;
                    break;
                default:
                    System.out.println("Invalid command, please try again.");
                    linesToClear = 3;
            }
        }
    }

    private void printHeader() {
        this.clearScreen();
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("     | Game Overview |        ");
        System.out.println("     *****************        ");
        System.out.println("\n\n\n");
        System.out.println("Current Player: " + this.game.getCurrentPlayer().getUsername());
        System.out.println("Client's Username: " + this.tuiApp.getUsername());
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void promptCommand() {
        System.out.println("Available commands: ");
        System.out.println("(1) Show board");
        System.out.println("(2) Show players' status");
        System.out.println("Type 'exit' to go back to the main menu.");
        System.out.print(">>> ");
    }

    private void showBoard() {
        this.sceneManager.setScene( new ViewBoardScene( this.sceneManager, this.tuiApp, this.game.getPlayerByUsername(this.tuiApp.getUsername()).getBoard()));
        this.running = false;
    }

    private void showPlayersStatus() {
        this.clearScreen();
        System.out.println("Players' Status:");
        for (VirtualPlayer player : this.game.getPlayers()) {
            System.out.println("Player: " + player.getUsername());
            System.out.println("Points: " + player.getPoints());
            System.out.println("Hand: " + player.getHand());
            System.out.println("Active Symbols: " + player.getActiveSymbols());
            System.out.println();
        }
        System.out.println("\n\n");
        System.out.println("Press enter to continue...");
        this.scanner.nextLine();
    }

//    private void placeCard() {
//        String username = this.tuiApp.getUsername();
//        VirtualPlayer player = this.game.getPlayerByUsername(username);
//        if (player == null) {
//            System.out.println("Player not found.");
//            return;
//        }
//
//        System.out.println("Your hand: " + player.getHand());
//        int cardId = -1;
//        linesToClear = 1;
//        while (true) {
//            System.out.print("Enter the card ID to place: ");
//            try {
//                cardId = Integer.parseInt(scanner.nextLine().trim());
//                if (player.getHand().contains(cardId)) {
//                    IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                    System.out.println("You chose the card number " + cardId);
//                    linesToClear = 1;
//                    break;
//                } else {
//                    IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                    System.out.println("Invalid card ID. The card is not in your hand.");
//                    linesToClear = 2;
//                }
//            } catch (NumberFormatException e) {
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("Invalid input. Please enter a valid card ID.");
//                linesToClear = 2;
//            }
//        }
//
//        int row = -1;
//        linesToClear = 1;
//        while (true) {
//            System.out.print("Enter the row: ");
//            try {
//                row = Integer.parseInt(scanner.nextLine().trim());
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("You chose the row number: " + row);
//                linesToClear = 1;
//                break;
//            } catch (NumberFormatException e) {
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("Invalid input. Please enter a valid row number.");
//                linesToClear = 2;
//            }
//        }
//
//        int col = -1;
//        linesToClear = 1;
//        while (true) {
//            System.out.print("Enter the column: ");
//            try {
//                col = Integer.parseInt(scanner.nextLine().trim());
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("You chose the column number: " + col);
//                linesToClear = 1;
//                break;
//            } catch (NumberFormatException e) {
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("Invalid input. Please enter a valid column number.");
//                linesToClear = 2;
//            }
//        }
//
//        CornerPosition cornerPosition = null;
//        linesToClear = 1;
//        while (true) {
//            System.out.print("Enter the corner position (TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT): ");
//            try {
//                cornerPosition = CornerPosition.valueOf(scanner.nextLine().trim().toUpperCase());
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("You chose the corner: " + cornerPosition.toString());
//                linesToClear = 1;
//                break;
//            } catch (IllegalArgumentException e) {
//                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
//                System.out.println("Invalid input. Please enter a valid corner position.");
//                linesToClear = 2;
//            }
//        }
//
//        System.out.print("Flip the card? (yes/no): ");
//        boolean flipped = scanner.nextLine().trim().equalsIgnoreCase("yes");
//
//        try {
//            this.tuiApp.getServer().executeAction(this.tuiApp, new PlaceCard(username, cardId, row, col, cornerPosition, flipped));
//            System.out.println("Card placed successfully.");
//        } catch (NotYourTurnException e) {
//            System.out.println("You must wait for your turn.");
//        } catch (NotInGameException e) {
//            System.out.println("Failed to place the card. Please try again. (NotInGameException)");
//        } catch (RemoteException e) {
//            System.out.println("Failed to place the card. Please try again.");
//            e.printStackTrace();
//        }
//    }

    @Override
    public void update() {
        this.printHeader();
        System.out.println("Game state updated. Type a command to see the details.");
        System.out.println("\n\n");
        System.out.println("Press enter to continue...");
        this.scanner.nextLine();
    }
}