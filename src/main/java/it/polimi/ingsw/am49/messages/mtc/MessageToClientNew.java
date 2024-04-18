package it.polimi.ingsw.am49.messages.mtc;

import java.io.Serializable;

public interface MessageToClientNew extends Serializable {
    public MessageToClientType getType();
}
