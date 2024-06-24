package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ConnectorType;
import it.polimi.ingsw.am49.client.view.gui.controllers.GuiController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.rmi.RemoteException;

public class ServerSetUpController extends GuiController {

    @FXML
    private TextField serveripTextfield;

    @FXML
    private TextField serverportTextfield;

    @FXML
    private ToggleButton rmiTogglebutton, socketTogglebutton;

    @FXML
    private Button connecttoserverButton;

    private String ip, port;

    private ConnectorType connectorType = ConnectorType.RMI;
    private boolean errorDisplayed = true; // Flag to track if error is displayed

    @Override
    public void init() {
        ip = serveripTextfield.getText();
        port = serverportTextfield.getText();
        // Update ip and port when the text fields lose focus
        serveripTextfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // focus lost
                if (!ip.equals(serveripTextfield.getText()))
                    errorDisplayed = false;
                ip = serveripTextfield.getText();
                isIpValid();
            }
        });

        serverportTextfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // focus lost
                if (!port.equals(serverportTextfield.getText()))
                    errorDisplayed = false;
                port = serverportTextfield.getText();
                isPortValid();
            }
        });

        rmiTogglebutton.setOnAction(e -> {
            connectorType = ConnectorType.RMI;
        });

        socketTogglebutton.setOnAction(e -> {
            connectorType = ConnectorType.SOCKET;
        });

        connecttoserverButton.setOnAction(e -> connectToServer());
    }

    private void connectToServer() {
        ip = serveripTextfield.getText();
        port = serverportTextfield.getText();
        errorDisplayed = false;
        if (isIpValid() && isPortValid()) {
            try {
                menuController.connectToServer(ip, Integer.parseInt(port), connectorType);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isIpValid() {
        if (!ip.isEmpty() && ip.contains(".") && ip.length() < 15 && ip.length() > 7) {
            return true;
        } else {
            if (!errorDisplayed) {
                showErrorPopup("IP address " + ip + " is invalid");
                errorDisplayed = true; // Set the flag to true
            }
        }
        return false;
    }


    private boolean isPortValid() {
        if (!port.isEmpty() && port.length() <= 5) {
            return true;
        } else {
                if (!errorDisplayed) {
                    showErrorPopup("Port address " + port + " is invalid");
                    errorDisplayed = true;
                }
            return false;
        }
    }

}
