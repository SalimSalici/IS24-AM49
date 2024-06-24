package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.PersonalObjectiveChosenUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.players.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonalObjectiveChosenEventTest {

    private PersonalObjectiveChosenEvent event;
    private Player mockPlayer;
    private ObjectiveCard mockObjectiveCard;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockObjectiveCard = mock(ObjectiveCard.class);
        when(mockPlayer.getUsername()).thenReturn("Player1");
        when(mockObjectiveCard.getId()).thenReturn(101);

        event = new PersonalObjectiveChosenEvent(mockPlayer, mockObjectiveCard);
    }

    @Test
    void getTypeTest() {
        assertEquals(GameEventType.PERSONAL_OBJECTIVE_CHOSEN_EVENT, event.getType(), "getType should return PERSONAL_OBJECTIVE_CHOSEN_EVENT");
    }

    @Test
    void toGameUpdateTest() {
        PersonalObjectiveChosenUpdate update = (PersonalObjectiveChosenUpdate) event.toGameUpdate();
        assertNotNull(update, "Update should not be null");
        assertEquals("Player1", update.username(), "Username should match the expected value");
        assertEquals(101, update.objective(), "Objective card ID should match the expected value");
    }
}
