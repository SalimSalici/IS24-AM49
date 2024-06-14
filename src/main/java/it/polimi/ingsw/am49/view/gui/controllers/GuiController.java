package it.polimi.ingsw.am49.view.gui.controllers;


import it.polimi.ingsw.am49.client.GuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.GuiManager;
import it.polimi.ingsw.am49.view.gui.GuiTextureManager;
import javafx.scene.control.Alert;

/**
 * Abstract base class for GUI controllers.
 * Provides common functionalities and structure for all GUI controllers.
 */
public abstract class GuiController {
    protected GuiManager manager;
    protected GuiApp app;
    protected GuiTextureManager guiTextureManager;

    /**
     * Sets the GUI application and manager for this controller.
     * Initializes the {@link GuiTextureManager} instance.
     *
     * @param app the GUI application
     * @param manager the GUI manager
     */
    public void setGui(GuiApp app, GuiManager manager){
        this.manager = manager;
        this.app = app;
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
     * @throws InvalidSceneException if the current scene is invalid for this update
     */
    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException{}

    /**
     * Updates the game state.
     * This method can be overridden by subclasses to handle game updates.
     *
     * @param gameUpdate the new game update
     * @throws InvalidSceneException if the current scene is invalid for this update
     */
    public void gameUpdate(GameUpdate gameUpdate) throws InvalidSceneException{}

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

        alert.showAndWait();
    }
}