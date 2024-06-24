package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.StartedCardAssignedUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StarterCardAssignedEventTest {

    private StarterCardAssignedEvent event;
    private Player mockPlayer;
    private StarterCard mockStarterCard;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStarterCard = mock(StarterCard.class);
        when(mockPlayer.getUsername()).thenReturn("Player1");
        when(mockStarterCard.getId()).thenReturn(101);

        event = new StarterCardAssignedEvent(mockPlayer, mockStarterCard);
    }

    @Test
    void getTypeTest() {
        assertEquals(GameEventType.STARTER_CARD_ASSIGNED_EVENT, event.getType(), "getType should return STARTER_CARD_ASSIGNED_EVENT");
    }

    @Test
    void toGameUpdateTest() {
        StartedCardAssignedUpdate update = event.toGameUpdate();
        assertNotNull(update, "StartedCardAssignedUpdate should not be null");
        assertEquals("Player1", update.username(), "Username should match the expected value");
        assertEquals(101, update.starterCardId(), "Starter card ID should match the expected value");
    }
}
