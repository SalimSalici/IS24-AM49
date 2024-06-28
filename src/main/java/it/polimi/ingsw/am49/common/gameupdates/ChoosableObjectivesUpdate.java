package it.polimi.ingsw.am49.common.gameupdates;

import java.util.List;

/**
 * Represents an update when there are new choosable objectives for a player.
 * This is a record class that holds the username of the player and a list of objective card IDs.
 *
 * @param username the username of the player who has new choosable objectives.
 * @param objectiveCards a list of IDs for the objective cards that are choosable by the player.
 */
public record ChoosableObjectivesUpdate(String username, List<Integer> objectiveCards) implements GameUpdate {
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to choosable objectives updates.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.CHOOSABLE_OBJETIVES_UPDATE;
    }
}
