package it.polimi.ingsw.am49.controller.gameupdates;

public record PlayerLeftUpdate(String username) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PLAYER_LEFT_UPDATE;
    }
}
