package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.EndGameUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class EndgameEventTest {

    @Test
    void getTypeTest() {
        EndgameEvent event = new EndgameEvent(new HashMap<>(), null);
        assertEquals(GameEventType.END_GAME, event.getType(), "getType should return END_GAME");
    }

    @Test
    void toGameUpdateTest() {
        // Mock players
        Player mockPlayer1 = mock(Player.class);
        Player mockPlayer2 = mock(Player.class);
        ObjectiveCard mockObjectiveCard = mock(ObjectiveCard.class);

        when(mockPlayer1.getUsername()).thenReturn("Player1");
        when(mockPlayer1.getPoints()).thenReturn(100);
        when(mockPlayer1.getPersonalObjective()).thenReturn(mockObjectiveCard);
        when(mockPlayer2.getUsername()).thenReturn("Player2");
        when(mockPlayer2.getPoints()).thenReturn(80);
        when(mockPlayer2.getPersonalObjective()).thenReturn(mockObjectiveCard);

        when(mockObjectiveCard.getId()).thenReturn(101);

        Map<Player, Integer> playersToAchievedObjectives = new HashMap<>();
        playersToAchievedObjectives.put(mockPlayer1, 5);
        playersToAchievedObjectives.put(mockPlayer2, 3);

        EndgameEvent event = new EndgameEvent(playersToAchievedObjectives, mockPlayer1);

        // Act
        EndGameUpdate update = (EndGameUpdate) event.toGameUpdate();

        // Assert
        assertNotNull(update);
        assertEquals("Player1", update.forfeitWinner(), "Forfeit winner should match the input.");

        // Check the contents of the playerToPoints map
        Map<String, Integer[]> playerToPoints = update.playerToPoints();
        assertNotNull(playerToPoints, "Player to points map should not be null.");
        assertTrue(playerToPoints.containsKey("Player1"));
        assertTrue(playerToPoints.containsKey("Player2"));

        assertArrayEquals(new Integer[]{100, 5, 101}, playerToPoints.get("Player1"), "Player1 data should match expected values.");
        assertArrayEquals(new Integer[]{80, 3, 101}, playerToPoints.get("Player2"), "Player2 data should match expected values.");
    }
}
