package it.polimi.ingsw.am49.view.gui.controllers;


import it.polimi.ingsw.am49.client.GuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.GuiManager;
import it.polimi.ingsw.am49.view.gui.GuiTextureManager;
import javafx.scene.control.Alert;

public abstract class GuiController {
    protected GuiManager manager;
    protected GuiApp app;
    protected GuiTextureManager guiTextureManager;

    public void setGui(GuiApp app, GuiManager manager){
        this.manager = manager;
        this.app = app;
        this.guiTextureManager = GuiTextureManager.getInstance();

        //this.init();
    }

    public void init(){}

    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException{}

    public void gameUpdate(GameUpdate gameUpdate) throws InvalidSceneException{}

    public void showErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }
}