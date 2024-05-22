package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.scenes.StarterCardScene;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.rmi.RemoteException;

public class RoomController extends GuiController {

    @FXML
    private Button notreadyButton, leaveButton;
    @FXML
    private Label titleLabel, playersLabel;
    @FXML
    private ListView<String> playersListview;
    @FXML
    private ImageView redImageview, greenImageview, blueImageview, yellowImageview, totemImageview;

    private Server server;
    private RoomInfo roomInfo;
    private Color totemColor;

    public void initialize(){

    }

    @Override
    public void init() {
        this.server = this.app.getServer();
        this.roomInfo = this.manager.getRoomInfo();

        titleLabel.setText("Room: " + this.roomInfo.roomName());

        drawPlayersList();
        drawPlayersCount();

        this.redImageview.setOnMouseClicked(e -> {
            this.totemColor = Color.RED;
            this.setColor();
        });

        this.greenImageview.setOnMouseClicked(e -> {
            this.totemColor = Color.GREEN;
            this.setColor();
        });

        this.blueImageview.setOnMouseClicked(e -> {
            this.totemColor = Color.BLUE;
            this.setColor();
        });

        this.yellowImageview.setOnMouseClicked(e -> {
            this.totemColor = Color.YELLOW;
            this.setColor();
        });

        this.notreadyButton.setOnAction(e -> {
            this.totemColor = null;
            this.setColor();
        });

        this.leaveButton.setOnAction(e -> {
            this.leaveRoom();
        });
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException {
        this.roomInfo = roomInfo;
        drawPlayersList();
        drawPlayersCount();
    }

    @Override
    public void gameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE) {
            GameStartedUpdate update = (GameStartedUpdate) gameUpdate;
            this.manager.setStarterCardId(update.starterCardId()); //TODO: CAPIRE COME PASSARE INFO TRA UNA SCENA E L'ALTRA
            this.manager.changeScene(SceneTitle.STARTER_CARD);
        }
    }

    private void leaveRoom(){
        this.totemColor = null;
        try{
            roomInfo = this.server.readyDown(this.app);
            this.server.leaveRoom(this.app);
            this.manager.changeScene(SceneTitle.MAIN_MENU);
        } catch (RemoteException e) {
            //TODO: handle exeption
            throw new RuntimeException(e);
        }

    }

    private void setColor(){
        try {
            // se il colore è not set
            if(totemColor == null){
                this.roomInfo = this.server.readyDown(this.app);
            }
            else{
                this.roomInfo = this.server.readyUp(this.app, totemColor);
                this.manager.setRoomInfo(this.roomInfo);
            }

            this.totemImageview.setImage(this.roomInfo.playersToColors().get(this.app.getUsername()) != null
                ? getImageByTotemColor(this.totemColor)
                : null
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color. Please try again.");
            this.totemColor = null;
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void drawPlayersCount(){
        Platform.runLater(() -> {
            playersLabel.setText("Players: " + this.roomInfo.playersToColors().size() + "/" + this.roomInfo.maxPlayers());
        });
    }

    private void drawPlayersList() {
        Platform.runLater(() -> {
            playersListview.getItems().clear();

            // populates playersListView
            playersListview.getItems().addAll(
                    this.roomInfo.playersToColors()
                            .entrySet()
                            .stream()
                            .filter(entry -> !entry.getKey().equals(this.app.getUsername()))
                            .map(
                                    entry -> entry.getKey() + " " + (entry.getValue() == null ? "not ready" : entry.getValue().toString().toLowerCase())
                            )
                            .toList()
            );

            System.out.println("Players List Updated: " + playersListview.getItems()); // Debug
        });
    }
}

