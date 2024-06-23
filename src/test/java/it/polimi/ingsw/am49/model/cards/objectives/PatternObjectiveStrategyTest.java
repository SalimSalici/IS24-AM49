package it.polimi.ingsw.am49.model.cards.objectives;

import it.polimi.ingsw.am49.server.model.cards.objectives.Pattern;
import it.polimi.ingsw.am49.server.model.cards.objectives.PatternObjectiveStrategy;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

class PatternObjectiveStrategyTest {

    @Test
    void execute() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        PlayerBoard testBoard = new PlayerBoard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(1);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(2);
        ResourceCard res3 = DeckLoader.getInstance().getNewResourceCardById(3);
        BoardTile starterTile = testBoard.getStarterTile();
        BoardTile tile1 = testBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile2 = testBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
        testBoard.placeTile(res3, tile2.getRow(), tile2.getCol(), RelativePosition.TOP_RIGHT);
        List<Resource> resources = new LinkedList();
        List<RelativePosition> positions = new LinkedList();
        resources.add(Resource.MUSHROOMS);
        resources.add(Resource.MUSHROOMS);
        positions.add(RelativePosition.TOP_RIGHT);
        positions.add(RelativePosition.TOP_RIGHT);
        Pattern pattern = new Pattern(Resource.MUSHROOMS, resources, positions);
        PatternObjectiveStrategy testStrategy = new PatternObjectiveStrategy(pattern);
        int acc = testStrategy.execute(testBoard);
        Assertions.assertEquals(1, acc);
    }
}