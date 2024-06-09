package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PlayerInfoListCell extends ListCell<PlayerInfoItem> {
    private final HBox hbox = new HBox();
    private final ImageView totemImage = new ImageView();
    private final Text playerName = new Text();

    public PlayerInfoListCell() {
        super();
        totemImage.setFitWidth(35);
        totemImage.setFitHeight(35);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(totemImage, playerName);
    }

    @Override
    protected void updateItem(PlayerInfoItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            totemImage.setImage(item.getImage());
            playerName.setText(item.getText());
            setGraphic(hbox);
        }
    }
}
