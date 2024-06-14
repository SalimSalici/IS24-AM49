package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


/**
 * Represents a custom list cell for displaying player information in a ListView.
 * This cell includes an image and a text label for each player.
 */
public class PlayerInfoListCell extends ListCell<PlayerInfoItem> {
    private final HBox hbox = new HBox();
    private final ImageView totemImage = new ImageView();
    private final Text playerName = new Text();

    /**
     * Constructs a new PlayerInfoListCell.
     * Initializes the HBox layout, the ImageView for the player's totem, and the Text for the player's name.
     */
    public PlayerInfoListCell() {
        super();
        totemImage.setFitWidth(35);
        totemImage.setFitHeight(35);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(totemImage, playerName);
    }

    /**
     * Updates the content of this ListCell.
     * If the item is not empty or null, it sets the totem image and player name from the provided PlayerInfoItem.
     * Otherwise, it clears the content.
     *
     * @param item  the PlayerInfoItem to display in this cell
     * @param empty whether this cell is empty
     */
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
