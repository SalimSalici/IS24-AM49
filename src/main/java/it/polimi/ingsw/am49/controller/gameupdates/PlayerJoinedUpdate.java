package it.polimi.ingsw.am49.controller.gameupdates;

public record PlayerJoinedUpdate(String username) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PLAYER_JOINED_UPDATE;
    }
}
