package it.polimi.ingsw.am49.client;

import java.rmi.RemoteException;

public class TuiAppBU extends ClientApp {
    public TuiAppBU() throws RemoteException {
    }

    @Override
    protected void initialize() {

    }

//    private final SceneManager sceneManager;
//    public TuiApp() throws RemoteException {
//        this.sceneManager = new SceneManager();
//    }
//
//    public void initialize() {
//        this.sceneManager.start(new WelcomeScene(this.sceneManager, this));
//    }
//
//    @Override
//    public void roomUpdate(RoomInfo roomInfo, String message) {
//        try {
//            this.sceneManager.getScene().roomUpdate(roomInfo, message);
//        } catch (InvalidSceneException e) {
//            System.out.println("Invalid Scene");
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void receiveGameUpdate(GameUpdate gameUpdate) {
//        super.receiveGameUpdate(gameUpdate);
//        try {
//            this.sceneManager.getScene().gameUpdate(gameUpdate);
//        } catch (InvalidSceneException e) {
//            System.out.println("Game update received during wrong scene.");
//            throw new RuntimeException(e);
//        }
//    }
}
