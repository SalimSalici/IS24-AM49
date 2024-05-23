package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.rmi.RemoteException;
import java.util.List;

public class ObjectiveCardsController extends GuiController{
    @FXML
    private ImageView firstobjImageview, secondobjImageview, starterImageview;

    private Server server;

    @Override
    public void init() {
        this.server = this.app.getServer();

        int starterCardId = this.manager.getStarterCardId();
        List<Integer> objectiveCardIds = this.manager.getObjectiveCardsIds();

        Image startingImage = getImageByCardId(starterCardId, true);
        starterImageview.setImage(startingImage);

        int firstObj = objectiveCardIds.get(0);
        Image firstobjImage = getImageByCardId(firstObj, true);
        firstobjImageview.setImage(firstobjImage);

        int secondObj = objectiveCardIds.get(1);
        Image secondobjImage = getImageByCardId(secondObj, true);
        secondobjImageview.setImage(secondobjImage);

        firstobjImageview.setOnMouseClicked(mouseEvent -> {
            chooseObj(firstObj);
        });

        secondobjImageview.setOnMouseClicked(mouseEvent -> {
            chooseObj(secondObj);
        });
    }

    private void chooseObj(int objectiveId){
        try {
            this.server.executeAction(this.app, new ChooseObjectiveAction(this.app.getUsername(), objectiveId));
            this.app.getVirtualGame().getPlayerByUsername(this.app.getUsername()).setPersonalObjectiveId(objectiveId);
            this.manager.changeScene(SceneTitle.WAITING);
        } catch (NotInGameException | InvalidActionException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (NotYourTurnException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }
}
