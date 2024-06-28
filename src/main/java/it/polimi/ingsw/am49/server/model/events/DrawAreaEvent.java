package it.polimi.ingsw.am49.server.model.events;

import it.polimi.ingsw.am49.common.gameupdates.DrawAreaUpdate;
import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.common.enumerations.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This event notifies changes the changes in the drawable area. More specifically, how many cards remain
 * in the two drawable decks (resource deck and gold deck) and what are the revealed cards that can be drawn
 * @param remainingResources how many cards are left in the resource deck
 * @param remainingGolds how many cards are left in the gold deck
 * @param deckTopResource the resource of the top card of the resource's deck
 * @param deckTopGold the resource of the top card of the gold's deck
 * @param revealedResources the list of revealed resource cards that can be drawn
 * @param revealedGolds the list of revealed gold cards that can be drawn
 */
public record DrawAreaEvent(
        int remainingResources,
        int remainingGolds,
        Resource deckTopResource,
        Resource deckTopGold,
        List<ResourceCard> revealedResources,
        List<GoldCard> revealedGolds
) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.DRAW_AREA_EVENT;
    }

    @Override
    public GameUpdate toGameUpdate() {
        return new DrawAreaUpdate(
                remainingResources,
                remainingGolds,
                deckTopResource,
                deckTopGold,
                revealedResources.stream()
                        .map(card -> card != null ? card.getId() : null)
                        .collect(Collectors.toCollection(LinkedList::new)),
                revealedGolds.stream()
                        .map(card -> card != null ? card.getId() : null)
                        .collect(Collectors.toCollection(LinkedList::new))
        );
    }
}