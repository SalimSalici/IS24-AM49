package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BoardTileTest {

    @Test
    void updateActiveSymbols() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(2);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(1);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        Map<Symbol, Integer> activeSymbol = tile1.getActiveSymbols();
        System.out.println(activeSymbol);
        testBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
        tile1.updateActiveSymbols();
        Map<Symbol, Integer> newActiveSymbol = tile1.getActiveSymbols();
        System.out.println(newActiveSymbol);
    }
}