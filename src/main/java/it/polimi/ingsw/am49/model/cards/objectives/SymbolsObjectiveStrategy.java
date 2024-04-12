package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedCollection;

/**
 * Is the strategy of {@link ObjectivePointsStrategy} used to rapresent the
 * {@link ObjectiveCard}s that give to the player the amount of points
 * shown on the card multiplied by how many times a specific set of symbols appear
 * in the playing board.
 */
public class SymbolsObjectiveStrategy implements ObjectivePointsStrategy, Serializable {

    /**
     * Is the specific set of symbols that determines the amount of points
     * the player is getting.
     */
    private final Map<Symbol, Integer> requirements;

    /**
     * Costructs a new {@link SymbolsObjectiveStrategy} object with the
     * specified requirements
     *
     * @param requirements is the set of symbols that determines how many
     *                     points the player is getting
     */
    public SymbolsObjectiveStrategy(Map<Symbol, Integer> requirements) {
        this.requirements = new HashMap<>(requirements);
    }

    @Override
    public int execute(PlayerBoard playerBoard) {
        ArrayList<Integer> iterationsList = new ArrayList<Integer>();

        requirements.forEach((k, v) -> {
            int itemCount = 0;

            for(BoardTile tile : playerBoard.getPlacementOrder()){
                itemCount += tile.getActiveSymbols().get(k);
            }

            iterationsList.add(itemCount / v); // division of int should already return the floored number so no need to add Math.floor()
            }
        );

        return iterationsList.stream().min(Integer::compareTo).get();
    }
}
