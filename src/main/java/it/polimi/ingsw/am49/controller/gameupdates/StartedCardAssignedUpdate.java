package it.polimi.ingsw.am49.controller.gameupdates;

/**
 * Represents an update for when a starter card is assigned to a player.
 * This record is used to notify all clients about the assignment of a starter card to a specific user.
 *
 * @param username the username of the player to whom the starter card is assigned
 * @param starterCardId the ID of the starter card that is assigned
 */
public record StartedCardAssignedUpdate(String username, int starterCardId) implements GameUpdate {
    /**
     * Returns the type of the game update.
     *
     * @return the game update type specific to starter card assignment
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.STARTER_CARD_ASSIGNED_UPDATE;
    }
}
