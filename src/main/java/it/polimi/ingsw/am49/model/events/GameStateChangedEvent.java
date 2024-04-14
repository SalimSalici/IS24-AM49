package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;

public record GameStateChangedEvent(GameStateType gameStateType, int turn, int round) implements GameEvent {

    @Override
    public GameEventType getType() {
        return GameEventType.GAME_STATE_CHANGED_EVENT;
    }
}
