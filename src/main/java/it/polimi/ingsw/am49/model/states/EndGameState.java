package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.EndgameEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rapresents the game after the final round has been played.
 */
public class EndGameState extends GameState {

    List<Player> players;

    /**
     * Stores how many objectives every player has completed.
     */
    Map<Player, Integer> playersToAchievedObjevtives;

    /**
     * Constructs the EndGameState.
     * @param game istance of the {@link Game} class.
     */
    protected EndGameState(Game game) {
        super(GameStateType.END_GAME, game, Set.of());
        this.players = game.getPlayers();
        this.notYourTurnMessage =
                "The game is over. If you want to keep playing you must create a new game.";
    }

    /**
     * Calculates how many objectives every player has achived.
     */
    @Override
    public void setUp() {
        for (Player player : this.players) {
            int achievedObjectives = player.calculateFinalPoints(Arrays.asList(this.game.getCommonObjectives()));
            playersToAchievedObjevtives.put(player, achievedObjectives);
        }
        this.game.triggerEvent(new EndgameEvent(playersToAchievedObjevtives));
    }

    @Override
    public void execute(GameAction action) {}

    @Override
    protected boolean isYourTurn(GameAction action) {
        return false;
    }
}
