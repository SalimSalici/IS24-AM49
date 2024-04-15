package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.GameStateChangedEvent;

import java.util.HashSet;
import java.util.Set;

public abstract class GameState {

    protected final GameStateType type;
    protected final Game game;
    protected final Set<GameActionType> acceptableActionTypes;
    protected String notYourTurnMessage;

    protected GameState(GameStateType type, Game game, Set<GameActionType> acceptableActionTypes) {
        this.type = type;
        this.game = game;
        this.acceptableActionTypes = new HashSet<>(acceptableActionTypes);
        this.notYourTurnMessage = "You must wait for your turn";
    }

    public void setUp() {
        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound(), this.game.getCurrentPlayer()));
    }

    public abstract void execute(GameAction action) throws Exception;

    public void goToNextState(GameState nextState) throws Exception {
        this.game.setGameState(nextState);
        try {
            nextState.setUp();
        } catch (NullPointerException ex) {
            throw new Exception("Server error. nextState was not set");
        }
    }

    protected void checkActionValidity(GameAction action) throws Exception {
        if (action.getType() == GameActionType.JOIN_GAME && this.type != GameStateType.PREGAME)
            throw new Exception("The game you are trying to join has already started");
        if (!this.acceptableActionTypes.contains(action.getType()))
            throw new Exception("You cannot do that now");
        if (!this.isYourTurn(action))
            throw new Exception(this.notYourTurnMessage);
    }

    protected abstract boolean isYourTurn(GameAction action);
}
