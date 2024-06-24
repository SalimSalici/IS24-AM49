package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.GameStateChangedUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameStateChangedEventTest {

    private GameStateChangedEvent event;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        when(mockPlayer.getUsername()).thenReturn("Player1");
        event = new GameStateChangedEvent(GameStateType.DRAW_CARD, mockPlayer, 5, 3, false, true);
    }

    @Test
    void getTypeTest() {
        assertEquals(GameEventType.GAME_STATE_CHANGED_EVENT, event.getType(), "getType should return GAME_STATE_CHANGED_EVENT");
    }

    @Test
    void toGameUpdateTest() {
        GameStateChangedUpdate update = event.toGameUpdate();

        assertNotNull(update, "GameStateChangedUpdate should not be null");
        assertEquals(GameStateType.DRAW_CARD, update.gameStateType(), "GameStateType should match");
        assertEquals("Player1", update.currentPlayer(), "Current player username should match");
        assertEquals(5, update.turn(), "Turn number should match");
        assertEquals(3, update.round(), "Round number should match");
        assertFalse(update.endGame(), "End game flag should be false");
        assertTrue(update.finalRound(), "Final round flag should be true");
    }
}
