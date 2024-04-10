package it.polimi.am49.cn_demo.model.cards.placeables;

import it.polimi.am49.cn_demo.model.enumerations.Item;
import it.polimi.am49.cn_demo.model.enumerations.RelativePosition;
import it.polimi.am49.cn_demo.model.players.BoardTile;
import it.polimi.am49.cn_demo.model.players.PlayerBoard;

/**
 * Is the strategy of {@link PlacementPointsStrategy} used to rapresent the
 * {@link ColouredCard}s that give to the player the amount of points
 * shown on the card multiplied by the number of corners the card covers.
 */
public class CornersPointsStrategy implements PlacementPointsStrategy{
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
