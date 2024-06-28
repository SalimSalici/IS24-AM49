package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.common.CommonConfig;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.util.Observable;
import it.polimi.ingsw.am49.common.util.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a virtual board in the game. Manages the placement of virtual tiles and cards, and provides methods for retrieving and manipulating the board state.
 */
public class VirtualBoard extends Observable {
    /**
     * The board matrix representing the placement of tiles.
     */
    private final VirtualTile[][] board;

    /**
     * The Z-index of the last placed tile.
     */
    private int lastZIndex;

    /**
     * The row of the last placed tile.
     */
    private int lastPlacedRow;

    /**
     * The column of the last placed tile.
     */
    private int lastPlacedCol;

    /**
     * The starter tile on the board.
     */
    private VirtualTile starterTile;

    /**
     * Constructs a new VirtualBoard with a default size.
     * Initializes the board and other state variables.
     */
    public VirtualBoard() {
        this.board = new VirtualTile[CommonConfig.boardMatrixHeight][CommonConfig.boardMatrixWidth];
        this.lastZIndex = 0;
        this.lastPlacedRow = -1;
        this.lastPlacedCol = -1;
        this.starterTile = null;
    }

    /**
     * Places a card on the board at the specified position.
     *
     * @param card the card to place
     * @param row  the row position to place the card
     * @param col  the column position to place the card
     * @return the newly created VirtualTile representing the placed card
     */
    public VirtualTile placeCard(VirtualCard card, int row, int col) {
        this.lastZIndex++;
        VirtualTile tile = new VirtualTile(card, row, col, this.lastZIndex, this);
        this.board[row][col] = tile;
        this.lastPlacedRow = row;
        this.lastPlacedCol = col;
        if (this.starterTile == null) this.starterTile = tile;
        return tile;
    }

    /**
     * Retrieves the tile at the specified position on the board.
     *
     * @param row the row position
     * @param col the column position
     * @return the VirtualTile at the specified position, or null if no tile is present
     */
    public VirtualTile getTile(int row, int col) {
        return this.board[row][col];
    }

    /**
     * @return the starter tile, or null if no starter tile has been placed
     */
    public VirtualTile getStarterTile() {
        return this.starterTile;
    }

    /**
     * Given the coordinates of a card and a relative position, returns the coordinates of the card in the specified relative position.
     *
     * @param relativePosition the relative position to convert
     * @param row              the current row position of the card
     * @param col              the current column position of the card
     * @return a Pair representing the new coordinates of the card in the specified relative position
     */
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

    /**
     * @return a list of all tiles, ordered by Z-index
     */
    public List<VirtualTile> getOrderedTilesList(){
        return Arrays.stream(this.board)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(VirtualTile::getzIndex))
                .toList();
    }
}
