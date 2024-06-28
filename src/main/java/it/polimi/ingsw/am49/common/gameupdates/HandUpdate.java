package it.polimi.ingsw.am49.common.gameupdates;

import java.util.List;

/**
 * Represents an update for a player's hand in the game.
 * This record holds the username of the player and a list of card IDs that are currently in the player's hand.
 *
 * @param username the username of the player whose hand is being updated
 * @param handIds a list of card IDs that are currently in the player's hand
 */
public record HandUpdate(String username, List<Integer> handIds) implements GameUpdate {
    /**
     * Returns the type of game update.
     * @return the game update type specific to updating a player's hand.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.HAND_UPDATE;
    }
}
