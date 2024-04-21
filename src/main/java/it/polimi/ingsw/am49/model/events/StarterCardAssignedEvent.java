package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an event that occurs when starter cards are assigned to players at the beginning of the game.
 *
 * @param playersToStarterCard a map linking each player to their assigned {@link StarterCard}
 */
public record StarterCardAssignedEvent(
        Map<Player, StarterCard> playersToStarterCard
) implements GameEvent {

    public StarterCardAssignedEvent(Map<Player, StarterCard> playersToStarterCard) {
        this.playersToStarterCard = new HashMap<>(playersToStarterCard);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.STARTER_CARD_ASSIGNED_EVENT;
    }

    @Override
    public GameUpdate toGameUpdate() {
        return null;
    }
}


