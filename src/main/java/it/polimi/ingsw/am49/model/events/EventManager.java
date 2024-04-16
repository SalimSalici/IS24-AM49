package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Is the event manager class for the observer design pattern.
 * @see EventEmitter
 */
public class EventManager implements EventEmitter {

    /**
     * Keeps track of all the listeners for every GameEventType.
     */
    private final Map<GameEventType, List<EventListener>> listeners;

    /**
     * Constructor for the EventManager class.
     */
    public EventManager() {
        this.listeners = new HashMap<>();
        for (GameEventType gameEventType : GameEventType.values())
            this.listeners.put(gameEventType, new LinkedList<>());
    }

    public void addEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.listeners.get(gameEventType).add(eventListener);
    }

    public void addEventListener(List<GameEventType> gameEventTypes, EventListener eventListener) {
        for (GameEventType type : gameEventTypes)
            this.addEventListener(type, eventListener);
    }

    public void removeEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.listeners.get(gameEventType).remove(eventListener);
    }

    public void triggerEvent(GameEvent gameEvent) {
        this.listeners.get(gameEvent.getType()).forEach(listener -> {
            try {
                listener.onEventTrigger(gameEvent);
            } catch (Exception e) {
                // TODO: Log the exception or handle it as needed
                System.err.println("Error processing event: " + e.getMessage());
            }
        });
    }
}
