package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

public record CardDrawnEvent(Player player, DrawPosition drawPosition, int idOfDrawnCard) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.CARD_DRAWN_EVENT;
    }
}
