package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.Map;

/**
 * @param playerToPoints mapping from player username to their information about the end game: the first elements
 *                       of the Integer[] array represents the points of the player at the end of the game,
 *                       while the second element represents the amount of completed objective cards (for playoffs).
 */
public record EndGameUpdate(Map<String, Integer[]> playerToPoints) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.END_GAME_UPDATE;
    }
}