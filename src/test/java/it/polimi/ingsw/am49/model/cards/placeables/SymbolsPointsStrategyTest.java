package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.SymbolsPointsStrategy;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SymbolsPointsStrategyTest {

    @Test
    void execute() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(6);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(25);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile2 = testBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
        SymbolsPointsStrategy itemTest = new SymbolsPointsStrategy(Symbol.INKWELL);
        int points = itemTest.execute(testBoard, tile2);
        Assertions.assertEquals(2, points);
    }

    @Test
    void getSymbol(){
        SymbolsPointsStrategy itemTest = new SymbolsPointsStrategy(Symbol.INKWELL);
        Assertions.assertEquals(Symbol.INKWELL, itemTest.getSymbol());
    }
}