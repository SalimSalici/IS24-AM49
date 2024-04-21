package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.GameStateType;

public record GameStateChangedUpdate(GameStateType gameStateType, int turn, int round, String currentPlayer)
        implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.GAME_STATE_UPDATE;
    }
}
