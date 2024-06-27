package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller class for the change username GUI screen.
 * Handles user interactions for changing the username.
 */
public class
ChangeUsernameController extends GuiController {

    @FXML
    private TextField usernameTextfield;

    @FXML
    private Button confButton, exitButton;

    /**
     * Initializes the controller by setting action handlers for the confirm and exit buttons.
     * The confirm button validates the username and changes the scene if valid.
     * The exit button clears the text field and returns to the main menu.
     */
    public void initialize(){
        usernameTextfield.setOnAction(e -> execute());
        confButton.setOnAction(e -> execute());

        exitButton.setOnAction(x -> {
            this.manager.changeScene(SceneTitle.MAIN_MENU, true);
            usernameTextfield.clear();
        });
    }

    private void execute(){
        String text = usernameTextfield.getText();
        if(this.isUsernameValid(text)) {
            ClientApp.setUsername(text);
            this.manager.changeScene(SceneTitle.MAIN_MENU, true);
        }else {
            System.out.println("The username your trying to use is not allowed");
            showErrorPopup("The username your trying to use is not allowed");
        }

        usernameTextfield.clear();
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
}
