package it.polimi.ingsw.am49.model.cards.objectives;
import it.polimi.ingsw.am49.model.players.PlayerBoard;
public class PatternObjectiveStrategy implements ObjectivePointsStrategy {

    Pattern pattern;

    public PatternObjectiveStrategy(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public int execute(PlayerBoard playerBoard) {
        return 0;
    }
}
