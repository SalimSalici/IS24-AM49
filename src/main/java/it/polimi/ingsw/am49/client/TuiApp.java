package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.view.tui.SceneManager;

import java.rmi.RemoteException;

public class TuiApp extends ClientApp {

    private final SceneManager sceneManager;
    public TuiApp() throws RemoteException {
        this.sceneManager = new SceneManager(this);
    }

    public void initialize() {
        this.sceneManager.initialize();
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        this.sceneManager.roomUpdate(roomInfo, message);
    }

    @Override
    public void receiveGameUpdate(GameUpdate gameUpdate) {
        super.receiveGameUpdate(gameUpdate);
        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE)
            this.sceneManager.gameStarted((GameStartedUpdate) gameUpdate);
        else if (gameUpdate.getType() == GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE)
            this.sceneManager.chooseObjectiveCardUpdate(((ChoosableObjectivesUpdate) gameUpdate).objectiveCards());
    }

    @Override
    public void receiveChatMessage(ChatMSG msg) {
        super.receiveChatMessage(msg);
        this.sceneManager.chatMessage(msg);
    }
}
