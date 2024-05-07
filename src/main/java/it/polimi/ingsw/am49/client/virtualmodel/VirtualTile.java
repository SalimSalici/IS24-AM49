package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.util.Pair;

public class VirtualTile implements Comparable<VirtualTile> {
    private final VirtualCard card;

    private final int row;
    private final int col;

    private final int zIndex;

    private final VirtualBoard board;

    public VirtualTile(VirtualCard card, int row, int col, int zIndex, VirtualBoard board) {
        this.card = card;
        this.row = row;
        this.col = col;
        this.zIndex = zIndex;
        this.board = board;
    }

    public VirtualTile getNeighbourTile(RelativePosition relativePosition) {
        Pair<Integer, Integer> coords = this.getCoords(relativePosition);
        return this.board.getTile(coords.first, coords.second);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Pair<Integer, Integer> getCoords(RelativePosition relativePosition) {
        return this.board.getCoords(relativePosition, this.row, this.col);
    }

    public VirtualCard getCard() {
        return card;
    }

    public int getzIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(VirtualTile other) {
        return this.zIndex - other.zIndex;
    }
}
