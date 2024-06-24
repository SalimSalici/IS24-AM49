package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ChoosableObjectivesEventTest {

    @Test
    void testToGameUpdate() {
        // Mock dependencies
        Player mockPlayer = mock(Player.class);
        List<ObjectiveCard> mockObjectiveCards = IntStream.range(0, 5)
                .mapToObj(i -> {
                    ObjectiveCard card = mock(ObjectiveCard.class);
                    when(card.getId()).thenReturn(100 + i);
                    return card;
                }).collect(Collectors.toList());

        // Stubbing the player's username
        when(mockPlayer.getUsername()).thenReturn("Player1");

        // Create an event instance
        ChoosableObjectivesEvent event = new ChoosableObjectivesEvent(mockPlayer, mockObjectiveCards);

        // Invoke the method under test
        ChoosableObjectivesUpdate update = event.toGameUpdate();

        // Extract the IDs from the update to assert on them
        List<Integer> actualIds = mockObjectiveCards.stream().map(ObjectiveCard::getId).collect(Collectors.toList());

        // Assert results
        assertNotNull(update);
        assertEquals("Player1", update.username());

        // Assert each individual ID
        List<Integer> expectedIds = List.of(100, 101, 102, 103, 104);
        assertEquals(expectedIds.size(), actualIds.size(), "The number of IDs does not match expected count.");
        for (int i = 0; i < expectedIds.size(); i++) {
            assertEquals(expectedIds.get(i), actualIds.get(i), "Mismatch at index " + i);
        }
    }
}
