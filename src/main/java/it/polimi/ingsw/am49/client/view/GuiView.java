package it.polimi.ingsw.am49.client.view;

import it.polimi.ingsw.am49.client.view.gui.GuiEntry;
import it.polimi.ingsw.am49.client.view.gui.GuiManager;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;

import java.rmi.RemoteException;
import java.util.List;

/**
 * GuiView class extends View to manage GUI related functionalities.
 */
public class GuiView extends View {

    private final GuiManager manager;

    /**
     * Constructs a GuiView with controllers for menu, room, and game.
     * @param menuController the controller for menu operations
     * @param roomController the controller for room operations
     * @param gameController the controller for game operations
     */
    public GuiView(MenuController menuController, RoomController roomController, GameController gameController) {
        super(menuController, roomController, gameController);
        manager = new GuiManager(menuController, roomController, gameController);
    }

    /**
     * Initializes the GUI by setting the manager instance and starting the main GUI entry point.
     */
    @Override
    public void initialize() {
        GuiEntry.guiManagerInstance = this.manager;
        GuiEntry.main(new String[0]);
    }

    /**
     * Displays a welcome message or screen.
     */
    @Override
    public void showWelcome() {
        manager.changeScene(SceneTitle.WELCOME, false);
    }

    /**
     * Displays the main menu.
     */
    @Override
    public void showMainMenu() {
        manager.changeScene(SceneTitle.MAIN_MENU, true);
    }

    /**
     * Displays the room view with the given room information.
     * @param roomInfo the room information to display
     */
    @Override
    public void showRoom(RoomInfo roomInfo) {
        manager.setRoomInfo(roomInfo);
        manager.changeScene(SceneTitle.ROOM, true);
    }

    /**
     * Displays the starter card choice interface.
     * @param starterCardId the ID of the starter card to display
     */
    @Override
    public void showStarterChoice(int starterCardId) {
        this.manager.setStarterCardId(starterCardId);
        this.manager.changeScene(SceneTitle.STARTER_CARD, true);
    }

    /**
     * Displays the objective card choice interface with the given list of objective IDs.
     * @param objectiveIds the list of objective card IDs to display
     */
    @Override
    public void showObjectiveChoice(List<Integer> objectiveIds) {
        this.manager.setObjectiveCardsIds(objectiveIds);
        this.manager.changeScene(SceneTitle.OBJECTIVE_CARDS, true);
    }

    /**
     * Displays the game overview with the given virtual game state.
     * @param game the virtual game state to display
     */
    @Override
    public void showGame(VirtualGame game) {
        super.setVirtualGame(game);
        this.manager.setVirtualGame(game);
        this.manager.changeScene(SceneTitle.OVERVIEW, true);
    }

    /**
     * Receives and processes a chat message.
     * @param chatMSG the chat message received
     */
    @Override
    public void receiveChatMessage(ChatMSG chatMSG) {

    }

    /**
     * Updates the room view based on the given room information and message.
     * @param roomInfo the updated room information
     * @param message the message related to the room update
     */
    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) {
        this.manager.getCurrentController().roomUpdate(roomInfo, message);
    }

    /**
     * Sets the virtual game state.
     * @param virtualGame the virtual game state to set
     */
    @Override
    public void setVirtualGame(VirtualGame virtualGame) {
        super.setVirtualGame(virtualGame);
        this.manager.setVirtualGame(virtualGame);
    }
}
