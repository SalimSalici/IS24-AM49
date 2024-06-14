package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Represents a custom list cell for displaying room information in a ListView.
 * This cell includes the room name and its capacity.
 */
public class RoomInfoListCell extends ListCell<RoomInfoItem> {
    private final HBox hbox = new HBox();
    private final Text roomName = new Text();
    private final Text capacity = new Text();

    /**
     * Constructs a new RoomInfoListCell.
     * Initializes the HBox layout and the Text nodes for the room name and capacity.
     */
    public RoomInfoListCell() {
        super();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(30);
        hbox.getChildren().addAll(roomName, capacity);
    }

    @Override
    protected void updateItem(RoomInfoItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            roomName.setText(item.getRoomName());
            capacity.setText(item.getPlayersInside() + "/" + item.getMaxCapacity());
            setGraphic(hbox);
        }
    }
}
