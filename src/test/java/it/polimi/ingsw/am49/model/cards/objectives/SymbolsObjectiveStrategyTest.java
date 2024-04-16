package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SymbolsObjectiveStrategyTest {

    @Test
    void execute() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(6);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(25);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        testBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
        Map<Symbol, Integer> req = new HashMap();
        req.put(Symbol.INKWELL, 2);
        SymbolsObjectiveStrategy testStrategy = new SymbolsObjectiveStrategy(req);
        int acc = testStrategy.execute(testBoard);
        Assertions.assertEquals(1, acc);
    }
}