package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Resource;

import java.util.List;

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