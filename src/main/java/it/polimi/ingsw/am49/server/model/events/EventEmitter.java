package it.polimi.ingsw.am49.server.model.events;

import it.polimi.ingsw.am49.common.enumerations.GameEventType;

/**
 * This class is used to implement the observer design pattern, it offers all the methods needed.
 */
public interface EventEmitter {

    /**
     * Allows to subscribe to an event.
     * @param gameEventType the type of event that you want to subscribe to.
     * @param eventListener the object that wants to subscribe to the event.
     */
    public void addEventListener(GameEventType gameEventType, EventListener eventListener);

    /**
     * Used to unsubscribe from an event.
     * @param gameEventType the type of event that you want to unsubscribe from.
     * @param eventListener the object that wants to unsubscribe from the event.
     */
    public void removeEventListener(GameEventType gameEventType, EventListener eventListener);

    /**
     * notifies if an event of a given type occurred.
     * @param gameEvent represent the type of event that needs to be notified.
     */
    public void triggerEvent(GameEvent gameEvent);
}
