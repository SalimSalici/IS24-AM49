package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class RoomInfoListCell extends ListCell<RoomInfoItem> {
    private final HBox hbox = new HBox();
    private final Text roomName = new Text();
    private final Text capacity = new Text();

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
