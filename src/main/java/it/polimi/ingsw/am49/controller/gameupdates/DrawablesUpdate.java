package it.polimi.ingsw.am49.controller.gameupdates;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Resource;

import java.util.LinkedHashMap;
import java.util.List;

public record DrawablesUpdate (Resource deckTopResource, Resource deckTopGold, List<Integer> revealedResourcesIds, List<Integer> revealedGoldsIds) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.DRAWABLES_UPDATE;
    }
}