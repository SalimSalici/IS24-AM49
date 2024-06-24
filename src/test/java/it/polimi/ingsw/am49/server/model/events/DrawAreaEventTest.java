package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.common.gameupdates.DrawAreaUpdate;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class DrawAreaEventTest {

    @Test
    void getTypeTest() {
        DrawAreaEvent event = new DrawAreaEvent(10, 5, Resource.BUGS, Resource.LEAVES, List.of(), List.of());
        assertEquals(GameEventType.DRAW_AREA_EVENT, event.getType(), "getType should return DRAW_AREA_EVENT");
    }

    @Test
    void toGameUpdateTest() {
        // Mock cards
        ResourceCard mockResourceCard = mock(ResourceCard.class);
        when(mockResourceCard.getId()).thenReturn(101);

        GoldCard mockGoldCard = mock(GoldCard.class);
        when(mockGoldCard.getId()).thenReturn(201);

        // Prepare data with null values to test null handling
        List<ResourceCard> revealedResources = Arrays.asList(mockResourceCard, null);  // Including a null to test null handling
        List<GoldCard> revealedGolds = Arrays.asList(mockGoldCard, null);  // Including a null to test null handling

        DrawAreaEvent event = new DrawAreaEvent(
                10, 5,
                Resource.BUGS, Resource.LEAVES,
                revealedResources, revealedGolds
        );

        // Act
        DrawAreaUpdate update = (DrawAreaUpdate) event.toGameUpdate();

        // Assert
        assertNotNull(update);
        assertEquals(10, update.remainingResources());
        assertEquals(5, update.remainingGolds());
        assertEquals(Resource.BUGS, update.deckTopResource());
        assertEquals(Resource.LEAVES, update.deckTopGold());

        // Verify the ID list for resources
        List<Integer> actualResourceIDs = revealedResources.stream()
                .map(card -> card != null ? card.getId() : null)
                .collect(Collectors.toList());
        List<Integer> actualGoldIDs = revealedGolds.stream()
                .map(card -> card != null ? card.getId() : null)
                .collect(Collectors.toList());

        List<Integer> expectedResourceIDs = Arrays.asList(101, null);
        List<Integer> expectedGoldIDs = Arrays.asList(201, null);
        assertEquals(expectedResourceIDs, actualResourceIDs, "Revealed resource IDs do not match expected");
        assertEquals(expectedGoldIDs, actualGoldIDs, "Revealed gold IDs do not match expected");
    }
}
