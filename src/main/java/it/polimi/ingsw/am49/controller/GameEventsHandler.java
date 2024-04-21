package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.events.*;

public class GameEventsHandler implements EventListener {
    private final Room room;
    private final Game game;

    public GameEventsHandler(Room room, Game game) {
        this.room = room;
        this.game = game;

        for (GameEventType eventType : GameEventType.values())
            this.game.addEventListener(eventType, this);
    }

    @Override
    public void onEventTrigger(GameEvent event) {
        System.out.println("Event received: " + event);
//        this.room.broadcastGameUpdate(event.toGameUpdate());
        switch (event.getType()) {
            case STARTER_CARD_ASSIGNED_EVENT -> {
                StarterCardAssignedEvent evt = (StarterCardAssignedEvent) event;
                this.room.notifyGameUpdateTo(evt.player().getUsername(), evt.toGameUpdate());
            }
            case CHOOSABLE_OBJECTIVES_EVENT -> {
                ChoosableObjectivesEvent evt = (ChoosableObjectivesEvent) event;
                this.room.notifyGameUpdateTo(evt.player().getUsername(), evt.toGameUpdate());
            }
            case HAND_UPDATE_EVENT -> {
                HandEvent evt = (HandEvent) event;
                this.room.notifyGameUpdateTo(evt.player().getUsername(), evt.toGameUpdate());
                this.room.broadcastGameUpdateExcept(evt.toHiddenHandUpdate(), evt.player().getUsername());
            }
            default -> this.room.broadcastGameUpdate(event.toGameUpdate());
        }
    }
}
