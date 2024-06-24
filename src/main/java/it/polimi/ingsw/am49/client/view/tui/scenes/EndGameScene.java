package it.polimi.ingsw.am49.client.view.tui.scenes;

import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.textures.AnsiColor;

import java.util.List;

/**
 * Represents the end game scene in the text-based user interface.
 */
public class EndGameScene extends Scene {

    private final GameController gameController;

    /**
     * Constructs an EndGameScene with the specified scene manager and game controller.
     * @param sceneManager the scene manager
     * @param gameController the game controller
     */
    public EndGameScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
    }

    /**
     * Prints the end game view including the header, leaderboard, and prompt.
     */
    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n");
        this.printLeaderBoard();
        System.out.println("\n");
        this.printPrompt();
    }

    /**
     * Prints the game over header.
     */
    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("       |  Game Over  |         ");
        System.out.println("       ***************         ");
    }

    /**
     * Prints the leaderboard showing the ranking of players.
     */
    private void printLeaderBoard() {
        VirtualPlayer forfeitWinner = this.sceneManager.getVirtualGame().getforfeitWinner();
        if (forfeitWinner != null) {
            System.out.println(this.getColoredUsername(forfeitWinner) + " wins by forfeit!\n");
        }

        List<VirtualPlayer> ranking = this.sceneManager.getVirtualGame().getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            VirtualPlayer player = ranking.get(i);
            System.out.println(
                    (i+1) + " " + AnsiColor.fromColor(player.getColor()) + player.getUsername() + AnsiColor.ANSI_RESET + ": " +
                            player.getPoints() + " points and " +
                            player.getCompletedObjectives() + " completed objectives."
            );
        }
    }

    /**
     * Prints the prompt for user commands.
     */
    private void printPrompt() {
        this.printInfoOrError();
        System.out.println("Available commands: (1) back to game overview | leave ");
        System.out.print(">>> ");
    }

    /**
     * Handles user input commands.
     * @param input the user input
     */
    @Override
    public void handleInput(String input) {
        String[] parts = input.split(" ");
        String command = parts[0];
        switch (command) {
            case "1":
                this.handleBackToGameOverview();
                break;
            case "leave":
                this.handleLeave();
                break;
            default:
                this.showError("Invalid command, please try again.");
        }
    }

    /**
     * Handles the command to return to the game overview scene.
     */
    private void handleBackToGameOverview() {
        this.sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
    }

    /**
     * Handles the command to leave the game.
     */
    private void handleLeave() {
        this.gameController.leave();
    }

    @Override
    public void focus() {
        this.printView();
    }
}
