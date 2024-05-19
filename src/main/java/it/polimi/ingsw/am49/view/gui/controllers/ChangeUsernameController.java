package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChangeUsernameController extends GuiController {

    @FXML
    private TextField usernameTextfield;

    @FXML
    private Label newUsernameLabel;

    @FXML
    private Button confirmButton;

    public void initialize(){
        confirmButton.setOnAction(e -> {
            if(this.isUsernameValid(usernameTextfield.getText())) {
                app.setUsername(usernameTextfield.getText());
                this.manager.changeScene(SceneTitle.MAIN_MENU);
            }else System.out.println("The username your trying to use is not allowed");
        });
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 2 && username.length() <= 20;
    }
}
