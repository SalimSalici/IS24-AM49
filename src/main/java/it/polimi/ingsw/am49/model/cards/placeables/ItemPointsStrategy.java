package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.enumerations.Item;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

/**
 * Is the strategy of {@link PlacementPointsStrategy} used to rapresent the
 * {@link ColouredCard}s that give to the player the amount of points
 * shown on the card multiplied by the number of times a specific
 * {@link Item} appears on the board.
 */
public class ItemPointsStrategy implements PlacementPointsStrategy {

    /**
     * Is the specific {@link Item} considered to calculate the points.
     */
    Item item;

    /**
     * Costructs a new {@link ItemPointsStrategy} with the specified item.
     *
     * @param item the specific {@link Item} considered to calculate the points
     */
    public ItemPointsStrategy(Item item) {
        this.item = item;
    }

    @Override
    public int execute(PlayerBoard playerBoard, BoardTile boardTile) {
        int itemCount = 0;

        for(BoardTile tile : playerBoard.getPlacementOrder()){
            itemCount += tile.getActiveSymbols().get((Item)item);
        }

        return itemCount;
    }

    /**
     *
     * @return the specific {@link Item} considered to calculate the points
     */
    public Item getItem() {
        return this.item;
    }
}
