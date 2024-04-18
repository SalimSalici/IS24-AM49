package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.ClientOld;

public abstract class MessageToServer {

    protected final MessageToServerType type;
    protected final ClientOld client;

    public MessageToServer(MessageToServerType type, ClientOld client) {
        this.type = type;
        this.client = client;
    }

    public MessageToServerType getType() {
        return this.type;
    }

    public ClientOld getClient() {
        return this.client;
    }
}
