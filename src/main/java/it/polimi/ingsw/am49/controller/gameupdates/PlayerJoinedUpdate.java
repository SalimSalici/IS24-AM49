package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.List;

public record PlayerJoinedUpdate(String playerWhoJoined, List<String> playersInGame) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PLAYER_JOINED_UPDATE;
    }
}
