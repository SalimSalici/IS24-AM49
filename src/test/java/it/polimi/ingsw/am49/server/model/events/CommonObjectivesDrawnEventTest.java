package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class CommonObjectivesDrawnEventTest {

    @Test
    void testGetType() {
        // Arrange
        List<ObjectiveCard> mockObjectives = Arrays.asList(mock(ObjectiveCard.class), mock(ObjectiveCard.class));
        CommonObjectivesDrawnEvent event = new CommonObjectivesDrawnEvent(mockObjectives);

        // Act
        GameEventType type = event.getType();

        // Assert
        assertEquals(GameEventType.COMMON_OBJECTIVES_DRAWN, type, "getType should return COMMON_OBJECTIVES_DRAWN");
    }

    @Test
    void testToGameUpdate() {
        // Arrange
        List<ObjectiveCard> mockObjectives = Arrays.asList(mock(ObjectiveCard.class), mock(ObjectiveCard.class));
        CommonObjectivesDrawnEvent event = new CommonObjectivesDrawnEvent(mockObjectives);

        // Act
        GameUpdate update = event.toGameUpdate();

        // Assert
        assertNull(update, "toGameUpdate should return null as currently implemented");
    }
}
