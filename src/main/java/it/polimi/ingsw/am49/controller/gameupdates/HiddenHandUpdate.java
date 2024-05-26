package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.util.Pair;

import java.util.List;

public record HiddenHandUpdate(String username, List<Pair<Resource, Boolean>> hiddenHand) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.HIDDEN_HAND_UPDATE;
    }
}
