package it.polimi.ingsw.am49.messages.mts;

public abstract class MessageToServer {

    protected final MessageToServerType type;
    protected final String username;

    public MessageToServer(MessageToServerType type, String username) {
        this.type = type;
        this.username = username;
    }

    public MessageToServerType getType() {
        return this.type;
    }

    public String getUsername() {
        return this.username;
    }
}
