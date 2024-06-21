package it.polimi.ingsw.am49.view.tui.scenes;

import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;

import java.util.List;

public class EndGameScene extends Scene {

    private final GameController gameController;

    public EndGameScene(SceneManager sceneManager, GameController gameController) {
        super(sceneManager);
        this.gameController = gameController;
    }

    @Override
    public void printView() {
        this.clearScreen();
        this.printHeader();
        System.out.println("\n\n");
        this.printLeaderBoard();
        System.out.println("\n");
        this.printPrompt();
    }

    private void printHeader() {
        System.out.println("*******************************");
        System.out.println("| Welcome to Codex Naturalis! |");
        System.out.println("*******************************");
        System.out.println("       |  Game Over  |         ");
        System.out.println("       ***************         ");
    }

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

    private void printPrompt() {
        this.printInfoOrError();
        System.out.println("Available commands: (1) back to game overview | leave ");
        System.out.print(">>> ");
    }

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

    private void handleBackToGameOverview() {
        this.sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
    }

    private void handleLeave() {
        this.gameController.leave();
    }
}
