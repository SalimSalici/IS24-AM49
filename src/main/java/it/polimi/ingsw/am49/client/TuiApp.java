package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.scenes.SceneManager;
import it.polimi.ingsw.am49.scenes.WelcomeScene;

import java.rmi.RemoteException;

public class TuiApp extends ClientApp {

    private final SceneManager sceneManager;
    public TuiApp() throws RemoteException {
        this.sceneManager = new SceneManager();
    }

    public void initialize() {
        this.sceneManager.start(new WelcomeScene(this.sceneManager, this));
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        try {
            this.sceneManager.getScene().roomUpdate(roomInfo, message);
        } catch (InvalidSceneException e) {
            System.out.println("Invalid Scene");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        super.receiveGameUpdate(gameUpdate);
        try {
            this.sceneManager.getScene().gameUpdate(gameUpdate);
        } catch (InvalidSceneException e) {
            System.out.println("Game update received during wrong scene.");
            throw new RuntimeException(e);
        }
    }
}
