package it.polimi.ingsw.am49.model.decks;

import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CardStackTest {

    private final CardStack<ResourceCard> cardStack = new CardStack<>();

    @Test
    void push() {
        ResourceCard mockCard10 = mock(ResourceCard.class);
        when(mockCard10.getId()).thenReturn(10);
        cardStack.push(mockCard10);

        assertEquals(cardStack.size(), 1);
        assertEquals(cardStack.peek().getId(), 10);

        ResourceCard mockCard20 = mock(ResourceCard.class);
        when(mockCard20.getId()).thenReturn(20);
        cardStack.push(mockCard20);

        assertEquals(cardStack.size(), 2);
        assertEquals(cardStack.peek().getId(), 20);
    }

    @Test
    void pop() {
        assertNull(cardStack.pop());

        ResourceCard mockCard10 = mock(ResourceCard.class);
        ResourceCard mockCard20 = mock(ResourceCard.class);
        when(mockCard10.getId()).thenReturn(10);
        when(mockCard20.getId()).thenReturn(20);
        cardStack.push(mockCard10);
        cardStack.push(mockCard20);

        assertEquals(cardStack.pop().getId(), 20);
        assertEquals(cardStack.pop().getId(), 10);
        assertNull(cardStack.pop());
    }

    @Test
    void peek() {
        assertNull(cardStack.peek());

        ResourceCard mockCard10 = mock(ResourceCard.class);
        ResourceCard mockCard20 = mock(ResourceCard.class);
        when(mockCard10.getId()).thenReturn(10);
        when(mockCard20.getId()).thenReturn(20);
        cardStack.push(mockCard10);
        cardStack.push(mockCard20);

        assertEquals(cardStack.peek().getId(), 20);
        cardStack.pop();
        assertEquals(cardStack.peek().getId(), 10);
        cardStack.pop();
        assertNull(cardStack.pop());
    }

    @Test
    void isEmpty() {
        assertTrue(cardStack.isEmpty());

        ResourceCard mockCard10 = mock(ResourceCard.class);
        ResourceCard mockCard20 = mock(ResourceCard.class);
        when(mockCard10.getId()).thenReturn(10);
        when(mockCard20.getId()).thenReturn(20);

        cardStack.push(mockCard10);
        assertFalse(cardStack.isEmpty());

        cardStack.push(mockCard20);
        assertFalse(cardStack.isEmpty());

        cardStack.pop();
        assertFalse(cardStack.isEmpty());

        cardStack.peek();
        assertFalse(cardStack.isEmpty());

        cardStack.pop();
        assertTrue(cardStack.isEmpty());
    }

    @Test
    void size() {
        assertEquals(cardStack.size(), 0);

        ResourceCard mockCard10 = mock(ResourceCard.class);
        ResourceCard mockCard20 = mock(ResourceCard.class);
        when(mockCard10.getId()).thenReturn(10);
        when(mockCard20.getId()).thenReturn(20);

        cardStack.push(mockCard10);
        assertEquals(cardStack.size(), 1);

        cardStack.push(mockCard20);
        assertEquals(cardStack.size(), 2);

        cardStack.peek();
        assertEquals(cardStack.size(), 2);

        cardStack.pop();
        assertEquals(cardStack.size(), 1);
        cardStack.pop();
        assertEquals(cardStack.size(), 0);
        cardStack.pop();
        assertEquals(cardStack.size(), 0);
    }
}