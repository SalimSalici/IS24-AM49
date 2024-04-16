package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

/**
 * Implements the event that notifies when a card gets drawn.
 * @param player the player that drew the card
 * @param card the drawn card
 */
public record CardDrawnEvent(Player player, PlaceableCard card) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.CARD_DRAWN_EVENT;
    }
}