package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.CompleteGameInfo;
import it.polimi.ingsw.am49.controller.room.Room;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.GameAlreadyStartedException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.rmi.RemoteException;
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

    private RoomInfoItem selectedRoom;
    private Server server;

    private List<RoomInfo> rooms;

    @Override
    public void init(){
        this.server = this.app.getServer();
        refreshRooms();
        roomsListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RoomInfoItem>() {
            @Override
            public void changed(ObservableValue<? extends RoomInfoItem> observableValue, RoomInfoItem oldItem, RoomInfoItem newItem) {
                selectedRoom = roomsListview.getSelectionModel().getSelectedItem();
            }
        });
        usernameLabel.setText("Username: " + this.app.getUsername());

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
    }

    /**
     * Refreshes the list of rooms by fetching room data from the server.
     */
    private void refreshRooms() {
        this.manager.executorService.submit(() -> {
            roomsListview.getItems().clear();
            try {
                this.rooms = this.server.fetchRooms(this.app);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

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
        this.manager.executorService.submit(() -> {
            try {
                System.out.println("Joining room: " + selectedRoom);
                RoomInfo roomInfo = this.server.joinRoom(this.app, selectedRoom.getRoomName(), this.app.getUsername());
                this.manager.setRoomInfo(roomInfo);
                this.manager.changeScene(SceneTitle.ROOM, true);
                //this.manager.changeScene();
            } catch (JoinRoomException | InvalidSceneException e) {
                    Platform.runLater(() -> showErrorPopup(e.getMessage()));
                    System.out.println(e.getMessage());
                    return;
            } catch (AlreadyInRoomException | RemoteException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
                throw new RuntimeException(e);
            } catch (GameAlreadyStartedException e) {
                try {
                    CompleteGameInfo completeGameInfo = this.server.reconnect(this.app, selectedRoom.getRoomName(), this.app.getUsername());
                    this.app.loadGame(completeGameInfo);
                    this.manager.changeScene(SceneTitle.OVERVIEW, true);
                } catch (AlreadyInRoomException | JoinRoomException | RemoteException | InvalidSceneException ex) {
                    Platform.runLater(() -> showErrorPopup(e.getMessage()));
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Changes the scene to the change username screen.
     */
    private void changeUsername(){
        try {
            this.manager.changeScene(SceneTitle.CHANGE_USERNAME, true);
        } catch (InvalidSceneException e) {
            Platform.runLater(() -> showErrorPopup(e.getMessage()));
        }
    }

    /**
     * Changes the scene to the create room screen.
     */
    private void createRoom(){
        try {
            this.manager.changeScene(SceneTitle.CREATE_ROOM, true);
        } catch (InvalidSceneException e) {
            throw new RuntimeException(e);
        }
    }
}
