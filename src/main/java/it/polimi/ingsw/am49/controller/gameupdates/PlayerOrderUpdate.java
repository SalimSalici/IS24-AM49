package it.polimi.ingsw.am49.controller.gameupdates;

import java.util.List;

public record PlayerOrderUpdate(List<String> playerOrder) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PLAYER_ORDER_UPDATE;
    }
}
