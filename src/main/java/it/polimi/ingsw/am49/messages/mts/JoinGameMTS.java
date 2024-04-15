package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.controller.Client;

public class JoinGameMTS extends MessageToServer {
    private final Client client;
    public JoinGameMTS(Client client) {
        super(MessageToServerType.JOIN_GAME, client.getUserName());
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
