package it.polimi.ingsw.am49.client.controller;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.view.View;

public abstract class ClientController {

    protected Server server;
    protected ClientApp client;
    protected View view;

    protected ClientController(Server server, ClientApp client) {
        this.server = server;
        this.client = client;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setView(View view) {
        this.view = view;
    }
}
