package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.util.Observer;

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
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
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
                case "3":
                    this.showDrawArea();
                    break;
                case "exit":
                    this.stop();
                    break;
                default:
                    System.out.println("Invalid command, please try again.");
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
        System.out.println("Current state: " + this.game.getGameState());
        System.out.println("Current Player: " + this.game.getCurrentPlayer().getUsername());
        System.out.println("Client's Username: " + this.tuiApp.getUsername());
        System.out.println("\n\n\n");
    }

    private void promptCommand() {
        System.out.println("Available commands: ");
        System.out.println("(1) Show board");
        System.out.println("(2) Show players' status");
        System.out.println("(3) Show draw area");
        System.out.println("Type 'exit' to go back to the main menu.");
        System.out.print(">>> ");
    }

    private void showBoard() {
        this.sceneManager.setScene( new ViewBoardScene( this.sceneManager, this.tuiApp, this.game.getPlayerByUsername(this.tuiApp.getUsername()).getBoard()));
        this.stop();
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

    private void showDrawArea() {
        this.sceneManager.setScene(new DrawAreaScene( this.sceneManager, this.tuiApp));
        this.stop();
    }

    @Override
    public void update() {
        this.printHeader();
        this.promptCommand();
    }

    private void stop() {
        this.running = false;
        this.game.deleteObserver(this);
    }
}