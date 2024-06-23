package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class ClientSetUpController extends GuiController{

    @FXML
    private TextField serveripTextfield;

    @FXML
    private TextField serverportTextfield;

    @FXML
    private ToggleButton rmiTogglebutton, socketTogglebutton;

    @FXML
    private Button connecttoserverButton;

    private String ip = "", port = "", connectionTipe = "rmi";

    @Override
    public void init() {

        serveripTextfield.setOnAction(e -> { ip = serveripTextfield.getText(); });

        serverportTextfield.setOnAction(e -> { port = serverportTextfield.getText(); });

        rmiTogglebutton.setOnAction(e -> {
            connectionTipe = "rmi";

        });

        socketTogglebutton.setOnAction(e -> {
            connectionTipe = "socket";
        });

        connecttoserverButton.setOnAction(e-> connectToServer());

    }

    private void connectToServer() {
        if (isIpValid() && isPortValid()) {
            //TODO:try connection to server
        }
    }

    private boolean isIpValid() {
        return !ip.isEmpty() && (ip.contains(".") || ip.contains(":")) && ip.length() < 15 && ip.length() > 7;
    }

    private boolean isPortValid() {
        return !port.isEmpty() && !port.contains(":") && !port.contains(".") && port.length() <= 5;
    }
}
