package it.polimi.ingsw.am49.view;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.view.tui.SceneManager;
import it.polimi.ingsw.am49.view.tui.scenes.SceneType;

import java.util.List;

public class TuiView extends View {

    private final SceneManager sceneManager;

    public TuiView(MenuController menuController, RoomController roomController, GameController gameController) {
        super(menuController, roomController, gameController);
        sceneManager = new SceneManager(menuController, roomController, gameController);
    }

    @Override
    public void initialize() {
        this.sceneManager.initialize();
    }

    @Override
    public void showWelcome() {
        sceneManager.switchScene(SceneType.WELCOME_SCENE);
    }

    @Override
    public void showMainMenu() {
        sceneManager.destroyPlayerScenes();
        sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }

    @Override
    public void showRoom(RoomInfo roomInfo) {
        sceneManager.switchScene(roomInfo);
    }

    @Override
    public void showStarterChoice(int starterCardId) {
        sceneManager.gameStarted(starterCardId);
    }

    @Override
    public void showObjectiveChoice(List<Integer> objectiveIds) {
        sceneManager.chooseObjectiveCardUpdate(objectiveIds);
    }

    @Override
    public void showGame(VirtualGame game) {
        super.setVirtualGame(game);
        this.sceneManager.setVirtualGame(game);
        this.sceneManager.initializePlayerScenes();
        sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
    }

    @Override
    public void receiveChatMessage(ChatMSG chatMSG) {
        this.sceneManager.chatMessage(chatMSG);
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        sceneManager.roomUpdate(roomInfo, message);
    }

    @Override
    public void setVirtualGame(VirtualGame virtualGame) {
        super.setVirtualGame(virtualGame);
        this.sceneManager.setVirtualGame(virtualGame);
    }
}
