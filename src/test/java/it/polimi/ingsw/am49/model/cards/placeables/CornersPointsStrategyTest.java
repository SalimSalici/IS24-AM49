package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CornersPointsStrategyTest {

    @Test
    void execute() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        GoldCard goldTest = DeckLoader.getInstance().getNewGoldCardById(44);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(goldTest, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        CornersPointsStrategy cornersTest = new CornersPointsStrategy();
        int points = cornersTest.execute(testBoard, tile1);
        Assertions.assertEquals(1, points);
    }
}