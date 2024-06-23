package it.polimi.ingsw.am49.view;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.view.gui.GuiEntry;
import it.polimi.ingsw.am49.view.gui.GuiManager;
import it.polimi.ingsw.am49.view.gui.SceneTitle;

import java.rmi.RemoteException;
import java.util.List;

public class GuiView extends View {

    private final GuiManager manager;

    public GuiView(MenuController menuController, RoomController roomController, GameController gameController) throws RemoteException {
        super(menuController, roomController, gameController);
        manager = new GuiManager(menuController, roomController, gameController);
    }

    @Override
    public void initialize() {
        GuiEntry.guiManagerInstance = this.manager;
        GuiEntry.main(new String[0]);
    }

    @Override
    public void showWelcome() {
        manager.changeScene(SceneTitle.WELCOME, false);
    }

    @Override
    public void showMainMenu() {
        manager.changeScene(SceneTitle.MAIN_MENU, true);
    }

    @Override
    public void showRoom(RoomInfo roomInfo) {
        manager.setRoomInfo(roomInfo);
        manager.changeScene(SceneTitle.ROOM, true);
    }

    @Override
    public void showStarterChoice(int starterCardId) {
        this.manager.setStarterCardId(starterCardId);
        this.manager.changeScene(SceneTitle.STARTER_CARD, true);
    }

    @Override
    public void showObjectiveChoice(List<Integer> objectiveIds) {
        this.manager.setObjectiveCardsIds(objectiveIds);
        this.manager.changeScene(SceneTitle.OBJECTIVE_CARDS, true);
    }

    @Override
    public void showGame(VirtualGame game) {
        super.setVirtualGame(game);
        this.manager.setVirtualGame(game);
        this.manager.changeScene(SceneTitle.OVERVIEW, true);
    }

    @Override
    public void receiveChatMessage(ChatMSG chatMSG) {

    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        this.manager.getCurrentController().roomUpdate(roomInfo, message);
    }

    @Override
    public void setVirtualGame(VirtualGame virtualGame) {
        super.setVirtualGame(virtualGame);
        this.manager.setVirtualGame(virtualGame);
    }
}
