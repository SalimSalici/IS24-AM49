package it.polimi.ingsw.am49.server.model.events;

import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;

/**
 * Is the interface for game events.
 */
public interface GameEvent {
    GameEventType getType();
    GameUpdate toGameUpdate();
}
