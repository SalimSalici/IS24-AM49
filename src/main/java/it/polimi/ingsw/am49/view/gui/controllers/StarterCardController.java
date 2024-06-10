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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.rmi.RemoteException;
import java.util.Objects;

public class StarterCardController extends GuiController{

    @FXML
    private TextFlow descriptionTextflow;
    @FXML
    private ImageView frontImageview, backImageview;

    private Server server;

    @Override
    public void init() {
        this.server = this.app.getServer();

        Text text1 = new Text("It's time to make a choice, ");
        Text text2 = new Text(this.app.getUsername());
        Text text3 = new Text(": which side of your starting card will you choose?");

        text1.setFont(Font.font("DejaVu Sans Mono", FontWeight.NORMAL, 20));
        text2.setFont(Font.font("DejaVu Sans Mono", FontWeight.EXTRA_BOLD, 22));
        text3.setFont(Font.font("DejaVu Sans Mono", FontWeight.NORMAL, 20));

        descriptionTextflow.getChildren().addAll(text1, text2, text3);

        int starterCardId = this.manager.getStarterCardId();
        
        Image frontImage = this.guiTextureManager.getCardImage(starterCardId, false);
        Image backImage = this.guiTextureManager.getCardImage(starterCardId, true);

        frontImageview.setImage(frontImage);
        backImageview.setImage(backImage);

        frontImageview.applyCss();
        backImageview.applyCss();

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
