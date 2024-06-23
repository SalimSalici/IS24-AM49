package it.polimi.ingsw.am49.view;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.controller.room.RoomInfo;

import java.util.List;

/**
 * Abstract class representing the view in the MVC architecture.
 * This class provides the structure for UI components and interaction handling.
 */
public abstract class View {
    protected final MenuController menuController;
    protected final RoomController roomController;
    protected final GameController gameController;
    protected VirtualGame virtualGame;

    /**
     * Constructs a View with specified controllers.
     * @param menuController the controller for menu-related actions
     * @param roomController the controller for room-related actions
     * @param gameController the controller for game-related actions
     */
    public View(MenuController menuController, RoomController roomController, GameController gameController) {
        this.menuController = menuController;
        this.roomController = roomController;
        this.gameController = gameController;
    }

    /**
     * Initializes the view components.
     */
    public abstract void initialize();

    /**
     * Displays the welcome message.
     */
    public abstract void showWelcome();

    /**
     * Displays the main menu.
     */
    public abstract void showMainMenu();

    /**
     * Displays the room information.
     * @param roomInfo the information about the room
     */
    public abstract void showRoom(RoomInfo roomInfo);

    /**
     * Displays the starter card choice.
     * @param starterCardId the ID of the starter card
     */
    public abstract void showStarterChoice(int starterCardId);

    /**
     * Displays the objective choices.
     * @param objectiveIds a list of objective IDs
     */
    public abstract void showObjectiveChoice(List<Integer> objectiveIds);

    /**
     * Displays the game state.
     * @param game the virtual representation of the game
     */
    public abstract void showGame(VirtualGame game);

    /**
     * Receives and processes a chat message.
     * @param chatMSG the chat message received
     */
    public abstract void receiveChatMessage(ChatMSG chatMSG);

    /**
     * Updates the room view with new information or messages.
     * @param roomInfo updated room information
     * @param message additional message to be displayed
     */
    public abstract void roomUpdate(RoomInfo roomInfo, String message);

    /**
     * Sets the virtual game model.
     * @param virtualGame the virtual game to set
     */
    public void setVirtualGame(VirtualGame virtualGame) {
        this.virtualGame = virtualGame;
    }
}
