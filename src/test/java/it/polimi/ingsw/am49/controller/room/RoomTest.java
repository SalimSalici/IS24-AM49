package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.ClientHandler;
import it.polimi.ingsw.am49.server.ServerApp;
import it.polimi.ingsw.am49.server.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomTest {
    private Room room;
    private final ClientHandler mockCreatorClient = mock(ClientHandler.class);
    private final String creatorUsername = "creator";
    private final ClientHandler mockAnotherClient = mock(ClientHandler.class);
    private final String anotherUsername = "another";

    @BeforeEach
    void setUp() {
        int maxPlayers = 4;
        room = new Room("testRoom", maxPlayers, mockCreatorClient, creatorUsername, mock(ServerApp.class));
    }

    @Test
    void testRoomName() {
        assertEquals(room.getRoomName(), "testRoom");
    }

    @Test
    void testAddNewPlayer() throws JoinRoomException, GameAlreadyStartedException {
        room.addNewPlayer(mock(ClientHandler.class), "player2");

        assertEquals(2, room.getCurrentPlayers());
        assertTrue(room.getRoomInfo().playersToColors().containsKey("player2"));

        room.addNewPlayer(mock(ClientHandler.class), "player3");

        assertEquals(3, room.getCurrentPlayers());
        assertTrue(room.getRoomInfo().playersToColors().containsKey("player3"));

        room.addNewPlayer(mock(ClientHandler.class), "player4");

        assertEquals(4, room.getCurrentPlayers());
        assertTrue(room.getRoomInfo().playersToColors().containsKey("player4"));
    }

    @Test
    void testAddNewPlayerWhenRoomIsFull() {
        assertThrows(JoinRoomException.class, () -> {
            room.addNewPlayer(mock(ClientHandler.class), "player2");
            room.addNewPlayer(mock(ClientHandler.class), "player3");
            room.addNewPlayer(mock(ClientHandler.class), "player4");
            room.addNewPlayer(mock(ClientHandler.class), "player5");
        });
    }

    @Test
    void testAddNewPlayerWhenUsernameTaken() {
        assertThrows(JoinRoomException.class, () -> room.addNewPlayer(mock(ClientHandler.class), creatorUsername));
    }

    @Test
    void testClientReady() throws RoomException {
        room.clientReady(mockCreatorClient, Color.RED);
        assertEquals(room.getRoomInfo().playersToColors().get("creator"), Color.RED);
    }

    @Test
    void testGameStarted() throws JoinRoomException, RoomException, GameAlreadyStartedException {
        HashMap<String, Color> players = new HashMap<>();
        players.put("player2", Color.RED);
        players.put("player3", Color.GREEN);
        players.put("player4", Color.BLUE);
        this.addPlayersAndMakeThemReady(room, players);
        room.clientReady(mockCreatorClient, Color.YELLOW);
        assertTrue(room.isGameStarted());
    }

    @Test
    void testReconnectPlayer() throws JoinRoomException, RoomException, InterruptedException, InvalidActionException, NotYourTurnException, GameAlreadyStartedException {

        ClientHandler player1 = mock(ClientHandler.class);
        ClientHandler player2 = mock(ClientHandler.class);
        Room room = new Room("testRoom", 2, player1, "player1", mock(ServerApp.class));

        assertThrows(JoinRoomException.class, () -> room.reconnect(player2, "player2"));

        room.addNewPlayer(player2, "player2");
        room.clientReady(player1, Color.RED);
        room.clientReady(player2, Color.BLUE);

        Thread.sleep(500);

        // Execute all actions needed to get to the PLACE_CARD_STATE

        room.executeGameAction(player1, new ChooseStarterSideAction("player1", true));
        room.executeGameAction(player2, new ChooseStarterSideAction("player2", false));

        ArgumentCaptor<ChoosableObjectivesUpdate> captor = ArgumentCaptor.forClass(ChoosableObjectivesUpdate.class);

        verify(player1, timeout(1000)).receiveGameUpdate(captor.capture());
        int objective1 = captor.getValue().objectiveCards().getFirst();

        verify(player2, timeout(1000)).receiveGameUpdate(captor.capture());
        int objective2 = captor.getValue().objectiveCards().getFirst();

        room.executeGameAction(player1, new ChooseObjectiveAction("player1", objective1));
        room.executeGameAction(player2, new ChooseObjectiveAction("player2", objective2));

        room.removePlayer(player2);

        assertEquals(room.getCurrentPlayers(), 1);
        assertFalse(room.getRoomInfo().playersToColors().containsKey("player2"));

        ClientHandler reconnectingClient = mock(ClientHandler.class);
        room.reconnect(reconnectingClient, "player2");

        assertEquals(room.getCurrentPlayers(), 2);
        assertEquals(room.getRoomInfo().playersToColors().get("player2"), Color.BLUE);
    }

    @Test
    void testRemovePlayer() throws JoinRoomException, GameAlreadyStartedException {
        room.addNewPlayer(mockAnotherClient, anotherUsername);
        boolean removed = room.removePlayer(mockAnotherClient);

        assertTrue(removed);
        assertEquals(1, room.getCurrentPlayers());
        assertFalse(room.getRoomInfo().playersToColors().containsKey(anotherUsername));
    }

    @Test
    void testClientReadyWhenColorNotAvailable() throws RoomException, JoinRoomException, GameAlreadyStartedException {
        room.addNewPlayer(mockAnotherClient, anotherUsername);
        room.clientReady(mockAnotherClient, Color.RED);

        assertThrows(RoomException.class, () -> room.clientReady(mockCreatorClient, Color.RED));
    }

    @Test
    void testClientNoMoreReady() throws RoomException, JoinRoomException, GameAlreadyStartedException {
        room.addNewPlayer(mockAnotherClient, anotherUsername);
        room.clientReady(mockAnotherClient, Color.RED);
        room.clientNoMoreReady(mockAnotherClient);

        assertTrue(
                room.getRoomInfo().playersToColors().containsKey(anotherUsername) &&
                room.getRoomInfo().playersToColors().get(anotherUsername) == null
        );
    }

    @Test
    void testIsGameOver() {
        assertFalse(room.isGameOver());
    }

    @Test
    void testExecuteGameAction() throws JoinRoomException, RoomException, GameAlreadyStartedException {

        assertThrows(InvalidActionException.class,
                () -> room.executeGameAction(mockCreatorClient, new ChooseStarterSideAction("wrongUsername", true)));

        ClientHandler player3 = mock(ClientHandler.class);
        ClientHandler player4 = mock(ClientHandler.class);
        room.addNewPlayer(mockAnotherClient, anotherUsername);
        room.addNewPlayer(player3, "player3");
        room.addNewPlayer(player4, "player4");

        room.clientReady(mockCreatorClient, Color.RED);
        room.clientReady(mockAnotherClient, Color.BLUE);
        room.clientReady(player3, Color.YELLOW);
        room.clientReady(player4, Color.GREEN);

        assertThrows(InvalidActionException.class,
                () -> room.executeGameAction(mockCreatorClient, new ChooseStarterSideAction("wrongUsername", true)));

        assertDoesNotThrow(
                () -> room.executeGameAction(mockCreatorClient, new ChooseStarterSideAction(creatorUsername, true)));
    }

    @Test
    void testNewChatMSG() throws JoinRoomException, GameAlreadyStartedException {
        ClientHandler player2 = mock(ClientHandler.class);
        ClientHandler player3 = mock(ClientHandler.class);
        room.addNewPlayer(player2, "player2");
        room.addNewPlayer(player3, "player3");

        ChatMSG broadcastMsg = new ChatMSG("Hello everyone!","creator", "*");
        room.newChatMSG(broadcastMsg);

        verify(mockCreatorClient, times(1)).receiveChatMessage(broadcastMsg);
        verify(player2, times(1)).receiveChatMessage(broadcastMsg);
        verify(player3, times(1)).receiveChatMessage(broadcastMsg);

        ChatMSG directMsg = new ChatMSG("Hello player2!","creator", "player2");
        room.newChatMSG(directMsg);

        verify(mockCreatorClient, times(1)).receiveChatMessage(directMsg);
        verify(player2, times(1)).receiveChatMessage(directMsg);
        verify(player3, never()).receiveChatMessage(directMsg);

        ChatMSG invalidMsg = new ChatMSG("This shouldn't be sent","creator", "nonexistent");
        room.newChatMSG(invalidMsg);

        verify(mockCreatorClient, never()).receiveChatMessage(invalidMsg);
        verify(player2, never()).receiveChatMessage(invalidMsg);
        verify(player3, never()).receiveChatMessage(invalidMsg);
    }

    private List<ClientHandler> addPlayersAndMakeThemReady(Room room, HashMap<String, Color> players) throws JoinRoomException, RoomException, GameAlreadyStartedException {
        List<ClientHandler> clients = new LinkedList<>();
        for (Map.Entry<String, Color> player : players.entrySet()) {
            ClientHandler client = mock(ClientHandler.class);
            room.addNewPlayer(client, player.getKey());
            room.clientReady(client, player.getValue());
            clients.add(client);
        }
        return clients;
    }

}