package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * Represents a custom list cell for displaying end game information in a ListView.
 * This cell includes the player's rank, totem image, username, points, and completed objectives.
 */
public class EndGameInfoListCell extends ListCell<EndGameInfoItem> {
    private final HBox hbox = new HBox();

    private final Label rank = new Label();
    private final ImageView totemImage = new ImageView();
    private final Label username = new Label();
    private final Label points = new Label();
    private final Label completedOb = new Label();

    /**
     * Constructs a new EndGameInfoListCell.
     * Initializes the HBox layout and the Text nodes for rank, username, points, completed objectives, and totem image.
     */
    public EndGameInfoListCell() {
        super();
        completedOb.setPrefWidth(130);
        completedOb.setAlignment(Pos.CENTER);
        points.setPrefWidth(40);
        points.setAlignment(Pos.CENTER);
        username.setPrefWidth(150);
        totemImage.setFitWidth(35);
        totemImage.setFitHeight(35);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(35);
        hbox.setPrefHeight(38.5);
        hbox.getChildren().addAll(rank, username, totemImage, points, completedOb);
    }

    @Override
    protected void updateItem(EndGameInfoItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            rank.setText(item.getRank());
            username.setText(item.getusername());
            totemImage.setImage(item.getTotemImage());
            points.setText(item.getpoints());
            completedOb.setText(item.getCompletedOb());
            setGraphic(hbox);
        }
    }
}
