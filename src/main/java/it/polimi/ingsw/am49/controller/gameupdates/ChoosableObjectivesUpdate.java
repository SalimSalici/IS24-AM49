package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.List;

public record ChoosableObjectivesUpdate(String username, List<Integer> objectiveCards) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE;
    }
}
