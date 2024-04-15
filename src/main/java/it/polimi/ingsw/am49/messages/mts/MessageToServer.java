package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.Client;

public abstract class MessageToServer {

    protected final MessageToServerType type;
    protected final Client client;

    public MessageToServer(MessageToServerType type, Client client) {
        this.type = type;
        this.client = client;
    }

    public MessageToServerType getType() {
        return this.type;
    }

    public Client getClient() {
        return this.client;
    }
}
