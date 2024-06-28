package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.util.Pair;

/**
 * Represents a virtual tile on the game board. Manages the position, card, and neighbors of the tile.
 */
public class VirtualTile implements Comparable<VirtualTile> {

    /**
     * The card that is in this virtual tile
     */
    private final VirtualCard card;

    /**
     * the row of the tile in the matrix representing the game board
     */
    private final int row;

    /**
     * the column of the tile in the matrix representing the game board
     */
    private final int col;

    /**
     * Used to identify which is above
     */
    private final int zIndex;

    /**
     * The board that the tile is a part of
     */
    private final VirtualBoard board;

    /**
     * Constructs a new VirtualTile with the specified card, position, Z-index, and board reference.
     *
     * @param card   the card associated with this tile
     * @param row    the row position of the tile
     * @param col    the column position of the tile
     * @param zIndex the Z-index of the tile
     * @param board  the board to which this tile belongs
     */
    public VirtualTile(VirtualCard card, int row, int col, int zIndex, VirtualBoard board) {
        this.card = card;
        this.row = row;
        this.col = col;
        this.zIndex = zIndex;
        this.board = board;
    }

    /**
     * Retrieves the neighboring tile in the specified relative position.
     *
     * @param relativePosition the relative position of the neighbor tile
     * @return the neighboring VirtualTile, or null if no tile exists at that position
     */
    public VirtualTile getNeighbourTile(RelativePosition relativePosition) {
        Pair<Integer, Integer> coords = this.getCoords(relativePosition);
        return this.board.getTile(coords.first, coords.second);
    }

    /**
     * @return the row position of the tile
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column position of the tile
     */
    public int getCol() {
        return col;
    }

    /**
     * @return the expanded row position of the tile, adjusted for odd and even columns
     */
    public int getExpandedRow() {
        return col % 2 == 0 ? 2 * row : 2 * row + 1;
    }

    /**
     * @return the expanded column position of the tile
     */
    public int getExpandedCol() {
        return col;
    }

    /**
     * Returns the coordinates of the neighboring tile in the specified relative position.
     *
     * @param relativePosition the relative position of the neighbor tile
     * @return a Pair representing the coordinates of the neighboring tile
     */
    public Pair<Integer, Integer> getCoords(RelativePosition relativePosition) {
        return this.board.getCoords(relativePosition, this.row, this.col);
    }

    /**
     * @return the card associated with this tile
     */
    public VirtualCard getCard() {
        return card;
    }

    /**
     * @return the Z-index of this tile
     */
    public int getzIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(VirtualTile other) {
        return this.zIndex - other.zIndex;
    }
}
