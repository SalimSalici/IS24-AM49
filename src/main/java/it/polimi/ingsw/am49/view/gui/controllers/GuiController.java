package it.polimi.ingsw.am49.view.gui.controllers;


import it.polimi.ingsw.am49.client.GuiApp;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.GuiManager;
import javafx.scene.image.Image;

import java.util.Objects;

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

    protected Image getImageByCardId(int id, boolean front){
        String zeros = "0".repeat(3 - String.valueOf(id).length());
        String subFolder;

        if(front){
            subFolder = "gold_cards_front";
        } else
            subFolder = "gold_card_back";

        System.out.println("/it/polimi/ingsw/am49/images/" + subFolder + "/" + zeros + id + ".png");

        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/" + subFolder + "/" + zeros + id + ".png")));
    }
}