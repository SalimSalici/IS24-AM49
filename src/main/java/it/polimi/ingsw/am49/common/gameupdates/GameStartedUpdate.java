package it.polimi.ingsw.am49.common.gameupdates;

import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.enumerations.Resource;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Provides information about the game that just started, including starter card for the player receiving this update
 * and player-color association. Additionally, it also provides information about the ordering of the players
 * for the game ({@link LinkedHashMap} preserves insertion ordering).
 *
 * @param username username of the player receiving the starter card.
 * @param starterCardId starter card received by the player with relative username.
 * @param playersToColors provides info about player ordering and player-color association.
 * @param commonObjectivesIds a list of IDs for the common objectives in the game.
 * @param remainingResources the number of remaining resources.
 * @param remainingGolds the number of remaining golds.
 * @param deckTopResource the top resource card on the deck.
 * @param deckTopGold the top gold card on the deck.
 * @param revealedResourcesIds a list of IDs for the revealed resource cards.
 * @param revealedGoldsIds a list of IDs for the revealed gold cards.
 */
public record GameStartedUpdate(
        String username,
        int starterCardId,
        LinkedHashMap<String, Color> playersToColors,
        List<Integer> commonObjectivesIds,
        int remainingResources,
        int remainingGolds,
        Resource deckTopResource,
        Resource deckTopGold,
        List<Integer> revealedResourcesIds,
        List<Integer> revealedGoldsIds
) implements GameUpdate {
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to game start updates.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.GAME_STARTED_UPDATE;
    }
}
