package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class WelcomeController extends GuiController {
    @FXML
    private Button confirmButton;

    @FXML
    private TextField usernameTextfield;

    public void initialize(){
        confirmButton.setOnAction(e -> execute());
        usernameTextfield.setOnAction(e -> execute());
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }

    private void execute(){
        if(this.isUsernameValid(usernameTextfield.getText())) {
            app.setUsername(usernameTextfield.getText());
            this.manager.changeScene(SceneTitle.MAIN_MENU);
        }else System.out.println("The username your trying to use is not allowed");
    }
}
