package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.view.gui.SceneTitle;
import it.polimi.ingsw.am49.view.tui.scenes.InvalidSceneException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller class for the welcome GUI screen.
 * Handles user interactions for setting the username and navigating to the main menu.
 */
public class WelcomeController extends GuiController {
    @FXML
    private Button confirmButton;

    @FXML
    private TextField usernameTextfield;

    /**
     * Initializes the controller by setting up action handlers for the confirm button and the username text field.
     */
    public void initialize(){
        confirmButton.setOnAction(e -> execute());
        usernameTextfield.setOnAction(e -> execute());
    }

    /**
     * Validates the given username.
     * The username must be between 2 and 20 characters long.
     *
     * @param username the username to validate
     * @return true if the username is valid, false otherwise
     */
    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }

    /**
     * Executes the action when the confirm button or the username text field is activated.
     * If the username is valid, sets the username in the application and changes the scene to the main menu.
     * If the username is invalid, prints an error message.
     */
    private void execute(){
        if(this.isUsernameValid(usernameTextfield.getText())) {
            app.setUsername(usernameTextfield.getText());
            try {
                this.manager.changeScene(SceneTitle.MAIN_MENU, true);
            } catch (InvalidSceneException e) {
                Platform.runLater(() -> showErrorPopup(e.getMessage()));
            }
        }else System.out.println("The username your trying to use is not allowed");
    }
}
