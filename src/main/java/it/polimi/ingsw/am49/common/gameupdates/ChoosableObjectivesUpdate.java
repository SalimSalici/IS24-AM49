package it.polimi.ingsw.am49.common.gameupdates;

import java.util.List;


/**
 * Represents an update when there are new choosable objectives for a player.
 * This is a record class that holds the username of the player and a list of objective card IDs.
 */
public record ChoosableObjectivesUpdate(String username, List<Integer> objectiveCards) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE;
    }
}
