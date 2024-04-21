package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.HandUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.HiddenHandUpdate;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.List;

/**
 * Represents an event that notifies about updates to a player's hand of cards.
 *
 * @param player the {@link Player} whose hand is being updated
 * @param hand a list of {@link PlaceableCard} objects representing the current player's hand after the update
 */
public record HandEvent(Player player, List<PlaceableCard> hand) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.HAND_UPDATE_EVENT;
    }

    @Override
    public HandUpdate toGameUpdate() {
        return new HandUpdate(player.getUsername(), hand.stream().map(Card::getId).toList());
    }

    public HiddenHandUpdate toHiddenHandUpdate() {
        return new HiddenHandUpdate(player.getUsername(), hand.stream().map(PlaceableCard::getResource).toList());
    }
}
