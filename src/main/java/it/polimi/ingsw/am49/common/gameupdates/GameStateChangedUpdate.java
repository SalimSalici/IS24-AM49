package it.polimi.ingsw.am49.common.gameupdates;

import it.polimi.ingsw.am49.common.enumerations.GameStateType;

/**
 * Represents an update when the game state changes.
 * This is a record class that holds the new game state type, the current player, the turn number,
 * the round number, and flags indicating whether it is the end game or final round.
 *
 * @param gameStateType the new type of the game state
 * @param currentPlayer the username of the current player
 * @param turn the current turn number
 * @param round the current round number
 * @param endGame a flag indicating whether it is the end game
 * @param finalRound a flag indicating whether it is the final round
 */
public record GameStateChangedUpdate(
        GameStateType gameStateType,
        String currentPlayer,
        int turn,
        int round,
        boolean endGame,
        boolean finalRound
) implements GameUpdate {
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to game state changes.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.GAME_STATE_UPDATE;
    }
}
