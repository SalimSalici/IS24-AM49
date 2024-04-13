package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EventManager implements EventEmitter {
    private final Map<GameEventType, List<EventListener>> listeners;

    public EventManager() {
        this.listeners = new HashMap<>();
        for (GameEventType gameEventType : GameEventType.values())
            this.listeners.put(gameEventType, new LinkedList<>());
    }

    public void addEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.listeners.get(gameEventType).add(eventListener);
    }

    public void removeEventListener(GameEventType gameEventType, EventListener eventListener) {
        this.listeners.get(gameEventType).remove(eventListener);
    }

    public void triggerEvent(GameEventType gameEventType, GameEvent gameEvent) {
        this.listeners.get(gameEventType).forEach(listener -> {
            try {
                listener.onEventTrigger(gameEvent);
            } catch (Exception e) {
                // TODO: Log the exception or handle it as needed
                System.err.println("Error processing event: " + e.getMessage());
            }
        });
    }
}
