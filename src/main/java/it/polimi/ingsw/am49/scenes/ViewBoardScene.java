package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.client.TuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualBoard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.view.tui.TuiBoard;
import it.polimi.ingsw.am49.client.ClientManager;

/**
 * This class handles the display of the player board scene and the card placement action
 */
public class ViewBoardScene extends Scene {

    private final TuiApp tuiApp;
    private boolean running = true;
    private final Server server;
    private final VirtualBoard virtualBoard;
    private final TuiBoard tuiBoard;
    private boolean canPlaceCard = false;
    private int toPlaceIndex;
    private int row;
    private int col;

    public ViewBoardScene(SceneManager sceneManager, TuiApp tuiApp, Boolean canPlaceCard, VirtualBoard virtualBoard) {
        super(sceneManager);
        this.tuiApp = tuiApp;
        this.server = this.tuiApp.getServer();
        this.virtualBoard = virtualBoard;
        this.tuiBoard = new TuiBoard(virtualBoard);
        this.canPlaceCard = canPlaceCard; //must be calculated before switching to the scene.
        this.row = 25;
        this.col = 25;
    }

    @Override
    public void play() {
        this.printBoard();
        while (this.running) {
            this.promptCommand();
            //linesToClear = 2;
            String[] parts = scanner.nextLine().trim().toLowerCase().split(" ");
            if (parts.length == 0) {
                System.out.println("Invalid command, please try again.");
                linesToClear = 3;
                continue;
            }

            String command = parts[0];
            switch (command) {
                case "1":   //TODO: move board currently shows no effect on the displayed board
                    this.moveBoardUp();
                    break;
                case "2":
                    this.moveBoardDown();
                    break;
                case "3":
                    this.moveBoardRight();
                    break;
                case "4":
                    this.moveBoardLeft();
                    break;
                case "5":   //TODO: implement place card, currently not working
                    if(this.canPlaceCard){
                        placeCard(Integer.parseInt(parts[1]));
                    }
                case "exit":
                    this.sceneManager.setScene( new GameOverviewScene( this.sceneManager, this.tuiApp));
                    this.running = false;
                    break;
                default:
                    System.out.println("Invalid command, please try again.");
                    linesToClear = 3;
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
        System.out.println("(1) Move Up");
        System.out.println("(2) Move Down");
        System.out.println("(3) Move Right");
        System.out.println("(4) Move Left");
        if (this.canPlaceCard) {
            System.out.println("(5) Place a card");
        }
        System.out.println("Type 'exit' to go back to the Game Overview.");
        System.out.print(">>> ");
    }

    private void placeCard(int id) {}

    private void moveBoardUp() {
        row--;
        printBoard();
    }

    private void moveBoardDown() {
        row++;
        printBoard();
    }

    private void moveBoardRight() {
        col++;
        printBoard();
    }

    private void moveBoardLeft() {
        col--;
        printBoard();
    }

}
