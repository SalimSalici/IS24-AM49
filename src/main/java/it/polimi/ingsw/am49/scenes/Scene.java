package it.polimi.ingsw.am49.scenes;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;

import java.util.Scanner;

public abstract class Scene {
    protected SceneManager sceneManager;
    protected final Scanner scanner;

    public Scene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.scanner = new Scanner(System.in);
    }

    public abstract void play();

    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException {}

    public void gameUpdate(GameUpdate gameUpdate) throws InvalidSceneException {}

    protected void clearScreen() {
        System.out.println("-".repeat(150));
        System.out.println("\033[H\033[2J");
//        for (int i = 0; i < 60; i++)
//            System.out.println();
    }
}
