package it.polimi.ingsw.am49.client.view.gui.controllers;


import it.polimi.ingsw.am49.client.controller.GameController;
import it.polimi.ingsw.am49.client.controller.MenuController;
import it.polimi.ingsw.am49.client.controller.RoomController;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.client.view.tui.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.client.view.gui.GuiManager;
import it.polimi.ingsw.am49.client.view.gui.GuiTextureManager;
import javafx.scene.control.Alert;

/**
 * Abstract base class for GUI controllers.
 * Provides common functionalities and structure for all GUI controllers.
 */
public abstract class GuiController {
    protected GuiManager manager;
    protected GuiTextureManager guiTextureManager;
    protected MenuController menuController;
    protected RoomController roomController;
    protected GameController gameController;

    /**
     * Sets the GUI application and manager for this controller.
     * Initializes the {@link GuiTextureManager} instance.
     *
     * @param manager the GUI manager
     * @param menuController the menu controller
     * @param roomController the room controller
     * @param gameController the game controller
     */
    public void setGui(GuiManager manager, MenuController menuController, RoomController roomController, GameController gameController) {
        this.manager = manager;
        this.menuController = menuController;
        this.roomController = roomController;
        this.gameController = gameController;
        this.guiTextureManager = GuiTextureManager.getInstance();

        //this.init();
    }

    /**
     * Initializes the controller.
     * This method can be overridden by subclasses to provide specific initialization logic.
     */
    public void init(){}

    /**
     * Updates the room information.
     * This method can be overridden by subclasses to handle room updates.
     *
     * @param roomInfo the new room information
     * @param message a message associated with the update
     */
    public void roomUpdate(RoomInfo roomInfo, String message){}

    /**
     * Updates the game state.
     * This method can be overridden by subclasses to handle game updates.
     *
     * @param gameUpdate the new game update
     */
    public void gameUpdate(GameUpdate gameUpdate){}

    public void onClose() {}

    /**
     * Displays an error popup with the specified error message.
     *
     * @param errorMessage the error message to display
     */
    public void showErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);

        String css = this.getClass().getResource("/it/polimi/ingsw/am49/css/alert.css").toExternalForm();
        alert.getDialogPane().getStylesheets().add(css);
        alert.getDialogPane().getStyleClass().add("alert");

        alert.showAndWait();
    }
}