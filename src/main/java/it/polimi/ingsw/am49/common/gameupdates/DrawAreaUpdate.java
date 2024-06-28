package it.polimi.ingsw.am49.common.gameupdates;

import it.polimi.ingsw.am49.common.enumerations.Resource;

import java.util.List;

/**
 * Represents an update for the draw area in the game.
 * This is a record class that holds information about the remaining resources, remaining golds,
 * the top resource and gold on the deck, and the revealed resources and golds.
 *
 * @param remainingResources the number of remaining resource cards.
 * @param remainingGolds the number of remaining gold cards.
 * @param deckTopResource the top resource card on the deck.
 * @param deckTopGold the top gold card on the deck.
 * @param revealedResources a list of IDs for the revealed resource cards.
 * @param revealedGolds a list of IDs for the revealed gold cards.
 */
public record DrawAreaUpdate(
        int remainingResources,
        int remainingGolds,
        Resource deckTopResource,
        Resource deckTopGold,
        List<Integer> revealedResources,
        List<Integer> revealedGolds
) implements GameUpdate {
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to draw area updates.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.DRAW_AREA_UPDATE;
    }
}
