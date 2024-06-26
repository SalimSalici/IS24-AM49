package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.server.model.cards.placeables.*;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

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

    @Test
    void getPoints(){
        PlaceableCard placeableCard = new GoldCard(12, Symbol.INKWELL, Symbol.MANUSCRIPT, Symbol.BUGS, Symbol.EMPTY, Resource.BUGS, 1, new BasicPointsStrategy(), new HashMap<>(), true );
        Assertions.assertEquals(1, placeableCard.getPoints());
    }

    @Test
    void getTr(){
        PlaceableCard placeableCard = new GoldCard(12, Symbol.INKWELL, Symbol.MANUSCRIPT, Symbol.BUGS, Symbol.EMPTY, Resource.BUGS, 1, new BasicPointsStrategy(), new HashMap<>(), true );
        Assertions.assertEquals(Symbol.INKWELL, placeableCard.getTr());
    }

    @Test
    void getTl(){
        PlaceableCard placeableCard = new GoldCard(12, Symbol.INKWELL, Symbol.MANUSCRIPT, Symbol.BUGS, Symbol.EMPTY, Resource.BUGS, 1, new BasicPointsStrategy(), new HashMap<>(), true );
        Assertions.assertEquals(Symbol.MANUSCRIPT, placeableCard.getTl());
    }

    @Test
    void getBr(){
        PlaceableCard placeableCard = new GoldCard(12, Symbol.INKWELL, Symbol.MANUSCRIPT, Symbol.BUGS, Symbol.EMPTY, Resource.BUGS, 1, new BasicPointsStrategy(), new HashMap<>(), true );
        Assertions.assertEquals(Symbol.BUGS, placeableCard.getBr());
    }

    @Test
    void getBl(){
        PlaceableCard placeableCard = new GoldCard(12, Symbol.INKWELL, Symbol.MANUSCRIPT, Symbol.BUGS, Symbol.EMPTY, Resource.BUGS, 1, new BasicPointsStrategy(), new HashMap<>(), true );
        Assertions.assertEquals(Symbol.EMPTY, placeableCard.getBl());
    }

}