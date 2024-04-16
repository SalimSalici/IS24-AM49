package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

public record CardDrawnEvent(Player player, PlaceableCard card) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.CARD_DRAWN_EVENT;
    }
}
