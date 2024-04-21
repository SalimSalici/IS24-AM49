package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;

import java.util.List;

public record HandUpdate(String username, List<Integer> handIds) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.HAND_UPDATE;
    }
}
