package it.polimi.ingsw.am49.server.model.cards.placeables;

import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;

import java.io.Serializable;

/**
 * Is the strategy of {@link PlacementPointsStrategy} used to rapresent the
 * {@link PlaceableCard}s that give a fixed amount of points.
 */
public class BasicPointsStrategy implements PlacementPointsStrategy, Serializable {

    @Override
    public int execute(PlayerBoard playerBoard, BoardTile boardTile){
        return 1;
    }
}
