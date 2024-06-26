package it.polimi.ingsw.am49.server.model.states;

import it.polimi.ingsw.am49.common.actions.GameActionType;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.events.GameStateChangedEvent;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Log;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for implementing the State pattern in the handling of the main phases of the game.
 */
public abstract class GameState implements Serializable {

    /**
     * This attribute indicates witch of the possible {@link GameStateType} is rapresented by the class that will
     * inherit from {@link GameState}
     */
    protected final GameStateType type;

    /**
     * The game instance
     */
    protected final Game game;

    /**
     * Is a set with all the possible action that a player can do, they are listed in {@link GameActionType}.
     */
    protected final Set<GameActionType> acceptableActionTypes;

    /**
     * Stores a message indicating that currently is not your turn, It'll be displayed if a player tries to
     * do some action while it isn't his turn.
     */
    protected String notYourTurnMessage;

    /**
     * Costructor for the {@link GameState} class.
     * @param type rapresents the {@link GameStateType}.
     * @param game object rapresenting the {@link Game}.
     * @param acceptableActionTypes set containing the allowed actions, see {@link GameActionType}.
     */
    protected GameState(GameStateType type, Game game, Set<GameActionType> acceptableActionTypes) {
        this.type = type;
        this.game = game;
        this.acceptableActionTypes = new HashSet<>(acceptableActionTypes);
        this.notYourTurnMessage = "You must wait for your turn";
    }

    /**
     * Is used to handle some tasks needed for the proprer functioning of the state (es. drowing the common objectives).
     */
    public void setUp() {
        this.game.triggerEvent(new GameStateChangedEvent(
                this.type,
                this.game.getCurrentPlayer(),
                this.game.getTurn(),
                this.game.getRound(),
                this.game.isEndGame(),
                this.game.isFinalRound()
        ));
    }

    /**
     * Handles the main task of the State.
     * @param action tells witch type of {@link GameAction} needs to be handled.
     * @throws InvalidActionException if the action is not supported by this state.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    public abstract void execute(GameAction action) throws InvalidActionException, NotYourTurnException;

    /**
     * Handles the process of switching to the state passed as parameter.
     * @param nextState the state to switch to.
     */
    public void goToNextState(GameState nextState) {
        if (nextState == null) {
            Log.getLogger().severe("Tried to switch to null state.");
            return;
        }

        this.game.setGameState(nextState);
        nextState.setUp();
    }

    /**
     * Checks if the action requested by the player if valid in the current game state ad if not notifies the player.
     * @param action that the player tries to perform.
     * @throws InvalidActionException if the action is not supported by this state.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    protected void checkActionValidity(GameAction action) throws InvalidActionException, NotYourTurnException {
        if (this.game.isPaused() && !(this.type == GameStateType.CHOOSE_STARTER_SIDE || this.type == GameStateType.CHOOSE_OBJECTIVE))
            throw new InvalidActionException("You are the only player left. You cannot execute actions until someone else joins.");
        if (!this.acceptableActionTypes.contains(action.getType()))
            throw new InvalidActionException("You cannot do that now.");
        if (!this.isYourTurn(action))
            throw new NotYourTurnException(this.notYourTurnMessage);
    }

    /**
     * Checks if the action is being performed by the current player.
     *
     * @param action the action to be checked.
     * @return true if the action is performed by the current player, false otherwise.
     */
    protected boolean isYourTurn(GameAction action) {
        return this.game.getCurrentPlayer().getUsername().equals(action.getUsername());
    }

    /**
     * Handles the disconnection of a player.
     *
     * @param username the username of the player to be disconnected.
     */
    public abstract void disconnectPlayer(String username);

    /**
     * Gets the type of the current game state.
     *
     * @return the type of the current game state.
     */
    public GameStateType getType() {
        return this.type;
    }
}
