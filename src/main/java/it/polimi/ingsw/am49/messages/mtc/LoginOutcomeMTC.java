package it.polimi.ingsw.am49.messages.mtc;

public record LoginOutcomeMTC(boolean outcome) implements MessageToClientNew {
    @Override
    public MessageToClientType getType() {
        return MessageToClientType.LOGIN_OUTCOME;
    }
}
