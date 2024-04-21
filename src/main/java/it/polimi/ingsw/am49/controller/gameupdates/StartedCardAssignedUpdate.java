package it.polimi.ingsw.am49.controller.gameupdates;

public record StartedCardAssignedUpdate(String username, int starterCardId) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.STARTER_CARD_ASSIGNED_UPDATE;
    }
}
