package it.polimi.ingsw.am49.model.players;

import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.decks.DeckLoader;
import it.polimi.ingsw.am49.common.enumerations.CornerPosition;
import it.polimi.ingsw.am49.common.enumerations.RelativePosition;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PlayerTest {

    @Test
    void placeCard() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        Player matteo = new Player("Matteo");
        matteo.setStarterCard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(1);
        BoardTile starterTile = matteo.getBoard().getStarterTile();
        matteo.drawCard(res1);
        matteo.placeCard(res1, starterTile.getRow(), starterTile.getCol(), CornerPosition.TOP_RIGHT);
        BoardTile tile1 = starterTile.getNeighbourTile(RelativePosition.TOP_RIGHT);
        PlaceableCard testCard = tile1.getCard();
        Assertions.assertEquals(res1, testCard);
    }

    @Test
    void calculateFinalPoints() throws Exception {
        StarterCard start = DeckLoader.getInstance().getNewStarterCardById(81);
        Player matteo = new Player("Matteo");
        matteo.setStarterCard(start);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(1);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(2);
        ResourceCard res3 = DeckLoader.getInstance().getNewResourceCardById(3);
        BoardTile starterTile = matteo.getBoard().getStarterTile();
        matteo.drawCard(res1);
        matteo.placeCard(res1, starterTile.getRow(), starterTile.getCol(), CornerPosition.TOP_RIGHT);
        BoardTile tile1 = starterTile.getNeighbourTile(RelativePosition.TOP_RIGHT);
        matteo.drawCard(res2);
        matteo.placeCard(res2, tile1.getRow(), tile1.getCol(), CornerPosition.TOP_RIGHT);
        BoardTile tile2 = tile1.getNeighbourTile(RelativePosition.TOP_RIGHT);
        matteo.drawCard(res3);
        matteo.placeCard(res3, tile2.getRow(), tile2.getCol(), CornerPosition.TOP_RIGHT);
        ObjectiveCard obj1 = DeckLoader.getInstance().getNewObjectiveCardById(87);
        ObjectiveCard obj2 = DeckLoader.getInstance().getNewObjectiveCardById(95);
        List<ObjectiveCard> objList = new ArrayList<>();
        objList.add(obj1);
        matteo.setPersonalObjective(obj2);
        int obj = matteo.calculateFinalPoints(objList);
        Assertions.assertEquals(2, obj);

    }
}