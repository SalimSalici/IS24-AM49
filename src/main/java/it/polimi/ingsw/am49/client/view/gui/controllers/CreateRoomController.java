package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.common.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.common.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.RemoteException;

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
//        this.server = this.app.getServer();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4, 2);
        numplayerSpinner.setValueFactory( valueFactory);

        createButton.setOnAction(x-> execute());
        nameTextfield.setOnAction(x-> execute());
        exitButton.setOnAction(x -> {
            this.nameTextfield.clear();
            this.manager.changeScene(SceneTitle.MAIN_MENU, true);
        });
    }

    /**
     * Executes the creation of a new room.
     * If successful, it sets the room information and changes the scene to the room view.
     * If an exception occurs, it shows an error popup with the appropriate message.
     */
    private void execute(){
        this.manager.executorService.submit(() -> {
            try {
                this.menuController.createRoom(nameTextfield.getText(), numplayerSpinner.getValue());
//                RoomInfo roomInfo = this.menuController.createRoom(nameTextfield.getText(), numplayerSpinner.getValue());
//                this.manager.setRoomInfo(roomInfo);
//                this.manager.changeScene(SceneTitle.ROOM, true);
            } catch (CreateRoomException | RemoteException | AlreadyInRoomException e){
                System.out.println(e.getMessage());
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            }

            nameTextfield.clear();
        });
    }
}
