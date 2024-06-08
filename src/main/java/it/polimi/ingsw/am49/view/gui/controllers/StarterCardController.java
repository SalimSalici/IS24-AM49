package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotInGameException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.rmi.RemoteException;
import java.util.Objects;

public class StarterCardController extends GuiController{
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView frontImageview, backImageview;

    private Server server;

    @Override
    public void init() {
        this.server = this.app.getServer();

        descriptionLabel.setText(this.app.getUsername() + " choose the side  of your starter card");

        int starterCardId = this.manager.getStarterCardId();
        
        Image frontImage = this.guiTextureManager.getCardImage(starterCardId, false);
        Image backImage = this.guiTextureManager.getCardImage(starterCardId, true);

        frontImageview.setImage(frontImage);
        backImageview.setImage(backImage);

        frontImageview.setOnMouseClicked(mouseEvent -> {
            chooseSide(false);
        });

        backImageview.setOnMouseClicked(mouseEvent -> {
            chooseSide(true);
        });
    }

    private void chooseSide(boolean flipped){
        try {
            this.server.executeAction(this.app, new ChooseStarterSideAction(this.app.getUsername(), flipped));
            this.manager.changeScene(SceneTitle.WAITING);
        } catch (InvalidActionException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (NotInGameException e) {
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
