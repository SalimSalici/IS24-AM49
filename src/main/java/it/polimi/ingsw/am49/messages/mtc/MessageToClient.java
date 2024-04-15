package it.polimi.ingsw.am49.messages.mtc;

import it.polimi.ingsw.am49.messages.mts.MessageToServerType;

public abstract class MessageToClient {
    protected final MessageToClientType type;
    protected final String message;

    public MessageToClient(MessageToClientType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageToClientType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }
}