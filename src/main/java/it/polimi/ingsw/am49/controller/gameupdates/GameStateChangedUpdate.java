package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.GameStateType;

/**
 * Represents an update when the game state changes.
 * This is a record class that holds the new game state type, the current player, the turn number,
 * the round number, and flags indicating whether it is the end game or final round.
 */
public record GameStateChangedUpdate(
        GameStateType gameStateType,
        String currentPlayer,
        int turn,
        int round,
        boolean endGame,
        boolean finalRound
)
        implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.GAME_STATE_UPDATE;
    }
}
