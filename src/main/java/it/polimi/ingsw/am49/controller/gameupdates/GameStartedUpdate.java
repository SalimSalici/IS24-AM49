package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Color;

import java.util.LinkedHashMap;

/**
 * Provides information about the game that just started, including starter card for the player receiving this update
 * and player-color association. Additionally, it also provides information about the ordering of the players
 * for the game ({@link LinkedHashMap} preserves insertion ordering).
 * @param username username of the player receiving the starter card.
 * @param starterCardId starter card received by the player with relative username.
 * @param playersToColors provides info about player ordering and player-color association.
 */
public record GameStartedUpdate(String username, int starterCardId, LinkedHashMap<String, Color> playersToColors) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.GAME_STARTED_UPDATE;
    }
}
