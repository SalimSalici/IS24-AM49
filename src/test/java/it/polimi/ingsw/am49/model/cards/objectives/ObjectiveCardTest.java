package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectiveCardTest {

    @Test
    void calculatePoints() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(1);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(2);
        ResourceCard res3 = DeckLoader.getInstance().getNewResourceCardById(3);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile2 = testBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
        testBoard.placeTile(res3, tile2.getRow(), tile2.getCol(), RelativePosition.TOP_RIGHT);
        ObjectiveCard testObj = DeckLoader.getInstance().getNewObjectiveCardById(87);
        int points = testObj.calculatePoints(testBoard);
        Assertions.assertEquals(2, points);
    }
}