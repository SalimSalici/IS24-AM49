package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

/**
 * Is the strategy of {@link PlacementPointsStrategy} used to rapresent the
 * {@link ColouredCard}s that give a fixed amount of points.
 */
public class BasicPointsStrategy implements PlacementPointsStrategy{

    @Override
    public int execute(PlayerBoard playerBoard, BoardTile boardTile){
        return 1;
    }
}
