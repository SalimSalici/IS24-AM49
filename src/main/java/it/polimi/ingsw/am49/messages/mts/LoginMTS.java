package it.polimi.ingsw.am49.messages.mts;

public record LoginMTS(String username) implements MessageToServerNew {
    @Override
    public MessageToServerType getType() {
        return MessageToServerType.LOGIN;
    }
}
