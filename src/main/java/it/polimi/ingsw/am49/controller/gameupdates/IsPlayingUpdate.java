package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.List;

public record IsPlayingUpdate(String username, Boolean status) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.IS_PLAYING_UPDATE;
    }
}
