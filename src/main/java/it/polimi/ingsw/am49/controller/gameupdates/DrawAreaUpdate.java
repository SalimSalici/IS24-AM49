package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Resource;

import java.util.List;

/**
 * Represents an update for the draw area in the game.
 * This is a record class that holds information about the remaining resources, remaining golds,
 * the top resource and gold on the deck, and the revealed resources and golds.
 */

public record DrawAreaUpdate(
        int remainingResources,
        int remainingGolds,
        Resource deckTopResource,
        Resource deckTopGold,
        List<Integer> revealedResources,
        List<Integer> revealedGolds
) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.DRAW_AREA_UPDATE;
    }
}