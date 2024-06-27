package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;
import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.common.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.common.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

/**
 * Controller class for the main menu GUI screen.
 * Handles user interactions for joining, creating rooms, and changing username.
 */
public class MainMenuController extends GuiController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Button joinroomButton, createroomButton, usernameButton, refreshButton;

    @FXML
    private ListView<RoomInfoItem> roomsListview;

    private RoomInfoItem selectedRoom = null;
    private Server server;

    private List<RoomInfo> rooms;

    @Override
    public void init(){
        roomsListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RoomInfoItem>() {
            @Override
            public void changed(ObservableValue<? extends RoomInfoItem> observableValue, RoomInfoItem oldItem, RoomInfoItem newItem) {
                selectedRoom = roomsListview.getSelectionModel().getSelectedItem();
            }
        });
        usernameLabel.setText("Username: " + ClientApp.getUsername());

        refreshButton.setOnAction(e -> {
            refreshRooms();
            System.out.println("Refreshed rooms");
        });

        joinroomButton.setOnAction(e -> {
            joinRoom();
        });

        usernameButton.setOnAction(e -> {
           changeUsername();
        });

        createroomButton.setOnAction(e -> {
            createRoom();
        });

        Platform.runLater(this::refreshRooms);
    }

    /**
     * Refreshes the list of rooms by fetching room data from the server.
     */
    private void refreshRooms() {
        this.manager.execute(() -> {
            roomsListview.getItems().clear();

            this.rooms = this.menuController.fetchRooms();

            List<RoomInfoItem> roomItems = rooms.stream()
                    .map(room -> new RoomInfoItem(
                            room.roomName(),
                            room.maxPlayers(),
                            room.playersToColors().size()
                    ))
                    .toList();

            roomsListview.getItems().addAll(roomItems); // Populates roomsListView
            roomsListview.setCellFactory(param -> new RoomInfoListCell());
        });
    }

    /**
     * Attempts to join the selected room.
     * If successful, changes the scene to the room view.
     * If an exception occurs, shows an error popup with the appropriate message.
     */
    private void joinRoom(){
        if(selectedRoom == null)
            return;
        this.manager.execute(() -> {
            try {
                System.out.println("Joining room: " + selectedRoom);
//                RoomInfo roomInfo = this.server.joinRoom(this.app, selectedRoom.getRoomName(), ClientApp.getUsername());
//                this.manager.setRoomInfo(roomInfo);
//                this.manager.changeScene(SceneTitle.ROOM, true);
                this.menuController.joinRoom(selectedRoom.getRoomName());
            } catch (JoinRoomException e) {
                    Platform.runLater(() -> showErrorPopup(e.getMessage()));
                    System.out.println(e.getMessage());
                    return;
            } catch (AlreadyInRoomException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            } catch (GameAlreadyStartedException e) {
                try {
                    this.manager.setRoomInfo(selectedRoom.toRoomInfo());
                    this.menuController.reconnect(selectedRoom.getRoomName());
//                    this.app.loadGame(completeGameInfo);
//                    this.manager.changeScene(SceneTitle.OVERVIEW, true);
                } catch (AlreadyInRoomException | JoinRoomException ex) {
                    Platform.runLater(() -> showErrorPopup(e.getMessage()));
                }
            }
        });
    }

    /**
     * Changes the scene to the change username screen.
     */
    private void changeUsername(){
        this.manager.changeScene(SceneTitle.CHANGE_USERNAME, true);
    }

    /**
     * Changes the scene to the create room screen.
     */
    private void createRoom(){
        this.manager.changeScene(SceneTitle.CREATE_ROOM, true);
    }
}
