package it.polimi.ingsw.am49.model.cards.objectives;
import it.polimi.ingsw.am49.model.cards.placeables.ColouredCard;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.io.Serializable;
import java.util.*;

public class PatternObjectiveStrategy implements ObjectivePointsStrategy, Serializable {

    Pattern pattern;

    public PatternObjectiveStrategy(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Calculates the number of times the objective is achieved
     * @param playerBoard the whole player board
     * @return the number of times the objective is achieved
     */
    @Override
    public int execute(PlayerBoard playerBoard) {
        // TODO: this is the naive approach... can be optimized

        // Extract all pivots for the pattern from the player board
        List<PivotGroup> pivotsGroups = new ArrayList<>();
        for (BoardTile boardTile : playerBoard.getPlacementOrder()) {
            PivotGroup group;
            if ((group = this.extractPivotGroup(boardTile)) != null) // boardTile is a valid pivot
                pivotsGroups.add(group);
        }

        List<List<PivotGroup>> pivotsSubsets = this.generateListSubsets(pivotsGroups);

        int maxValidPivots = 0;
        for (List<PivotGroup> subset : pivotsSubsets)
            maxValidPivots = Math.max(maxValidPivots, this.validPivots(subset));

        return maxValidPivots;
    }

    /**
     * Generates all possible subsets of a list
     * @param originalList the list from which to extract all the possible subsets
     * @return a list of all possible sublists of originalList
     */
    public <T> List<List<T>> generateListSubsets(List<T> originalList) {
        List<T> list = new ArrayList<>(originalList);
        int n = originalList.size();
        int subsetCount = (1 << n); // Equivalent to 2^n
        List<List<T>> subsets = new ArrayList<>();

        for (int i = 0; i < subsetCount; i++) {
            List<T> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) { // Check if the jth bit in i is set
                    subset.add(list.get(j));
                }
            }
            subsets.add(subset);
        }

        return subsets;
    }

    /**
     * Given a bunch of potentially overlapping pivot groups, calculates the number of valid pivots
     * @param pivotGroups the potentially overlapping pivot groups
     * @return the number of non overlapping pivot groups
     */
    private int validPivots(List<PivotGroup> pivotGroups) {
        int result = 0;
        Set<BoardTile> explored = new HashSet<>();

        for (PivotGroup pg : pivotGroups) {
            boolean isValidPivot = true;
            for (BoardTile tile : pg) {
                if (explored.contains(tile)) {
                    isValidPivot = false;
                    break;
                }
            }
            if (isValidPivot) {
                explored.addAll(pg.tiles);
                result++;
            }
        }
        return result;
    }


    /**
     * @param boardTile the tile that would act as pivot
     * @return a PivotGroup containing the pivot tile and all tiles associated to it, or null if boardTile is not a pivot
     */
    private PivotGroup extractPivotGroup(BoardTile boardTile) {
        List<BoardTile> pivotGroup = new ArrayList<>();
        ColouredCard card;
        try {
            card = (ColouredCard) boardTile.getCard();
        } catch (ClassCastException ex) { // boardTile is not a ColouredCard and therefore cannot be a pivot
            return null;
        }

        if (card.getResource() != this.pattern.getPivotResource()) return null;
        pivotGroup.add(boardTile);

        List<RelativePosition> nextRelativePositions = this.pattern.getPositions();
        List<Resource> nextResources = this.pattern.getResources();

        if (nextRelativePositions.size() != nextResources.size()) return null;

        BoardTile currentTile = boardTile;
        for (int i = 0; i < nextRelativePositions.size(); i++) {
            currentTile = currentTile.getNeighbourTile(nextRelativePositions.get(i));
            if (currentTile == null) return null;

            ColouredCard currentCard;
            try {
                currentCard = (ColouredCard) currentTile.getCard();
            } catch (ClassCastException ex) { // currentTile is not a ColouredCard and therefore cannot be part of a pattern
                return null;
            }
            if (currentCard.getResource() != nextResources.get(i)) return null;
            pivotGroup.add(currentTile);
        }

        return new PivotGroup(pivotGroup);
    }


    private record PivotGroup(List<BoardTile> tiles) implements Iterable<BoardTile> {
            private PivotGroup(List<BoardTile> tiles) {
                this.tiles = new ArrayList<>(tiles);
            }

            @Override
            public Iterator<BoardTile> iterator() {
                return tiles.iterator();
            }
        }
}
