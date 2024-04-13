package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

public record CardPlacedEvent(Player player, BoardTile boardTile) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.CARD_PLACED_EVENT;
    }
}
