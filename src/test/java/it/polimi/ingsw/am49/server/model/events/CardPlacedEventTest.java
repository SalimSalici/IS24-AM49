package it.polimi.ingsw.am49.server.model.events;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.polimi.ingsw.am49.common.gameupdates.CardPlacedUpdate;
import it.polimi.ingsw.am49.server.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.server.model.players.BoardTile;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.server.model.players.PlayerBoard;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import org.junit.jupiter.api.Test;

import java.util.Map;

class CardPlacedEventTest {

    @Test
    void testToGameUpdate() {
        // Mock dependencies
        Player mockPlayer = mock(Player.class);
        BoardTile mockBoardTile = mock(BoardTile.class);
        PlaceableCard mockCard = mock(PlaceableCard.class);

        // Stubbing the necessary methods
        when(mockPlayer.getUsername()).thenReturn("Player1");
        when(mockPlayer.getPoints()).thenReturn(15);
        when(mockPlayer.getBoard()).thenReturn(mock(PlayerBoard.class));
        when(mockPlayer.getBoard().getAvailableResources()).thenReturn(Map.of(Symbol.LEAVES, 2));

        when(mockBoardTile.getRow()).thenReturn(3);
        when(mockBoardTile.getCol()).thenReturn(4);
        when(mockBoardTile.getCard()).thenReturn(mockCard);
        when(mockBoardTile.getCard().getId()).thenReturn(100);
        when(mockBoardTile.getCard().isFlipped()).thenReturn(true);

        // Create an event instance
        CardPlacedEvent event = new CardPlacedEvent(mockPlayer, mockBoardTile);

        // Invoke the method under test
        CardPlacedUpdate update = event.toGameUpdate();

        // Assert results
        assertNotNull(update);
        assertEquals("Player1", update.username());
        assertEquals(100, update.cardId());
        assertEquals(3, update.row());
        assertEquals(4, update.col());
        assertTrue(update.flipped());
        assertEquals(Map.of(Symbol.LEAVES, 2), update.activeSymbols());
        assertEquals(15, update.points());
    }
}
