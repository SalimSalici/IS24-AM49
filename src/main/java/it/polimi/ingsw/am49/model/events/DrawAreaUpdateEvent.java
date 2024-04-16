package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.List;

/**
 * This event notifies changes the changes in the drawable area. More specifically, how many cards remain
 * in the two drawable decks (resource deck and gold deck) and what are the revealed cards that can be drawn
 * @param remainingResources how many cards are left in the resource deck
 * @param remainingGolds how many cards are left in the gold deck
 * @param revealedResources the list of reveald resource cards that can be drawn
 * @param revealedGolds the list of reveald gold cards that can be drawn
 */
public record DrawAreaUpdateEvent(
        int remainingResources,
        int remainingGolds,
        List<ResourceCard> revealedResources,
        List<GoldCard> revealedGolds
) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.DRAW_AREA_UPDATE;
    }
}