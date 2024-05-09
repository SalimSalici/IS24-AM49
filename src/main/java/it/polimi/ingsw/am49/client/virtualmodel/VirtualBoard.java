package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.util.Pair;

public class VirtualBoard {

    private final VirtualTile[][] board;
    private final VirtualTile starterTile;

    private int lastZIndex;

    public VirtualBoard(VirtualCard starterCard) {
        this.board = new VirtualTile[50][50];
        this.starterTile = this.placeCard(starterCard, 25, 25);
        this.lastZIndex = 0;
    }

    public VirtualTile placeCard(VirtualCard card, int row, int col) {
        this.lastZIndex++;
        VirtualTile tile = new VirtualTile(card, row, col, this.lastZIndex, this);
        this.board[row][col] = tile;
        return tile;
    }

    public VirtualTile getStarterTile() {
        return this.starterTile;
    }

    public VirtualTile getTile(int row, int col) {
        return this.board[row][col];
    }

    // TODO: instead of code duplication, consider making this method static in PlayerBoard.java
    public Pair<Integer, Integer> getCoords(RelativePosition relativePosition, int row, int col) {
        switch (relativePosition) {
            case TOP -> {
                return new Pair<>(row - 1, col);
            }
            case TOP_LEFT -> {
                if (col % 2 == 0)
                    return new Pair<>(row - 1, col - 1);
                return new Pair<>(row, col - 1);
            }
            case TOP_RIGHT -> {
                if (col % 2 == 0)
                    return new Pair<>(row - 1, col + 1);
                return new Pair<>(row, col + 1);
            }
            case BOTTOM -> {
                return new Pair<>(row + 1, col);
            }
            case BOTTOM_LEFT -> {
                if (col % 2 == 0)
                    return new Pair<>(row, col - 1);
                return new Pair<>(row + 1, col - 1);
            }
            case BOTTOM_RIGHT -> {
                if (col % 2 == 0)
                    return new Pair<>(row, col + 1);
                return new Pair<>(row + 1, col + 1);
            }
            case LEFT -> {
                return new Pair<>(row, col - 2);
            }
            case RIGHT -> {
                return new Pair<>(row, col + 2);
            }
        }
        return new Pair<>(row, col);
    }
}
