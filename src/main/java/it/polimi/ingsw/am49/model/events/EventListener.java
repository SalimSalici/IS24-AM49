package it.polimi.ingsw.am49.model.events;

/**
 * Is used to implement the EventListeners in the observer design pattern.
 */
public interface EventListener {

    /**
     * Lissens for the specific event.
     * @param event is the type of event to listen for.
     */
    public void onEventTrigger(GameEvent event);
}
