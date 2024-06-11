package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameStateChangedUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.players.Player;

/**
 * Represents an event that signifies a change in the game state.
 *
 * @param gameStateType the current state of the game as defined by {@link GameStateType}
 * @param turn the current turn number within the game
 * @param round the current round number within the game
 * @param currentPlayer the {@link Player} who is currently active
 */
public record GameStateChangedEvent(
        GameStateType gameStateType,
        Player currentPlayer,
        int turn,
        int round,
        boolean endGame,
        boolean finalRound
) implements GameEvent {

    @Override
    public GameEventType getType() {
        return GameEventType.GAME_STATE_CHANGED_EVENT;
    }

    @Override
    public GameStateChangedUpdate toGameUpdate() {
        return new GameStateChangedUpdate(gameStateType, currentPlayer().getUsername(), turn, round, endGame, finalRound);
    }
}
