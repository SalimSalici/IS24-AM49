package it.polimi.ingsw.am49.messages.mts;

import java.io.Serializable;

public interface MessageToServerNew extends Serializable {
    public MessageToServerType getType();
}
