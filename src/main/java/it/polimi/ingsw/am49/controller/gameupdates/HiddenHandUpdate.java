package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.util.Pair;

import java.util.List;

/**
 * Represents an update for a game where a player's hand is partially or fully hidden.
 * This record is used to send updates about the hidden state of each resource in a player's hand.
 *
 * @param username the username of the player whose hand update is being represented
 * @param hiddenHand a list of pairs, each containing a {@link Resource} and a Boolean indicating if that resource is hidden (true) or not (false)
 */
public record HiddenHandUpdate(String username, List<Pair<Resource, Boolean>> hiddenHand) implements GameUpdate {
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to hidden hand updates
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.HIDDEN_HAND_UPDATE;
    }
}
