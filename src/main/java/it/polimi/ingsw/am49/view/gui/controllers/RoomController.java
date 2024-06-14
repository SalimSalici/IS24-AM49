package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.gameupdates.GameStartedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdateType;
import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.RoomException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.rmi.RemoteException;

/**
 * Controller class for the room GUI screen.
 * Handles user interactions within a game room, such as selecting a totem color, leaving the room, and updating room state.
 */
public class RoomController extends GuiController {

    @FXML
    private Button notreadyButton, leaveButton;
    @FXML
    private Label titleLabel, playersLabel;
    @FXML
    private ListView<PlayerInfoItem> playersListview;
    @FXML
    private ImageView redImageview, greenImageview, blueImageview, yellowImageview, totemImageview;
    @FXML
    private HBox totemHBox;

    private Server server;
    private RoomInfo roomInfo;
    private Color totemColor;

    public void initialize(){

    }

    @Override
    public void init() {
        this.server = this.app.getServer();
        this.roomInfo = this.manager.getRoomInfo();

        totemHBox.setVisible(false);

        titleLabel.setText(this.roomInfo.roomName());

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
            this.manager.setStarterCardId(update.starterCardId());
            try {
                this.manager.changeScene(SceneTitle.STARTER_CARD);
            } catch (InvalidSceneException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Leaves the current room and changes the scene to the main menu.
     * If an error occurs, shows an error popup with the appropriate message.
     */
    private void leaveRoom(){
        this.manager.executorService.submit(() -> {
            this.totemColor = null;
            try{
                roomInfo = this.server.readyDown(this.app);
                this.server.leaveRoom(this.app);
                this.manager.changeScene(SceneTitle.MAIN_MENU);
            } catch (RemoteException | RoomException | InvalidSceneException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Sets the totem color for the player and updates the room state.
     * If an error occurs, shows an error popup with the appropriate message.
     */
    private void setColor(){
        this.manager.executorService.submit(() -> {
            try {
                // if color isn't set
                if(totemColor == null){
                    this.roomInfo = this.server.readyDown(this.app);
                }
                else{
                    this.roomInfo = this.server.readyUp(this.app, totemColor);
                    this.manager.setRoomInfo(this.roomInfo);
                }

                if(this.roomInfo.playersToColors().get(this.app.getUsername()) == null){
                    totemHBox.setVisible(false);

                }else{
                    totemHBox.setVisible(true);
                    this.totemImageview.setImage(this.guiTextureManager.getImageByTotemColor(this.totemColor));
                }

            } catch (IllegalArgumentException e) {
                this.totemColor = null;
            } catch (RemoteException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
                throw new RuntimeException(e);
            } catch (RoomException e){
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            }
        });
    }

    /**
     * Updates the players count label in the room.
     */
    private void drawPlayersCount(){
        Platform.runLater(() -> {
            playersLabel.setText("Players: " + this.roomInfo.playersToColors().size() + "/" + this.roomInfo.maxPlayers());
        });
    }

    /**
     * Updates the players list in the room.
     */
    private void drawPlayersList() {
        Platform.runLater(() -> {
            playersListview.getItems().clear();
            playersListview.setCellFactory(param -> new PlayerInfoListCell());

            // Populates playersListView with PlayerInfoItem objects
            playersListview.getItems().addAll(
                    this.roomInfo.playersToColors()
                            .entrySet()
                            .stream()
                            .filter(entry -> !entry.getKey().equals(this.app.getUsername()))
                            .map(entry -> new PlayerInfoItem(
                                    entry.getKey(),
                                    entry.getValue() == null ? null : this.guiTextureManager.getImageByTotemColor(entry.getValue())
                            ))
                            .toList()
            );
        });
    }
}

