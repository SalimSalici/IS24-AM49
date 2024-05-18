package it.polimi.ingsw.am49.view.gui.controllers;


import it.polimi.ingsw.am49.client.GuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.GuiManager;

public abstract class GuiController {
    protected GuiManager manager;
    protected GuiApp app;

    public void setGui(GuiApp app, GuiManager manager){
        this.manager = manager;
        this.app = app;

        //this.init();
    }

    public void init(){}

    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException{}

    public void gameUpdate(GameUpdate gameUpdate) throws InvalidSceneException{}
}