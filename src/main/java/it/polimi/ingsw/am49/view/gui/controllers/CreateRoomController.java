package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Controller class for the create room GUI screen.
 * Handles user interactions for creating a new game room.
 */
public class CreateRoomController extends GuiController {
    @FXML
    private TextField nameTextfield;

    @FXML
    private Spinner<Integer> numplayerSpinner;

    @FXML
    private Button createButton, exitButton;

    private Server server;

    @Override
    public void init() {
        this.server = this.app.getServer();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4, 2);
        numplayerSpinner.setValueFactory( valueFactory);

        createButton.setOnAction(x-> execute());
        nameTextfield.setOnAction(x-> execute());
        exitButton.setOnAction(x -> {
            this.nameTextfield.clear();
                    try {
                        this.manager.changeScene(SceneTitle.MAIN_MENU);
                    } catch (InvalidSceneException e) {
                        showErrorPopup(e.getMessage());
                    }
                }
        );
    }

    /**
     * Executes the creation of a new room.
     * If successful, it sets the room information and changes the scene to the room view.
     * If an exception occurs, it shows an error popup with the appropriate message.
     */
    private void execute(){
        this.manager.executorService.submit(() -> {
            try {
                RoomInfo roomInfo = this.server.createRoom(this.app, nameTextfield.getText(), numplayerSpinner.getValue(), this.app.getUsername());
                this.manager.setRoomInfo(roomInfo);
                this.manager.changeScene(SceneTitle.ROOM);
            } catch (CreateRoomException | RemoteException | AlreadyInRoomException e){
                System.out.println(e.getMessage());
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            } catch (InvalidSceneException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            }

            nameTextfield.clear();
        });
    }
}
