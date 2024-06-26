package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotInGameException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller class for the objective cards GUI screen.
 * Handles user interactions for selecting objective cards.
 */
public class ObjectiveCardsController extends GuiController{
    @FXML
    private ImageView firstobjImageview, secondobjImageview, starterImageview;

    @Override
    public void init() {

        int starterCardId = this.manager.getStarterCardId();
        List<Integer> objectiveCardIds = this.manager.getObjectiveCardsIds();

        Image startingImage = this.guiTextureManager.getCardImage(starterCardId, this.manager.getVirtualGame().getPlayerByUsername(ClientApp.getUsername()).getStarterCard().flipped());
        starterImageview.setImage(startingImage);

        int firstObj = objectiveCardIds.get(0);
        Image firstobjImage = this.guiTextureManager.getCardImage(firstObj, false);
        firstobjImageview.setImage(firstobjImage);

        int secondObj = objectiveCardIds.get(1);
        Image secondobjImage = this.guiTextureManager.getCardImage(secondObj, false);
        secondobjImageview.setImage(secondobjImage);

        firstobjImageview.setOnMouseClicked(mouseEvent -> {
            chooseObj(firstObj);
        });

        secondobjImageview.setOnMouseClicked(mouseEvent -> {
            chooseObj(secondObj);
        });
    }

    /**
     * Sends the chosen objective card to the server and updates the player's objective.
     * If an error occurs, shows an error popup with the appropriate message.
     *
     * @param objectiveId the ID of the chosen objective card
     */
    private void chooseObj(int objectiveId){
        this.manager.execute(() -> {
            try {
                this.manager.changeScene(SceneTitle.WAITING, true);
                this.manager.getVirtualGame().getPlayerByUsername(ClientApp.getUsername()).setPersonalObjectiveId(objectiveId);
                this.gameController.chooseObjective(objectiveId);
            } catch (NotInGameException | InvalidActionException | NotYourTurnException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
                Platform.runLater(() -> {this.gameController.leave();});
            }
        });
    }
}
