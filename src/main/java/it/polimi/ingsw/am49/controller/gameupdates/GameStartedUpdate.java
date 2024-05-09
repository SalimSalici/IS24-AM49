package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Color;

import java.util.Map;

public record GameStartedUpdate(String username, int starterCardId, Map<String, Color> playersToColors) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.GAME_STARTED_UPDATE;
    }
}
