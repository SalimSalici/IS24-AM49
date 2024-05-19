package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.scenes.RoomScene;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.JoinRoomException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.rmi.RemoteException;
import java.util.List;

public class MainMenuController extends GuiController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Button joinroomButton, createroomButton, usernameButton, refreshButton;

    @FXML
    private ListView<String> roomsListview;

    private String selectedRoom;
    private Server server;

    private List<RoomInfo> rooms;

    @Override
    public void init(){
        this.server = this.app.getServer();
        refreshRooms();
        roomsListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
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

    private void refreshRooms(){
        roomsListview.getItems().clear();
        try {
            this.rooms = this.server.fetchRooms(this.app);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        roomsListview.getItems().addAll(rooms.stream().map(RoomInfo::roomName).toList()); //populates roomsListView
    }

    private void joinRoom(){
        try {
            System.out.println("Joining room: " + selectedRoom);
            RoomInfo roomInfo = this.server.joinRoom(this.app, selectedRoom, this.app.getUsername());
            this.manager.setRoomInfo(roomInfo);
            this.manager.changeScene(SceneTitle.ROOM);
            //this.manager.changeScene();
        } catch (JoinRoomException e) {
            // TODO: Handle exception
            System.out.println(e.getMessage());
            return;
        } catch (AlreadyInRoomException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void changeUsername(){
        this.manager.changeScene(SceneTitle.CHANGE_USERNAME);
    }

    private void createRoom(){
        this.manager.changeScene(SceneTitle.CREATE_ROOM);
    }
}
