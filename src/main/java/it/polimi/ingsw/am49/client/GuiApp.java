package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import java.rmi.RemoteException;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.GuiEntry;
import it.polimi.ingsw.am49.view.gui.GuiManager;

public class GuiApp extends ClientApp {

    private GuiManager manager;
    private final String[] args;
    public GuiApp(String[] args) throws RemoteException {
        this.args = args;
    }

    @Override
    public void initialize() {
        // Avvia JavaFX
        manager = new GuiManager(this);
        GuiEntry.guiManagerInstance = manager;
        GuiEntry.main(args);
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        try {
            this.manager.getCurrentController().roomUpdate(roomInfo, message);
        } catch (InvalidSceneException e) {
            System.out.println("Invalid Scene");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        super.receiveGameUpdate(gameUpdate);
        try {
            this.manager.getCurrentController().gameUpdate(gameUpdate);
        } catch (InvalidSceneException e) {
            System.out.println("Game update received during wrong scene.");
            throw new RuntimeException(e);
        }
    }
}

