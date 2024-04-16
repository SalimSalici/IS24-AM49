package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandUpdateMTC extends MessageToClient{
    private final Set<Integer> cardsIds;
    private final String userName;

    public HandUpdateMTC(String userName, Set<Integer> cardsIds){
        super(MessageToClientType.HAND_UPDATE, " has the following cards in his hand: ");
        this.userName = userName;
        this.cardsIds = new HashSet<>(cardsIds);
    }

    @Override
    public String getMessage() {
        return this.userName + super.getMessage() + this.cardsIds.toString();
    }
}

