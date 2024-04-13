package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.List;

public record HandUpdateEvent(Player player, List<PlaceableCard> hand) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.HAND_UPDATE_EVENT;
    }
}
