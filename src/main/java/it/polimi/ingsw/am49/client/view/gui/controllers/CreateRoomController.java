package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.common.CommonConfig;
import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.common.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @Override
    public void init() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(CommonConfig.minPlayers, CommonConfig.maxPlayers, 2);
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
        this.manager.execute(() -> {
            try {
                this.menuController.createRoom(nameTextfield.getText(), numplayerSpinner.getValue());
            } catch (CreateRoomException | AlreadyInRoomException e){
                System.out.println(e.getMessage());
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            }

            nameTextfield.clear();
        });
    }
}
