package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.PlayerOrderUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class PlayersOrderEventTest {

    private PlayersOrderEvent event;
    private Player mockPlayer1;
    private Player mockPlayer2;

    @BeforeEach
    void setUp() {
        mockPlayer1 = mock(Player.class);
        mockPlayer2 = mock(Player.class);
        when(mockPlayer1.getUsername()).thenReturn("Alice");
        when(mockPlayer2.getUsername()).thenReturn("Bob");

        List<Player> playersOrder = Arrays.asList(mockPlayer1, mockPlayer2);
        event = new PlayersOrderEvent(playersOrder);
    }

    @Test
    void getTypeTest() {
        assertEquals(GameEventType.PLAYERS_ORDER_SET_EVENT, event.getType(), "getType should return PLAYERS_ORDER_SET_EVENT");
    }

    @Test
    void toGameUpdateTest() {
        PlayerOrderUpdate update = event.toGameUpdate();
        assertNotNull(update, "PlayerOrderUpdate should not be null");
        List<String> playerOrder = update.playerOrder();  // Directly accessing the playerOrder from the record
        assertEquals(2, playerOrder.size(), "There should be two usernames in the player order.");
        assertTrue(playerOrder.contains("Alice"), "Usernames should include 'Alice'.");
        assertTrue(playerOrder.contains("Bob"), "Usernames should include 'Bob'.");
    }
}
