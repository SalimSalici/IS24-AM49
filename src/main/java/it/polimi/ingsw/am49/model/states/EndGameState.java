package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.EndgameEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndGameState extends GameState {

    List<Player> players;
    Map<Player, Integer> playersToAchievedObjevtives;

    protected EndGameState(Game game) {
        super(GameStateType.END_GAME, game, Set.of());
        this.players = game.getPlayers();
        this.notYourTurnMessage =
                "The game is over. If you want to keep playing you must create a new game.";
    }

    @Override
    public void setUp() {
        for (Player player : this.players) {
            int achievedObjectives = player.calculateFinalPoints(Arrays.asList(this.game.getCommonObjectives()));
            playersToAchievedObjevtives.put(player, achievedObjectives);
        }
        this.game.triggerEvent(new EndgameEvent(playersToAchievedObjevtives));
    }

    @Override
    public void execute(GameAction action) throws Exception {}

    @Override
    protected boolean isYourTurn(GameAction action) {
        return false;
    }
}
