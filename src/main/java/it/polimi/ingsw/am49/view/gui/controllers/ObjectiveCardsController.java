package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
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

    private Server server;

    @Override
    public void init() {
        this.server = this.app.getServer();

        int starterCardId = this.manager.getStarterCardId();
        List<Integer> objectiveCardIds = this.manager.getObjectiveCardsIds();

        Image startingImage = this.guiTextureManager.getCardImage(starterCardId, this.app.getVirtualGame().getPlayerByUsername(this.app.getUsername()).getStarterCard().flipped());
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
        this.manager.executorService.submit(() -> {
            try {
                this.manager.changeScene(SceneTitle.WAITING, true);
                this.app.getVirtualGame().getPlayerByUsername(this.app.getUsername()).setPersonalObjectiveId(objectiveId);
                this.server.executeAction(this.app, new ChooseObjectiveAction(this.app.getUsername(), objectiveId));
            } catch (NotInGameException | InvalidActionException | NotYourTurnException | RemoteException | InvalidSceneException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
                Platform.runLater(() -> {
                    try {
                        this.manager.changeScene(SceneTitle.MAIN_MENU, true);
                    } catch (InvalidSceneException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });
    }
}
