package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandUpdateMTC extends MessageToClient{
    private final Set<Integer> cardsIds;

    public HandUpdateMTC(Set<Integer> cardsIds){
        super(MessageToClientType.HAND_UPDATE, "You have the following cards in your hand: ");
        this.cardsIds = new HashSet<>(cardsIds);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.cardsIds.toString();
    }
}

