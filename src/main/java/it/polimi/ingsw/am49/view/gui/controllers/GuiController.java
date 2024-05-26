package it.polimi.ingsw.am49.view.gui.controllers;


import it.polimi.ingsw.am49.client.GuiApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.view.gui.GuiManager;
import javafx.scene.control.Alert;
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

    protected Image getImageByCardId(int id, boolean flipped){
        String zeros = "0".repeat(3 - String.valueOf(id).length());
        String subFolder;

        if(!flipped){
            subFolder = "gold_cards_front";
        } else
            subFolder = "gold_card_back";

        System.out.println("/it/polimi/ingsw/am49/images/" + subFolder + "/" + zeros + id + ".png");

        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/" + subFolder + "/" + zeros + id + ".png")));
    }
    protected Image getImageByVirtualCard(VirtualCard virtualCard){
        return getImageByCardId(virtualCard.id(), virtualCard.flipped());
    }
    protected Image getImageByTotemColor(Color color){
        String fileColor;
        switch (color){
            case RED -> fileColor = "rouge";
            case BLUE -> fileColor = "bleu";
            case GREEN -> fileColor = "vert";
            case YELLOW -> fileColor = "jaune";
            default -> fileColor = "noir";
        }

        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/" + "CODEX_pion_" + fileColor + ".png")));
    }
    protected Image getImageBackByResource(Resource resource, boolean isGold){
        int cardId;
        switch (resource){
            case MUSHROOMS -> cardId = 1;
            case LEAVES -> cardId = 11;
            case WOLVES -> cardId = 21;
            case BUGS -> cardId = 31;
            default -> cardId = 0;
        }

        if (cardId == 0){
            throw new RuntimeException("The resource passed to the function is not valid");
        }

        if(isGold) cardId += 40; // questo offset permette di prendere la prima carta gold per ogni tipo di risorsa

        return getImageByCardId(cardId, true);
    }

    public void showErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }
}