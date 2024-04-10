package it.polimi.am49.cn_demo.model.cards.objectives;
import it.polimi.am49.cn_demo.model.players.PlayerBoard;
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
