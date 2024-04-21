package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.List;
import java.util.Map;

public record EndgameEvent(Map<Player, Integer> playersToAchievedObjectives) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.END_GAME;
    }

    @Override
    public GameUpdate toGameUpdate() {
        return null;
    }
}
