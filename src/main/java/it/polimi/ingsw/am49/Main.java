package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectivePointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.*;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

public class Main {

    public static void main(String[] args) throws Exception {
//        GameDeck<ResourceCard> resourceGameDeck = DeckLoader.getInstance().getNewResourceDeck();
//        GameDeck<GoldCard> goldGameDeck = DeckLoader.getInstance().getNewGoldDeck();
//        GameDeck<StarterCard> starterGameDeck = DeckLoader.getInstance().getNewStarterDeck();
//        GameDeck<ObjectiveCard> objectiveCardGameDeck = DeckLoader.getInstance().getNewObjectiveDeck();
//
//        while (!resourceGameDeck.isEmpty())
//            System.out.println(resourceGameDeck.draw());
//
//        while (!goldGameDeck.isEmpty())
//            System.out.println(goldGameDeck.draw());
//
//        while (!starterGameDeck.isEmpty())
//            System.out.println(starterGameDeck.draw());
//
//        while (!objectiveCardGameDeck.isEmpty())
//            System.out.println(objectiveCardGameDeck.draw());

        StarterCard starterCard = DeckLoader.getInstance().getNewStarterCardById(81);
        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(1);
        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(2);
        ResourceCard res4 = DeckLoader.getInstance().getNewResourceCardById(4);
        ResourceCard res5 = DeckLoader.getInstance().getNewResourceCardById(5);
        ResourceCard res6 = DeckLoader.getInstance().getNewResourceCardById(6);
        ResourceCard res7 = DeckLoader.getInstance().getNewResourceCardById(7);
        ResourceCard res8 = DeckLoader.getInstance().getNewResourceCardById(32);
        ResourceCard res9 = DeckLoader.getInstance().getNewResourceCardById(14);
        ResourceCard res10 = DeckLoader.getInstance().getNewResourceCardById(33);

        PlayerBoard playerBoard = new PlayerBoard(starterCard);
        BoardTile starterTile = playerBoard.getStarterTile();
        BoardTile tile1 = playerBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile2 = playerBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile4 = playerBoard.placeTile(res4, tile2.getRow(), tile2.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile5 = playerBoard.placeTile(res5, tile4.getRow(), tile4.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile6 = playerBoard.placeTile(res6, tile5.getRow(), tile5.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile7 = playerBoard.placeTile(res7, tile6.getRow(), tile6.getCol(), RelativePosition.TOP_RIGHT);
        BoardTile tile8 = playerBoard.placeTile(res8, tile5.getRow(), tile5.getCol(), RelativePosition.BOTTOM_RIGHT);
        BoardTile tile9 = playerBoard.placeTile(res9, tile4.getRow(), tile4.getCol(), RelativePosition.BOTTOM_RIGHT);
        BoardTile tile10 = playerBoard.placeTile(res10, tile9.getRow(), tile9.getCol(), RelativePosition.BOTTOM_RIGHT);

//        playerBoard.printSimpleBoard();

        ObjectiveCard patternObjOblique = DeckLoader.getInstance().getNewObjectiveCardById(87);
        ObjectivePointsStrategy patterStrategyOblique = patternObjOblique.getPointsStrategy();
        System.out.println("Oblique: " + patterStrategyOblique.execute(playerBoard));

        ObjectiveCard patternObjL = DeckLoader.getInstance().getNewObjectiveCardById(94);
        ObjectivePointsStrategy patterStrategyL = patternObjL.getPointsStrategy();
        System.out.println("L: " + patterStrategyL.execute(playerBoard));
    }
}
