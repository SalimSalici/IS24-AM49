package it.polimi.ingsw.am49.client.view;

import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.client.view.tui.SceneManager;
import it.polimi.ingsw.am49.client.view.tui.scenes.SceneType;

import java.util.List;

/**
 * Text-based user interface view that handles the display and interaction of scenes in the game.
 */
public class TuiView extends View {

    private final SceneManager sceneManager;

    /**
     * Constructs a TuiView with controllers for menu, room, and game.
     * @param menuController the controller for menu operations
     * @param roomController the controller for room operations
     * @param gameController the controller for game operations
     */
    public TuiView(MenuController menuController, RoomController roomController, GameController gameController) {
        super(menuController, roomController, gameController);
        sceneManager = new SceneManager(menuController, roomController, gameController);
    }

    /**
     * Initializes the scene manager.
     */
    @Override
    public void initialize() {
        this.sceneManager.initialize();
    }

    /**
     * Initializes the scene manager.
     */
    @Override
    public void showServerSelection() {
        this.sceneManager.forceServerSelection();
    }

    /**
     * Displays the welcome scene.
     */
    @Override
    public void showWelcome() {
        sceneManager.switchScene(SceneType.WELCOME_SCENE);
    }

    /**
     * Displays the main menu scene after destroying player-specific scenes.
     */
    @Override
    public void showMainMenu() {
        sceneManager.destroyPlayerScenes();
        sceneManager.switchScene(SceneType.MAIN_MENU_SCENE);
    }

    /**
     * Displays the room scene based on the provided room information.
     * @param roomInfo information about the room to be displayed
     */
    @Override
    public void showRoom(RoomInfo roomInfo) {
        sceneManager.switchScene(roomInfo);
    }

    /**
     * Notifies the scene manager of the starter card choice.
     * @param starterCardId the ID of the chosen starter card
     */
    @Override
    public void showStarterChoice(int starterCardId) {
        sceneManager.gameStarted(starterCardId);
    }

    /**
     * Notifies the scene manager of the objective card choices.
     * @param objectiveIds a list of IDs for the objective cards to choose from
     */
    @Override
    public void showObjectiveChoice(List<Integer> objectiveIds) {
        sceneManager.chooseObjectiveCardUpdate(objectiveIds);
    }

    /**
     * Displays the game overview scene and initializes player scenes.
     * @param game the virtual game model to be displayed
     */
    @Override
    public void showGame(VirtualGame game) {
        super.setVirtualGame(game);
        this.sceneManager.setVirtualGame(game);
        this.sceneManager.initializePlayerScenes();
        sceneManager.switchScene(SceneType.OVERVIEW_SCENE);
    }

    /**
     * Receives and processes a chat message.
     * @param chatMSG the chat message to be processed
     */
    @Override
    public void receiveChatMessage(ChatMSG chatMSG) {
        this.sceneManager.chatMessage(chatMSG);
    }

    /**
     * Updates the room view based on the given room information and message.
     * @param roomInfo the updated room information
     * @param message the message associated with the room update
     */
    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        sceneManager.roomUpdate(roomInfo, message);
    }

    /**
     * Sets the virtual game model for the view and the scene manager.
     * @param virtualGame the virtual game model to be set
     */
    @Override
    public void setVirtualGame(VirtualGame virtualGame) {
        super.setVirtualGame(virtualGame);
        this.sceneManager.setVirtualGame(virtualGame);
    }
}
