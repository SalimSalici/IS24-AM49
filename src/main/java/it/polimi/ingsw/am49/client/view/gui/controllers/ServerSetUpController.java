package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.ClientConfig;
import it.polimi.ingsw.am49.client.connectors.ConnectorType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.rmi.RemoteException;

/**
 * Controller class for the server setup screen in the GUI. Handles user inputs
 * for server IP, port, and connection type, and attempts to connect to the server.
 */
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

    /**
     * Initializes the server setup screen. If server configuration is already provided in
     * the client configuration, it uses that to connect to the server. It also sets up
     * listeners for input validation and updates connector type based on user selection.
     */
    @Override
    public void init() {

        if (ClientConfig.connectionType != null && ClientConfig.serverHost != null && ClientConfig.serverPort != null){
            ip = ClientConfig.serverHost;
            port = ClientConfig.serverPort.toString();
            connectorType = ClientConfig.connectionType;
            connectToServer();
        }

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

    /**
     * Attempts to connect to the server using the provided IP address, port, and connector type.
     * Validates the IP address and port before attempting the connection. Throws a RuntimeException
     * if a RemoteException occurs during the connection attempt.
     */
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
