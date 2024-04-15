package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.controller.Client;
import it.polimi.ingsw.am49.controller.Server;
import it.polimi.ingsw.am49.controller.SingleGameController;
import it.polimi.ingsw.am49.messages.mts.ChooseObjectiveMTS;
import it.polimi.ingsw.am49.messages.mts.ChooseStarterSideMTS;
import it.polimi.ingsw.am49.messages.mts.JoinGameMTS;
import it.polimi.ingsw.am49.messages.mts.LeaveGameMTS;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectivePointsStrategy;
import it.polimi.ingsw.am49.model.cards.placeables.*;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.RelativePosition;
import it.polimi.ingsw.am49.model.events.ChoosableObjectivesAssignedEvent;
import it.polimi.ingsw.am49.model.events.EventListener;
import it.polimi.ingsw.am49.model.events.GameEvent;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.util.*;

public class Main {

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        Client nico = new Client("niccolo", server);
        Client lori = new Client("lorenzo", server);
        Client salim = new Client("salim", server);
        Client matte = new Client("matteo", server);

        nico.createGame(3);

        lori.joinGame(0);

        lori.leaveGame();

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
                System.out.println("nico-force");
            }
        }
        while (true) {
            Random random = new Random();
            int i = random.nextInt(200);
            try {
                salim.chooseObjective(i);
                break;
            } catch (Exception e) {
                System.out.println("salim-force");
            }
        }while (true) {
            Random random = new Random();
            int i = random.nextInt(200);
            try {
                lori.chooseObjective(i);
                break;
            } catch (Exception e) {
                System.out.println("lori-force");
            }
        }

        PlayerBoard playerBoard = new PlayerBoard(DeckLoader.getInstance().getNewStarterCardById(82));
        BoardTile starterTile = playerBoard.getStarterTile();
        nico.placeCard(34, starterTile.getRow(), starterTile.getCol(), CornerPosition.TOP_LEFT, false);


        //playerBoard.printSimpleBoard();

        System.out.println("The end...");
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
