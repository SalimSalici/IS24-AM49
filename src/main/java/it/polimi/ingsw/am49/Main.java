package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.events.EventListener;
import it.polimi.ingsw.am49.model.events.GameEvent;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        Client nico = new Client("nico", server);
        Client lori = new Client("lori", server);
        Client salim = new Client("salim", server);
        Client matte = new Client("matteo", server);

        nico.createGame(3);
        salim.joinGame(0);
        lori.joinGame(0);

        nico.chooseStarterSide(true);
        salim.chooseStarterSide(false);
        lori.chooseStarterSide(true);

        while (true) {
            Random random = new Random();
            int i = random.nextInt(200);
            try {
                nico.chooseObjective(i);
                break;
            } catch (Exception e) {
                //System.out.println("nico-force");
            }
        }
        while (true) {
            Random random = new Random();
            int i = random.nextInt(200);
            try {
                salim.chooseObjective(i);
                break;
            } catch (Exception e) {
                //System.out.println("salim-force");
            }
        }

        while (true) {
            Random random = new Random();
            int i = random.nextInt(200);
            try {
                lori.chooseObjective(i);
                break;
            } catch (Exception e) {
                //System.out.println("lori-force");
            }
        }

        PlayerBoard playerBoard = new PlayerBoard(DeckLoader.getInstance().getNewStarterCardById(82));
        BoardTile starterTile = playerBoard.getStarterTile();

        Scanner scanner = new Scanner(System.in);

        Client currentClient = null;

        do {
            System.out.print("choose client: " );
            String currentUsername = scanner.nextLine();

            currentClient = switch (currentUsername) {
                case "nico" -> nico;
                case "salim" -> salim;
                case "lori" -> lori;
                default -> null;
            };

            System.out.print("choose id card to place: " );
            int cardId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("choose row where to place the card: " );
            int row = scanner.nextInt();
            scanner.nextLine();
            System.out.print("choose col where to place the card: " );
            int col = scanner.nextInt();
            scanner.nextLine();

            if (currentClient != null) {
                currentClient.placeCard(cardId, row, col, CornerPosition.TOP_LEFT, false);
                currentClient.drawCard(DrawPosition.RESOURCE_DECK, 0);
            } else {
                System.err.println("currentClient is null");
            }
        } while (currentClient != null);
        System.out.println("The end...");

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
    }

    static class StubEventListener implements EventListener {

        List<GameEvent> events = new LinkedList<>();

        StubEventListener(Game game) {
            for (GameEventType type : GameEventType.values())
                game.addEventListener(type, this);
        }

        @Override
        public void onEventTrigger(GameEvent event) {
            System.out.println("Event " + event.getType() + " triggered");
            this.events.add(event);
        }

        public List<GameEvent> getEvents() {
            return this.events;
        }
    }
}
