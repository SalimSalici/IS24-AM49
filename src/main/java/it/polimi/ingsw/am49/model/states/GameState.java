package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.GameStateChangedEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for implementing the State pattern in the handling of the main phases of the game.
 */
public abstract class GameState {

    /**
     * This attribute indicates witch of the possibiel {@link GameStateType} is rapresented by the class that will
     * inherit from {@link GameState}
     */
    protected final GameStateType type;
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
        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound(), this.game.getCurrentPlayer()));
    }

    /**
     * Handles the main task of the State.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws Exception if unexpected behavior is uncoverd during the state handling.
     */
    public abstract void execute(GameAction action) throws Exception;

    /**
     * Handles the process of switching to the state passed as parameter.
     * @param nextState the state to switch to.
     * @throws Exception if the destination state is not found or can not access it.
     */
    public void goToNextState(GameState nextState) throws Exception {
        if (nextState == null)
            throw new Exception("Server error. nextState was not set");

        this.game.setGameState(nextState);
        try {
            nextState.setUp();
        } catch (Exception ex) {
            System.err.println("Server error when going to next state...");
            ex.printStackTrace();
        }
    }

    /**
     * Checks if the action requested by the player if valid in the current game state ad if not notifies the player.
     * @param action that the player tries to perform.
     * @throws Exception indicates why the desired action is not possible.
     */
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
