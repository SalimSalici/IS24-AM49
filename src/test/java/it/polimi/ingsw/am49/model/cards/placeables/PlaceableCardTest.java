package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PlaceableCardTest {

    @Test
    void calculatePoints() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        GoldCard goldTest = DeckLoader.getInstance().getNewGoldCardById(44);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(goldTest, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        int points = goldTest.calculatePoints(testBoard, tile1);
        Assertions.assertEquals(2, points);
    }
}