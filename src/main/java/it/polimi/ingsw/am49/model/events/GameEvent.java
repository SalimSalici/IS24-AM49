package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;

/**
 * Is the interface for game events.
 */
public interface GameEvent {
    public GameEventType getType();
}
