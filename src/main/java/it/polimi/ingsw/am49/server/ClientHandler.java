package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;

public class ClientHandler {
    private final Client client;
    private final String username;

    public ClientHandler(Client client, String username) {
        this.client = client;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
