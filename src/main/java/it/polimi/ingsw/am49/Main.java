package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectivePointsStrategy;
import it.polimi.ingsw.am49.model.cards.objectives.PatternObjectiveStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.*;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.io.*;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws Exception {
        Player salim = new Player("salim");
        Player niccolo = new Player("niccolo");
        Player lorenzo = new Player("lorenzo");
        Player matteo = new Player("matteo");

        Game game = new Game(42);
        game.addPlayer(salim);
        game.addPlayer(niccolo);
        game.addPlayer(lorenzo);
        game.addPlayer(matteo);

        game.getPlayers().forEach(System.out::println);
        System.out.println(game.getGameState());

        game.startGame();
        List<Player> players = game.getPlayers();

        System.out.println();
        game.getPlayers().forEach(System.out::println);
        System.out.println();
        System.out.println(game.getGameState());

        game.chooseStarterSide(game.getCurrentPlayer(), true);
        game.chooseStarterSide(game.getCurrentPlayer(), false);
        game.chooseStarterSide(game.getCurrentPlayer(), true);
        game.chooseStarterSide(game.getCurrentPlayer(), false);

        System.out.println(game.getGameState());

        game.chooseObjective(game.getCurrentPlayer(), DeckLoader.getInstance().getNewObjectiveCardById(101));
        game.chooseObjective(game.getCurrentPlayer(), DeckLoader.getInstance().getNewObjectiveCardById(102));
        game.chooseObjective(game.getCurrentPlayer(), DeckLoader.getInstance().getNewObjectiveCardById(89));
        game.chooseObjective(game.getCurrentPlayer(), DeckLoader.getInstance().getNewObjectiveCardById(90));

        for (int i = 0; i < 6; i++) {
            for (Player p : players) {
                Random random = new Random();
                System.out.println(p.getUsername() + ": " + game.getGameState());
                while (true) {

                    List<BoardTile> playerTiles = p.getBoard().getPlacementOrder();
                    BoardTile parentTile = playerTiles.get(random.nextInt(playerTiles.size()));

                    CornerPosition cornerPosition = CornerPosition.values()[random.nextInt(CornerPosition.values().length)];

                    ColouredCard cardToPlace = p.getHand().stream()
                            .filter(c -> c instanceof ResourceCard)
                            .findFirst()
                            .get();

                    try {
                        game.placeCard(
                                p,
                                cardToPlace,
                                parentTile,
                                cornerPosition
                        );
                        System.out.println(p.getUsername() + ": " + game.getGameState());
                        game.drawCard(p, DrawPosition.RESOURCE_DECK);
                        break;
                    } catch (Exception ex) {
                    }
                }
            }
        }

        game.getPlayers().forEach(System.out::println);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("myObject.dat"))) {
            out.writeObject(game);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("myObject.dat"))) {
            Game loadedObject = (Game) in.readObject();
            System.out.println();
            loadedObject.getPlayers().forEach(System.out::println);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) throws Exception {
//        GameDeck<ResourceCard> resourceGameDeck = DeckLoader.getInstance().getNewResourceDeck();
//        GameDeck<GoldCard> goldGameDeck = DeckLoader.getInstance().getNewGoldDeck();
//        GameDeck<StarterCard> starterGameDeck = DeckLoader.getInstance().getNewStarterDeck();
//        GameDeck<ObjectiveCard> objectiveCardGameDeck = DeckLoader.getInstance().getNewObjectiveDeck();
//
//        while (resourceGameDeck.size() > 0)
//            System.out.println(resourceGameDeck.draw());
//
//        while (goldGameDeck.size() > 0)
//            System.out.println(goldGameDeck.draw());
//
//        while (starterGameDeck.size() > 0)
//            System.out.println(starterGameDeck.draw());
//
//        while (objectiveCardGameDeck.size() > 0)
//            System.out.println(objectiveCardGameDeck.draw());
//
//        StarterCard starterCard = DeckLoader.getInstance().getNewStarterCardById(81);
//        ResourceCard res1 = DeckLoader.getInstance().getNewResourceCardById(1);
//        ResourceCard res2 = DeckLoader.getInstance().getNewResourceCardById(2);
//        ResourceCard res4 = DeckLoader.getInstance().getNewResourceCardById(4);
//        ResourceCard res5 = DeckLoader.getInstance().getNewResourceCardById(5);
//        ResourceCard res6 = DeckLoader.getInstance().getNewResourceCardById(6);
//        ResourceCard res7 = DeckLoader.getInstance().getNewResourceCardById(7);
//        ResourceCard res8 = DeckLoader.getInstance().getNewResourceCardById(32);
//        ResourceCard res9 = DeckLoader.getInstance().getNewResourceCardById(14);
//        ResourceCard res10 = DeckLoader.getInstance().getNewResourceCardById(33);
//
//        PlayerBoard playerBoard = new PlayerBoard(starterCard);
//        BoardTile starterTile = playerBoard.getStarterTile();
//        BoardTile tile1 = playerBoard.placeTile(res1, starterTile.getRow(), starterTile.getCol(), RelativePosition.TOP_RIGHT);
//        BoardTile tile2 = playerBoard.placeTile(res2, tile1.getRow(), tile1.getCol(), RelativePosition.TOP_RIGHT);
//        BoardTile tile4 = playerBoard.placeTile(res4, tile2.getRow(), tile2.getCol(), RelativePosition.TOP_RIGHT);
//        BoardTile tile5 = playerBoard.placeTile(res5, tile4.getRow(), tile4.getCol(), RelativePosition.TOP_RIGHT);
//        BoardTile tile6 = playerBoard.placeTile(res6, tile5.getRow(), tile5.getCol(), RelativePosition.TOP_RIGHT);
//        BoardTile tile7 = playerBoard.placeTile(res7, tile6.getRow(), tile6.getCol(), RelativePosition.TOP_RIGHT);
//        BoardTile tile8 = playerBoard.placeTile(res8, tile5.getRow(), tile5.getCol(), RelativePosition.BOTTOM_RIGHT);
//        BoardTile tile9 = playerBoard.placeTile(res9, tile4.getRow(), tile4.getCol(), RelativePosition.BOTTOM_RIGHT);
//        BoardTile tile10 = playerBoard.placeTile(res10, tile9.getRow(), tile9.getCol(), RelativePosition.BOTTOM_RIGHT);
//
////        playerBoard.printSimpleBoard();
//
//        ObjectiveCard patternObjOblique = DeckLoader.getInstance().getNewObjectiveCardById(87);
//        ObjectivePointsStrategy patterStrategyOblique = patternObjOblique.getPointsStrategy();
//        System.out.println("Oblique: " + patterStrategyOblique.execute(playerBoard));
//
//        ObjectiveCard patternObjL = DeckLoader.getInstance().getNewObjectiveCardById(94);
//        ObjectivePointsStrategy patterStrategyL = patternObjL.getPointsStrategy();
//        System.out.println("L: " + patterStrategyL.execute(playerBoard));
//    }
}
