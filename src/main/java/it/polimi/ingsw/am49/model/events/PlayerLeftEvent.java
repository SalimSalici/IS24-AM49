package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

public record PlayerLeftEvent(List<Player> players) implements GameEvent {

    public PlayerLeftEvent(List<Player> players) {
        this.players = new LinkedList<>(players);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.PLAYER_LEFT_EVENT;
    }
}
