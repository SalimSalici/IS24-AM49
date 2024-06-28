package it.polimi.ingsw.am49.common.gameupdates;

import java.util.Map;

/**
 * Represents an update at the end of the game.
 * This record holds the mapping from player usernames to their end game information, including points,
 * completed objective cards, and personal objective ID, as well as the username of the forfeit winner if applicable.
 *
 * @param playerToPoints mapping from player username to their information about the end game:
 *                       the first element of the Integer[] array represents the points of the player at the end of the game,
 *                       the second element represents the amount of completed objective cards (for playoffs),
 *                       and the third element represents the id of the personal objective of the player.
 * @param forfeitWinner the username of the player who won by forfeit, if applicable.
 */
public record EndGameUpdate(Map<String, Integer[]> playerToPoints, String forfeitWinner) implements GameUpdate {
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to end game updates.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.END_GAME_UPDATE;
    }
}
