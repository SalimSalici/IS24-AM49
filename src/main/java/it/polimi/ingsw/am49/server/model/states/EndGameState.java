package it.polimi.ingsw.am49.server.model.states;

import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.events.EndgameEvent;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;

import java.util.*;

/**
 * Rapresents the game after the final round has been played.
 */
public class EndGameState extends GameState {

    private final List<Player> players;

    private final Player forfeitWinner;

    /**
     * Stores how many objectives every player has completed.
     */
    private final Map<Player, Integer> playersToAchievedObjectives;

    /**
     * Constructs the EndGameState.
     * @param game istance of the {@link Game} class.
     */
    public EndGameState(Game game, Player forfeitWinner) {
        super(GameStateType.END_GAME, game, Set.of());
        this.players = game.getPlayers();
        this.notYourTurnMessage =
                "The game is over. If you want to keep playing you must create a new game.";
        this.playersToAchievedObjectives = new HashMap<>();
        this.forfeitWinner = forfeitWinner;
    }

    /**
     * Constructs the EndGameState.
     * @param game istance of the {@link Game} class.
     */
    public EndGameState(Game game) {
        this(game, null);
    }

    /**
     * Calculates objective points and how many objectives every player has achived.
     */
    @Override
    public void setUp() {
        for (Player player : this.players) {
            int achievedObjectives = player.calculateFinalPoints(Arrays.asList(this.game.getCommonObjectives()));
            playersToAchievedObjectives.put(player, achievedObjectives);
        }
        this.game.triggerEvent(new EndgameEvent(playersToAchievedObjectives, forfeitWinner));
    }

    /**
     * Executes the given game action.
     * 
     * @param action the action to be executed.
     */
    @Override
    public void execute(GameAction action) throws InvalidActionException, NotYourTurnException {
        this.checkActionValidity(action);
    }

    /**
     * @param action the action to be checked.
     * @return always returns false as the game is over.
     */
    @Override
    protected boolean isYourTurn(GameAction action) {
        return false;
    }

    /**
     * @param username the username of the player to be disconnected.
     */
    @Override
    public void disconnectPlayer(String username) {}
}
