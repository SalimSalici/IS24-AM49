package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.util.Observer;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class handles the display of the draw area and the draw card action
 */
public class DrawAreaScene extends Scene implements Observer {
    private boolean running = true;
    private final Server server;
    private final VirtualGame game;

    public DrawAreaScene(SceneManager sceneManager, TuiApp tuiApp) {
        super(sceneManager, tuiApp);
        this.server = this.tuiApp.getServer();
        this.game = tuiApp.getVirtualGame();
        this.game.addObserver(this);
    }

    @Override
    public void play() {
        this.printDrawArea();
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
                case "1" -> {
                    if (this.isClientTurn())
                        this.drawCard(parts);
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
        System.out.println("Draw Area for player: " + tuiApp.getUsername());
    }

    /**
     * Calls all methods necessary to construct the output of the draw area scene
     */
    private void printDrawArea() {
        this.clearScreen();
        this.printHeader();
        this.printDrawOptions();
    }

    /**
     * Shows the available options
     */
    private void promptCommand() {
        System.out.println("Available commands: ");
        if (this.isClientTurn()) {
            System.out.println("(1) Draw a card");
        }
        System.out.println("Type 'exit' to go back to the Game Overview.");
        System.out.print(">>> ");
    }

    private void printDrawOptions() {
        System.out.println("Deck top resource: " + this.game.getDrawableArea().getDeckTopResource());
        System.out.println("Deck top gold: " + this.game.getDrawableArea().getDeckTopGold());
        System.out.println("Revealed resources: " + this.game.getDrawableArea().getRevealedResourcesIds());
        System.out.println("Revealed golds: " + this.game.getDrawableArea().getRevealedGoldsIds());
    }

    private void drawCard(String[] args) {
        String username = this.tuiApp.getUsername();
        VirtualPlayer player = this.game.getPlayerByUsername(username);
        if (player == null) {
            System.out.println("Not your turn.");
            return;
        }

        DrawPosition drawPosition = null;
        int idOfRevealedDrawn = -1;
        linesToClear = 1;

        while (true) {
            System.out.print("Enter the draw position (GOLD_DECK, RESOURCE_DECK, REVEALED): ");
            try {
                drawPosition = DrawPosition.valueOf(scanner.nextLine().trim().toUpperCase());
                if (drawPosition == DrawPosition.REVEALED) {
                    System.out.print("Enter the ID of the revealed card to draw: ");
                    idOfRevealedDrawn = Integer.parseInt(scanner.nextLine().trim());
                }
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("You chose the draw position: " + drawPosition);
                linesToClear = 1;
                break;
            } catch (IllegalArgumentException e) {
                IntStream.range(0, linesToClear).forEach(i -> clearLastLine());
                System.out.println("Invalid input. Please enter a valid draw position and ID.");
                linesToClear = 2;
            }
        }

        try {
            this.server.executeAction(this.tuiApp, new DrawCardAction(username, drawPosition, idOfRevealedDrawn));
            System.out.println("Card drawn successfully.");
        } catch (NotYourTurnException e) {
            System.out.println("You must wait for your turn.");
        } catch (NotInGameException e) {
            System.out.println("Failed to draw the card. (NotInGameException)");
        } catch (RemoteException e) {
            System.out.println("Failed to draw the card. Please try again.");
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        this.printDrawArea();
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


//package it.polimi.ingsw.am49.scenes;
//
//import it.polimi.ingsw.am49.client.TuiApp;
//import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
//import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
//import it.polimi.ingsw.am49.model.actions.DrawCardAction;
//import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
//import it.polimi.ingsw.am49.util.Observer;
//
//public class DrawAreaScene extends Scene implements Observer {
//    private final TuiApp tuiApp;
//    private final VirtualGame game;
//    private boolean running = true;
//
//    public DrawAreaScene(SceneManager sceneManager, TuiApp tuiApp) {
//        super(sceneManager);
//        this.tuiApp = tuiApp;
//        this.game = tuiApp.getVirtualGame();
//        this.game.addObserver(this);
//    }
//
//    @Override
//    public void play() {
//        this.linesToClear = 3;
//        while (this.running) {
//            this.printHeader();
//            this.promptCommand();
//            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
//            if (parts.length == 0) {
//                System.out.println("Invalid command, please try again.");
//                continue;
//            }
//
//            this.clearLines(this.linesToClear);
//
//            String command = parts[0];
//            switch (command) {
//                case "1" -> {
//                    if (this.isClientTurn())
//                        this.handleDrawCard(parts);
//                    else
//                        System.out.println("Invalid command, please try again.");
//                }
//                case "2" -> this.stop();
//                default -> System.out.println("Invalid command, please try again.");
//            }
//        }
//    }
//
//    private void printHeader() {
//        this.clearScreen();
//        System.out.println("*******************************");
//        System.out.println("| Welcome to Codex Naturalis! |");
//        System.out.println("*******************************");
//        System.out.println("        | Draw area |         ");
//        System.out.println("        *************         ");
//        System.out.println("\n\n\n");
//        System.out.println("Current state: " + this.game.getGameState());
//        System.out.println("Current Player: " + this.game.getCurrentPlayer().getUsername());
//        System.out.println("Client's Username: " + this.tuiApp.getUsername());
//        System.out.println("\n\n\n");
//        System.out.println("ResourceDeck: " + this.game.getDeckTopResource());
//        System.out.println("GoldDeck: " + this.game.getDeckTopResource());
//        System.out.println("Revealed drawable resources: " + this.game.getRevealedResourcesIds());
//        System.out.println("Revealed drawable golds: " + this.game.getRevealedGoldsIds());
//        System.out.println("\n\n\n");
//    }
//
//    private void promptCommand() {
//        System.out.print("Available commands: ");
//        if (this.isClientTurn())
//            System.out.println("(1) Draw card | (2) Back");
//        else
//            System.out.println("(2) Back");
//        System.out.print(">>> ");
//    }
//
//    private void handleDrawCard(String[] args) {
//        if (args.length < 2) {
//            showError("Username missing, please try again. Type '1 --help' for more information about this command.");
//            return;
//        }
//        if (args[1].equals("--help")) {
//            showHelp("TODO draw card help...", "TODO");
//            return;
//        }
//
//        int position;
//        try {
//            position = Integer.parseInt(args[1]);
//        } catch (NumberFormatException e) {
//            showError("Invalid argument.");
//            return;
//        }
//
//        DrawPosition drawPosition;
//        switch (position) {
//            case 1 -> drawPosition = DrawPosition.RESOURCE_DECK;
//            case 2 -> drawPosition = DrawPosition.GOLD_DECK;
//            case 3 -> drawPosition = DrawPosition.REVEALED;
//            default -> {
//                showError("Invalid argument.");
//                return;
//            }
//        }
//
//        this.tuiApp.setUsername(username);
//        this.printHeader();
//        this.printRooms();
//    }
//
//    @Override
//    public void update() {
//        this.printHeader();
//        this.promptCommand();
//        this.scanner.nextLine();
//    }
//
//    private void stop() {
//        this.running = false;
//        this.game.deleteObserver(this);
//    }
//
//    private boolean isClientTurn() {
//        return this.game.getCurrentPlayer().getUsername().equals(this.tuiApp.getUsername());
//    }
//
//}