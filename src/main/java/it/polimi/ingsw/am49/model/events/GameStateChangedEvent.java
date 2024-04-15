package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

public record GameStateChangedEvent(GameStateType gameStateType, int turn, int round, Player currentPlayer) implements GameEvent {

    @Override
    public GameEventType getType() {
        return GameEventType.GAME_STATE_CHANGED_EVENT;
    }
}
