package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;

public interface EventEmitter {
    public void addEventListener(GameEventType gameEventType, EventListener eventListener);
    public void removeEventListener(GameEventType gameEventType, EventListener eventListener);
    public void triggerEvent(GameEventType gameEventType, GameEvent gameEvent);
}
