package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.HandUpdate;
import it.polimi.ingsw.am49.common.gameupdates.HiddenHandUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.util.Pair;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class HandEventTest {

    private HandEvent event;
    private Player mockPlayer;
    private PlaceableCard mockCard;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockCard = mock(PlaceableCard.class);
        when(mockPlayer.getUsername()).thenReturn("Player1");
        when(mockCard.getId()).thenReturn(100);
        when(mockCard.getResource()).thenReturn(Resource.LEAVES);
        when(mockCard.isGoldCard()).thenReturn(true);

        List<PlaceableCard> hand = Arrays.asList(mockCard);
        event = new HandEvent(mockPlayer, hand);
    }

    @Test
    void getTypeTest() {
        assertEquals(GameEventType.HAND_UPDATE_EVENT, event.getType(), "getType should return HAND_UPDATE_EVENT");
    }

    @Test
    void toGameUpdateTest() {
        HandUpdate update = event.toGameUpdate();
        assertNotNull(update, "HandUpdate should not be null");
        assertEquals("Player1", update.username(), "Player username should match");
        assertTrue(update.handIds().contains(100), "Card IDs should include 100");
    }

    @Test
    void toHiddenHandUpdateTest() {
        HiddenHandUpdate hiddenUpdate = event.toHiddenHandUpdate();
        assertNotNull(hiddenUpdate, "HiddenHandUpdate should not be null");
        assertEquals("Player1", hiddenUpdate.username(), "Player username should match");
        assertFalse(hiddenUpdate.hiddenHand().isEmpty(), "Hand should not be empty");

        Pair<Resource, Boolean> firstPair = hiddenUpdate.hiddenHand().get(0);
        assertEquals(Resource.LEAVES, firstPair.first, "Resource type should match");  // Accessing the first field directly
        assertTrue(firstPair.second, "Gold card visibility should be true");  // Accessing the second field directly
    }

}
