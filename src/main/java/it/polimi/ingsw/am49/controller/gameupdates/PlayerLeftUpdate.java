package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.List;

public record PlayerLeftUpdate(String username, List<String> remainingPlayers) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PLAYER_LEFT_UPDATE;
    }
}
