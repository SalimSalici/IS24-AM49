package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.ClientConfig;
import it.polimi.ingsw.am49.client.connectors.ConnectorType;
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

    @Override
    public void init() {

        if (ClientConfig.connectionType != null && ClientConfig.serverHost != null && ClientConfig.serverPort != null){
            ip = ClientConfig.serverHost;
            port = ClientConfig.serverPort.toString();
            connectorType = ClientConfig.connectionType;
            connectToServer();
        } else {
            ip = serveripTextfield.getText();
            port = serverportTextfield.getText();
            // Update ip and port when the text fields lose focus
            serveripTextfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) { // focus lost
                    String tmp = serveripTextfield.getText();
                    if (!ip.equals(tmp) && !ClientApp.isIpValid(tmp))
                        showErrorPopup("IP address " + tmp + " is invalid");
                    ip = tmp;
                }
            });

            serverportTextfield.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) { // focus lost
                    String tmp = serverportTextfield.getText();
                    if (!port.equals(tmp) && !ClientApp.isPortValid(tmp))
                        showErrorPopup("Port " + tmp + " is invalid");
                    port = tmp;
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
    }

    private void connectToServer() {
        if (ClientApp.isIpValid(ip) && ClientApp.isPortValid(port)) {
            try {
                menuController.connectToServer(ip, Integer.parseInt(port), connectorType);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
