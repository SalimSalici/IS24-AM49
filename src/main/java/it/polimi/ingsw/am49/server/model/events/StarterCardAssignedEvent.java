package it.polimi.ingsw.am49.server.model.events;

import it.polimi.ingsw.am49.common.gameupdates.StartedCardAssignedUpdate;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.players.Player;

/**
 * Represents an event that occurs when starter cards are assigned to players at the beginning of the game.
 *
 * @param player the player to which was assigned the {@link StarterCard}
 * @param starterCard the assigned starter card
 */
public record StarterCardAssignedEvent(Player player, StarterCard starterCard) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.STARTER_CARD_ASSIGNED_EVENT;
    }

    @Override
    public StartedCardAssignedUpdate toGameUpdate() {
        return new StartedCardAssignedUpdate(this.player().getUsername(), this.starterCard.getId());
    }
}


