package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.EndgameEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.*;

/**
 * Rapresents the game after the final round has been played.
 */
public class EndGameState extends GameState {

    private final List<Player> players;

    /**
     * Stores how many objectives every player has completed.
     */
    private final Map<Player, Integer> playersToAchievedObjectives;

    /**
     * Constructs the EndGameState.
     * @param game istance of the {@link Game} class.
     */
    protected EndGameState(Game game) {
        super(GameStateType.END_GAME, game, Set.of());
        this.players = game.getPlayers();
        this.notYourTurnMessage =
                "The game is over. If you want to keep playing you must create a new game.";
        this.playersToAchievedObjectives = new HashMap<>();
    }

    /**
     * Calculates how many objectives every player has achived.
     */
    @Override
    public void setUp() {
        for (Player player : this.players) {
            int achievedObjectives = player.calculateFinalPoints(Arrays.asList(this.game.getCommonObjectives()));
            playersToAchievedObjectives.put(player, achievedObjectives);
        }
        this.game.triggerEvent(new EndgameEvent(playersToAchievedObjectives));
    }

    @Override
    public void execute(GameAction action) {}

    @Override
    protected boolean isYourTurn(GameAction action) {
        return false;
    }

    @Override
    public void disconnectPlayer(String username) {

    }
}
