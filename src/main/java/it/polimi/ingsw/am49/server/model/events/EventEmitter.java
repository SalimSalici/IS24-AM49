package it.polimi.ingsw.am49.server.model.events;

import it.polimi.ingsw.am49.common.enumerations.GameEventType;

/**
 * This class is used to implemet the observer design pattern, it offfers all the mothods needed.
 */
public interface EventEmitter {

    /**
     * Allows to subscribe to an event.
     * @param gameEventType the type of evet that you whant to subscribe to.
     * @param eventListener the object that whant's to subscribe to the event.
     */
    public void addEventListener(GameEventType gameEventType, EventListener eventListener);

    /**
     * Used to unsubscribe from an event.
     * @param gameEventType the type of event that you whant to unsubscribe from.
     * @param eventListener the object that whant's to unsubscribe from the event.
     */
    public void removeEventListener(GameEventType gameEventType, EventListener eventListener);

    /**
     * notifies if an event of a given type occurred.
     * @param gameEvent rapresent the type of event that needs to be notified.
     */
    public void triggerEvent(GameEvent gameEvent);
}
