package it.polimi.ingsw.am49.view;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.controller.room.RoomInfo;

import java.util.List;

public abstract class View {
    protected final MenuController menuController;
    protected final RoomController roomController;
    protected final GameController gameController;
    protected VirtualGame virtualGame;

    public View(MenuController menuController, RoomController roomController, GameController gameController) {
        this.menuController = menuController;
        this.roomController = roomController;
        this.gameController = gameController;
    }

    public abstract void initialize();
    public abstract void showWelcome();
    public abstract void showMainMenu();
    public abstract void showRoom(RoomInfo roomInfo);
    public abstract void showStarterChoice(int starterCardId);
    public abstract void showObjectiveChoice(List<Integer> objectiveIds);
    public abstract void showGame(VirtualGame game);
    public abstract void receiveChatMessage(ChatMSG chatMSG);

    public abstract void roomUpdate(RoomInfo roomInfo, String message);

    public void setVirtualGame(VirtualGame virtualGame) {
        this.virtualGame = virtualGame;
    }
}
