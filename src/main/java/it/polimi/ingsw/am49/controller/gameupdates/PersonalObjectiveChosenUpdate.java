package it.polimi.ingsw.am49.controller.gameupdates;

/**
 * Represents an update for when a player chooses their personal objective in the game.
 * This record holds the username of the player and the identifier for the chosen objective.
 *
 * @param username the username of the player
 * @param objective the identifier of the chosen personal objective
 */
public record PersonalObjectiveChosenUpdate(String username, int objective) implements GameUpdate {
    /**
     * Returns the type of game update specific to when a personal objective is chosen.
     *
     * @return the game update type for personal objective chosen updates
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.PERSONAL_OBJECTIVE_CHOSEN_UPDATE;
    }
}