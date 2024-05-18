package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class StarterCardController extends GuiController{
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView frontImageview, backImageview;

    @Override
    public void init() {
        descriptionLabel.setText(this.app.getUsername() + " choose the side  of your starter card");

        frontImageview.setOnMouseClicked(mouseEvent -> {
            chooseSide(false);
        });
    }

    private void chooseSide(boolean flipped){

    }


}
