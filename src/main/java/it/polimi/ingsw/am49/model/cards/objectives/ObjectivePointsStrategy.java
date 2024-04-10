package it.polimi.am49.cn_demo.model.cards.objectives;

import it.polimi.am49.cn_demo.model.cards.placeables.ColouredCard;
import it.polimi.am49.cn_demo.model.players.BoardTile;
import it.polimi.am49.cn_demo.model.players.PlayerBoard;

/**
 * It implements the Strategy Pattern, used to choose the right way of
 * calculating the points given by a specific {@link ObjectiveCard}.
 */
public interface ObjectivePointsStrategy{

    /**
     * Calculates the amount of points given to the player considering a single
     * accomplishment of the objective shown on the card.
     *
     * @param playerBoard the whole player board, containing all the already placed cards
     * @return the points the player gets assuming the objective shown on the card is completed a single time
     */
    int execute(PlayerBoard playerBoard);
}
