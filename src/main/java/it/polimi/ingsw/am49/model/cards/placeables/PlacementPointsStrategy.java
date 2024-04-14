package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

/**
 * It implements the Strategy Pattern, used to choose the right way of
 * calculating the points given by placing a specific {@link PlaceableCard}.
 */
public interface PlacementPointsStrategy {

    /**
     * Calculates the amount of points given to the player considering a single
     * accomplishment of the condition shown on the card.
     *
     * @param playerBoard the whole player board, containing all the already placed cards
     * @param boardTile the object of type {@link BoardTile} rapresenting the tile in which the card is placed
     * @return the points the player gets assuming the condition shown on the card is accomplished a single time
     */
    public int execute(PlayerBoard playerBoard, BoardTile boardTile);
}
