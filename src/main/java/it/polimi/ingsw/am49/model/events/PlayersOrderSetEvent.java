package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.List;

public record PlayersOrderSetEvent(List<Player> playersOrder) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.PLAYERS_ORDER_SET_EVENT;
    }
}

