package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class EventManagerTest {

    private EventManager eventManager;
    private EventListener mockListener;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
        mockListener = mock(EventListener.class);
    }

    @Test
    void testAddEventListenerSingleType() {
        eventManager.addEventListener(GameEventType.CARD_PLACED_EVENT, mockListener);
        GameEvent event = mock(GameEvent.class);
        when(event.getType()).thenReturn(GameEventType.CARD_PLACED_EVENT);
        // Implement other abstract methods here if needed

        eventManager.triggerEvent(event);
        verify(mockListener, times(1)).onEventTrigger(event);
    }

    @Test
    void testAddEventListenerMultipleTypes() {
        eventManager.addEventListener(Arrays.asList(GameEventType.END_GAME, GameEventType.DRAW_AREA_EVENT), mockListener);
        GameEvent endEvent = mock(GameEvent.class);
        when(endEvent.getType()).thenReturn(GameEventType.END_GAME);
        GameEvent drawAreaEvent = mock(GameEvent.class);
        when(drawAreaEvent.getType()).thenReturn(GameEventType.DRAW_AREA_EVENT);

        eventManager.triggerEvent(endEvent);
        eventManager.triggerEvent(drawAreaEvent);

        verify(mockListener, times(1)).onEventTrigger(endEvent);
        verify(mockListener, times(1)).onEventTrigger(drawAreaEvent);
    }

    @Test
    void testRemoveEventListener() {
        eventManager.addEventListener(GameEventType.CARD_PLACED_EVENT, mockListener);
        eventManager.removeEventListener(GameEventType.CARD_PLACED_EVENT, mockListener);

        GameEvent event = mock(GameEvent.class);
        when(event.getType()).thenReturn(GameEventType.CARD_PLACED_EVENT);
        eventManager.triggerEvent(event);
        verify(mockListener, times(0)).onEventTrigger(event);
    }

    @Test
    void testTriggerEvent() {
        EventListener anotherMockListener = mock(EventListener.class);
        eventManager.addEventListener(GameEventType.CARD_PLACED_EVENT, mockListener);
        eventManager.addEventListener(GameEventType.CARD_PLACED_EVENT, anotherMockListener);

        GameEvent event = mock(GameEvent.class);
        when(event.getType()).thenReturn(GameEventType.CARD_PLACED_EVENT);
        eventManager.triggerEvent(event);

        verify(mockListener, times(1)).onEventTrigger(event);
        verify(anotherMockListener, times(1)).onEventTrigger(event);
    }
}
