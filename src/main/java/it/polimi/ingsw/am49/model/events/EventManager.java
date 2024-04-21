package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Is the event manager class for the observer design pattern.
 *
 * @see EventEmitter
 */
public class EventManager implements EventEmitter {

    /**
     * Keeps track of all the listeners for every GameEventType.
     */
    private final Map<GameEventType, List<EventListener>> listeners;

    /**
     * Constructs a new EventManager, initializing a list for each type of {@link GameEventType}.
     */
    public EventManager() {
        this.listeners = new HashMap<>();
        for (GameEventType gameEventType : GameEventType.values())
            this.listeners.put(gameEventType, new LinkedList<>());
    }

    /**
     * Registers a listener for a specific type of game event.
     *
     * @param gameEventType the type of event to listen for
     * @param eventListener the listener that will respond to the event
     */
    public void addEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.listeners.get(gameEventType).add(eventListener);
    }

    /**
     * Registers a listener for multiple types of game events.
     *
     * @param gameEventTypes a list of event types to listen for
     * @param eventListener the listener that will respond to the events
     */
    public void addEventListener(List<GameEventType> gameEventTypes, EventListener eventListener) {
        for (GameEventType type : gameEventTypes)
            this.addEventListener(type, eventListener);
    }

    /**
     * Deregisters a listener from a specific type of game event.
     *
     * @param gameEventType the type of event to stop listening for
     * @param eventListener the listener to remove
     */
    public void removeEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.listeners.get(gameEventType).remove(eventListener);
    }

    /**
     * Triggers an event, notifying all registered listeners of the specified event type.
     *
     * @param gameEvent the event to be triggered
     */
    public void triggerEvent(GameEvent gameEvent) {
        this.listeners.get(gameEvent.getType()).forEach(listener -> {
            try {
                listener.onEventTrigger(gameEvent);
            } catch (Exception e) {
                // TODO: Log the exception or handle it as needed
                System.err.println("Error processing event: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
