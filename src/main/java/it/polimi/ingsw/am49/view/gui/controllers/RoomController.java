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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

public class RoomController extends GuiController {

    @FXML
    Button redButton, greenButton, blueButton, yellowButton, notreadyButton, leaveButton;
    @FXML
    Label titleLabel, playersLabel, statusLabel;
    @FXML
    ListView<String> playersListview;

    private Server server;
    private RoomInfo roomInfo;
    private boolean isUserReady;
    private Color buttonColor;

    public void initialize(){

    }

    @Override
    public void init() {
        this.server = this.app.getServer();
        this.roomInfo = this.manager.getRoomInfo();
        this.isUserReady = false;

        titleLabel.setText("Room: " + this.roomInfo.roomName());

        drawPlayersList();

        this.redButton.setOnAction(e -> {
            this.buttonColor = Color.RED;
            this.setColor();
        });

        this.greenButton.setOnAction(e -> {
            this.buttonColor = Color.GREEN;
            this.setColor();
        });

        this.blueButton.setOnAction(e -> {
            this.buttonColor = Color.BLUE;
            this.setColor();
        });

        this.yellowButton.setOnAction(e -> {
            this.buttonColor = Color.YELLOW;
            this.setColor();
        });

        this.notreadyButton.setOnAction(e -> {
            this.buttonColor = null;
            this.setColor();
        });
    }

    @Override
    public void roomUpdate(RoomInfo roomInfo, String message) throws InvalidSceneException {
        this.roomInfo = roomInfo;
        drawPlayersList();
    }

    @Override
    public void gameUpdate(GameUpdate gameUpdate) {
        if (gameUpdate.getType() == GameUpdateType.GAME_STARTED_UPDATE) {
            GameStartedUpdate update = (GameStartedUpdate) gameUpdate;
            int starterCardId = update.starterCardId(); //TODO: CAPIRE COME PASSARE INFO TRA UNA SCENA E L'ALTRA
            //this.manager.changeScene();
        }
    }

    private void setColor(){
        try {
            // se il colore è not set
            if(buttonColor == null){
                //TODO: GESTISCI COLOR NOT SET
            }
            else{
                this.roomInfo = this.server.readyUp(this.app, buttonColor);
                this.isUserReady = true;
            }
            // aggiorna la status label
            this.statusLabel.setText("Status: " + this.roomInfo.playersToColors().get(this.app.getUsername()).toString().toLowerCase()); //TODO: POI DOVRA' STAMPARE LE IMMAGINI DEI TOKEN
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color. Please try again.");
            this.buttonColor = null;
            return;
        } catch (RemoteException e) {
            // TODO: Handle exception
            throw new RuntimeException(e);
        }
    }

    private void drawPlayersLabel(){
        this.playersLabel.setText("Players: " + this.roomInfo.playersToColors() + "/" + this.roomInfo.maxPlayers());
    }

    private void drawPlayersList(){
        playersListview.getItems().clear();

        // populates playersListView
        playersListview.getItems().addAll(
            this.roomInfo.playersToColors()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(this.app.getUsername()))
                .map(
                        entry -> entry.getKey() + " " + (entry.getValue() == null  ? "not ready" : entry.getValue().toString().toLowerCase())
                )
                .toList()
        );

        System.out.println("Players List Updated: " + playersListview.getItems()); // Debug
    }
}

