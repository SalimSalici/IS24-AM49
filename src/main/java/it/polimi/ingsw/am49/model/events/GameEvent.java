package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;

public interface GameEvent {
    public GameEventType getType();
}
