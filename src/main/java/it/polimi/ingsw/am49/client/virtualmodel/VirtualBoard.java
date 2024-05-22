package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.util.Observable;
import it.polimi.ingsw.am49.util.Pair;

public class VirtualBoard extends Observable {

    private final VirtualTile[][] board;
    private int lastZIndex;
    private int lastPlacedRow;
    private int lastPlacedCol;
    private VirtualTile starterTile;

    public VirtualBoard() {
        this.board = new VirtualTile[50][50];
        this.lastZIndex = 0;
        this.lastPlacedRow = -1;
        this.lastPlacedCol = -1;
        this.starterTile = null;
    }

    public VirtualTile placeCard(VirtualCard card, int row, int col) {
        this.lastZIndex++;
        VirtualTile tile = new VirtualTile(card, row, col, this.lastZIndex, this);
        this.board[row][col] = tile;
        this.lastPlacedRow = row;
        this.lastPlacedCol = col;
        if (this.starterTile == null) this.starterTile = tile;
        this.notifyObservers();
        return tile;
    }

    public VirtualTile getTile(int row, int col) {
        return this.board[row][col];
    }

    public VirtualTile getStarterTile() {
        return this.starterTile;
    }

    // TODO: instead of code duplication, consider making this method static in PlayerBoard.java
    public static Pair<Integer, Integer> getCoords(RelativePosition relativePosition, int row, int col) {
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
