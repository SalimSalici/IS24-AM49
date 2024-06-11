package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.GameStateType;

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
