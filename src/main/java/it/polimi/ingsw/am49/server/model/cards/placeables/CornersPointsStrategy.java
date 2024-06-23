package it.polimi.ingsw.am49.server.model.cards.placeables;

import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;

import java.io.Serializable;

/**
 * Is the strategy of {@link PlacementPointsStrategy} used to rapresent the
 * {@link PlaceableCard}s that give to the player the amount of points
 * shown on the card multiplied by the number of corners the card covers.
 */
public class CornersPointsStrategy implements PlacementPointsStrategy, Serializable {
    @Override
    public int execute(PlayerBoard playerBoard, BoardTile boardTile) {
        int coveringCorners = 0;

        if(boardTile.getNeighbourTile(RelativePosition.TOP_RIGHT) != null) coveringCorners++;
        if(boardTile.getNeighbourTile(RelativePosition.TOP_LEFT) != null) coveringCorners++;
        if(boardTile.getNeighbourTile(RelativePosition.BOTTOM_RIGHT) != null) coveringCorners++;
        if(boardTile.getNeighbourTile(RelativePosition.BOTTOM_LEFT) != null) coveringCorners++;

        return coveringCorners;
    }
}
