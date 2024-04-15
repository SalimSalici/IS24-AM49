package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.Client;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

public record ClientJoinedEvent(Client client) implements GameEvent {

    public ClientJoinedEvent(Client client) {
        this.client = client;
    }

    @Override
    public GameEventType getType() {
        return GameEventType.PLAYER_JOINED_EVENT;
    }
}
