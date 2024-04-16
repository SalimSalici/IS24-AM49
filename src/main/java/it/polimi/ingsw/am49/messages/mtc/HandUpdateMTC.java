package it.polimi.ingsw.am49.messages.mtc;

import java.util.HashSet;
import java.util.Set;

public class HandUpdateMTC extends MessageToClient {
    private final Set<Integer> cardsIds;
    private final String username;

    public HandUpdateMTC(String username, Set<Integer> cardsIds){
        super(MessageToClientType.HAND_UPDATE, " has the following cards in his hand: ");
        this.username = username;
        this.cardsIds = new HashSet<>(cardsIds);
    }

    @Override
    public String getMessage() {
        return this.username + super.getMessage() + this.cardsIds.toString();
    }

    public Set<Integer> getCardsIds() {
        return cardsIds;
    }

    public String getUsername() {
        return username;
    }
}