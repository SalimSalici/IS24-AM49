package it.polimi.ingsw.am49.common.gameupdates;

import java.util.List;

/**
 * Represents an update for the order of players in the game.
 * This record holds the current order of players as a list of player identifiers.
 *
 * @param playerOrder the current order of players, represented as a list of player identifiers
 */
public record PlayerOrderUpdate(List<String> playerOrder) implements GameUpdate {
    /**
     * Returns the type of game update.
     * @return the game update type specific to player order updates.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PLAYER_ORDER_UPDATE;
    }
}
