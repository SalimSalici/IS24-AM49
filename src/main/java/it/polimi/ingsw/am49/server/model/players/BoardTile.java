package it.polimi.ingsw.am49.server.model.players;

import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.common.util.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a single tile of the player board.
 */
public class BoardTile implements Serializable {

    /**
     * The card placed in the tile.
     */
    private final PlaceableCard card;

    /**
     * Coordinates of the position in the matrix (player board).
     */
    private final int row;
    private final int col;

    /**
     * Indicates if the corners (tr=top right, tl=top left, etc.) of the card are covered by another card.
     */
    private boolean trCovered;
    private boolean tlCovered;
    private boolean brCovered;
    private boolean blCovered;

    /**
     * Counts how many uncovered symbols are in the tile.
     */
    private final Map<Symbol, Integer> activeSymbols;

    /**
     * Reference to the player board.
     */
    private final PlayerBoard board;

    /**
     * Constructs a BoardTile with specified card, row, column, and player board.
     * When a tile is created, it has all the corners uncovered.
     *
     * @param card  the card placed in the tile
     * @param row   the row position of the tile
     * @param col   the column position of the tile
     * @param board the player board
     */
    public BoardTile(PlaceableCard card, int row, int col, PlayerBoard board) {
        this.card = card;
        this.row = row;
        this.col = col;
        this.trCovered = false;
        this.tlCovered = false;
        this.brCovered = false;
        this.blCovered = false;
        this.board = board;
        this.activeSymbols = new HashMap<>();
        this.updateActiveSymbols();
    }

    /**
     * Gets the card placed in the tile.
     *
     * @return the card
     */
    public PlaceableCard getCard() { return this.card; }

    /**
     * Gets the row position of the tile.
     *
     * @return the row position
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Gets the column position of the tile.
     *
     * @return the column position
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Marks the top right corner as covered and updates active symbols.
     */
    public void coverTr() {
        this.trCovered = true;
        this.updateActiveSymbols();
    }

    /**
     * Marks the top left corner as covered and updates active symbols.
     */
    public void coverTl() {
        this.tlCovered = true;
        this.updateActiveSymbols();
    }

    /**
     * Marks the bottom right corner as covered and updates active symbols.
     */
    public void coverBr() {
        this.brCovered = true;
        this.updateActiveSymbols();
    }

    /**
     * Marks the bottom left corner as covered and updates active symbols.
     */
    public void coverBl() {
        this.blCovered = true;
        this.updateActiveSymbols();
    }

    /**
     * Gets the active symbols of the tile.
     *
     * @return a map of active symbols and their counts
     */
    public Map<Symbol, Integer> getActiveSymbols() {
        return new HashMap<>(this.activeSymbols);
    }

    /**
     * Gets the coordinates of a neighboring tile based on the relative position.
     *
     * @param relativePosition the relative position of the other tile
     * @return the coordinates of the neighboring tile
     */
    public Pair<Integer, Integer> getCoords(RelativePosition relativePosition) {
        return this.board.getCoords(relativePosition, this.row, this.col);
    }

    /**
     * Gets the neighboring tile based on the relative position.
     *
     * @param relativePosition the relative position of the other tile
     * @return the neighboring tile
     */
    public BoardTile getNeighbourTile(RelativePosition relativePosition) {
        Pair<Integer, Integer> coords = this.getCoords(relativePosition);
        return this.board.getTile(coords.first, coords.second);
    }

    /**
     * Updates the active symbols on the tile based on covered corners and center symbols.
     */
    public void updateActiveSymbols() {
        for (Symbol symbol : Symbol.values()) {
            this.activeSymbols.put(symbol, 0);
        }

        if (!this.tlCovered) this.activeSymbols.computeIfPresent(card.getActiveTl(), (k, v) -> v + 1);
        if (!this.trCovered) this.activeSymbols.computeIfPresent(card.getActiveTr(), (k, v) -> v + 1);
        if (!this.blCovered) this.activeSymbols.computeIfPresent(card.getActiveBl(), (k, v) -> v + 1);
        if (!this.brCovered) this.activeSymbols.computeIfPresent(card.getActiveBr(), (k, v) -> v + 1);

        List<Symbol> centerSymbols = card.getActiveCenterResources().stream().map(Resource::toSymbol).toList();

        for (Symbol symbol : centerSymbols) {
            this.activeSymbols.computeIfPresent(symbol, (k, v) -> v + 1);
        }
    }
}
