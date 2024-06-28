package it.polimi.ingsw.am49.server.model.cards.placeables;

import it.polimi.ingsw.am49.common.enumerations.Item;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;

import java.io.Serializable;

/**
 * Is the strategy of {@link PlacementPointsStrategy} used to represent the
 * {@link PlaceableCard}s that give to the player the amount of points
 * shown on the card multiplied by the number of times a specific
 * {@link Symbol} appears on the board.
 */
public class SymbolsPointsStrategy implements PlacementPointsStrategy, Serializable {

    /**
     * Is the specific {@link Item} considered to calculate the points.
     */
    Symbol symbol;

    /**
     * Constructs a new {@link SymbolsPointsStrategy} with the specified item.
     * @param symbol the specific {@link Symbol} considered to calculate the points
     */
    public SymbolsPointsStrategy(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public int execute(PlayerBoard playerBoard, BoardTile boardTile) {
        int symbolCount = 0;

        for(BoardTile tile : playerBoard.getPlacementOrder()){
            symbolCount += tile.getActiveSymbols().get(symbol);
        }

        return symbolCount;
    }

    /**
     *
     * @return the specific {@link Symbol} considered to calculate the points
     */
    public Symbol getSymbol() {
        return this.symbol;
    }
}
