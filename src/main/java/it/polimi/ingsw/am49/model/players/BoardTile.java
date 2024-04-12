package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.util.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the single tile of the playerboard
 */
public class BoardTile implements Serializable {
    /**
     * The card placed in the tile
     */
    private final PlaceableCard card;

    /**
     * Coordinates of the position in the matrix (playerboard)
     */
    private final int row;
    private final int col;

    /**
     * These indicate if the corner (tr=top right, tl=top left, etc.)
     * of the card is covered by another card
     */
    private boolean trCovered;
    private boolean tlCovered;
    private boolean brCovered;
    private boolean blCovered;

    /**
     * This count how many uncovered symbols are in the tile
     */
    private final Map<Symbol, Integer> activeSymbols;

    private final PlayerBoard board;

    /**
     * When a tile is created it has all the corners uncovered
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


    public PlaceableCard getCard() { return this.card; }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public void coverTr() {
        this.trCovered = true;
        this.updateActiveSymbols();
    }

    public void coverTl() {
        this.tlCovered = true;
        this.updateActiveSymbols();
    }

    public void coverBr() {
        this.brCovered = true;
        this.updateActiveSymbols();
    }

    public void coverBl() {
        this.blCovered = true;
        this.updateActiveSymbols();
    }

    public Map<Symbol, Integer> getActiveSymbols() {
        return Collections.unmodifiableMap(this.activeSymbols);
    }

    /**
     * @param relativePosition the relative position of the other tile next to
     * @return the coordinates of the tile
     */
    public Pair<Integer, Integer> getCoords(RelativePosition relativePosition) {
        return this.board.getCoords(relativePosition, this.row, this.col);
    }

    /**
     * @param relativePosition the relative position of the other tile next to
     * @return the tile
     */
    public BoardTile getNeighbourTile(RelativePosition relativePosition) {
        Pair<Integer, Integer> coords = this.getCoords(relativePosition);
        return this.board.getTile(coords.first, coords.second);
    }

    public boolean getTrCovered(){
        return trCovered;
    }

    public boolean getTlCovered(){
        return tlCovered;
    }

    public boolean getBrCovered(){
        return brCovered;
    }

    public boolean getBlCovered(){
        return blCovered;
    }

    public void updateActiveSymbols() {
        for (Symbol symbol : Symbol.values())
            this.activeSymbols.put(symbol, 0);

        if (!this.tlCovered) this.activeSymbols.computeIfPresent(card.getActiveTl(), (k, v) -> v  + 1);
        if (!this.trCovered) this.activeSymbols.computeIfPresent(card.getActiveTr(), (k, v) -> v  + 1);
        if (!this.blCovered) this.activeSymbols.computeIfPresent(card.getActiveBl(), (k, v) -> v  + 1);
        if (!this.brCovered) this.activeSymbols.computeIfPresent(card.getActiveBr(), (k, v) -> v  + 1);

        List<Symbol> centerSymbols = card.getActiveCenterResources().stream().map(Resource::toSymbol).toList();

        for (Symbol symbol : centerSymbols)
            this.activeSymbols.computeIfPresent(symbol, (k, v) -> v  + 1);
    }
}
