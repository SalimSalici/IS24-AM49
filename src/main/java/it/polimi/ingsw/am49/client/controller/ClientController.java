package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.view.View;

/**
 * Abstract base class for client controllers in the application.
 * This class provides basic fields and methods to manage a connection and interaction
 * with the server and the associated view.
 */
public abstract class ClientController {

    protected Server server;  // Server instance to communicate with
    protected ClientApp client;  // Client application instance
    protected View view;  // View interface for UI interactions

    /**
     * Constructs a new ClientController with a specified server and client application.
     * 
     * @param server the server instance to be used by this controller
     * @param client the client application instance
     */
    protected ClientController(Server server, ClientApp client) {
        this.server = server;
        this.client = client;
    }

    /**
     * Sets the server instance for this controller.
     * 
     * @param server the new server instance
     */
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Sets the view instance for this controller.
     * 
     * @param view the new view instance
     */
    public void setView(View view) {
        this.view = view;
    }
}
