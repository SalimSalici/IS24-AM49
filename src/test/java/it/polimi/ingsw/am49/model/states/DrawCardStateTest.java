package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.common.actions.DrawCardAction;
import it.polimi.ingsw.am49.server.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.server.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.server.model.decks.GameDeck;
import it.polimi.ingsw.am49.common.enumerations.DrawPosition;
import it.polimi.ingsw.am49.server.model.events.DrawAreaEvent;
import it.polimi.ingsw.am49.server.model.events.HandEvent;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.server.model.states.DrawCardState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DrawCardStateTest {

    private DrawCardState drawCardState;

    @Mock
    private Game game;
    private Player player1;
    private Player player2;
    @Mock
    private GameDeck<ResourceCard> resourceGameDeck;
    @Mock
    private GameDeck<GoldCard> goldGameDeck;
    @Mock
    private ResourceCard revealedCardResource;
    @Mock
    private GoldCard revealedCardGold;
    private ResourceCard[] revealedResources;
    private GoldCard[] revealedGolds;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        game = mock(Game.class);
        player1 = new Player("player1");
        player2 = new Player("player2");

        when(game.getCurrentPlayer()).thenReturn(player1);
        when(game.getResourceGameDeck()).thenReturn(resourceGameDeck);
        when(game.getGoldGameDeck()).thenReturn(goldGameDeck);
        when(game.getRevealedResources()).thenReturn(new ResourceCard[2]);
        when(game.getRevealedGolds()).thenReturn(new GoldCard[2]);

        when(revealedCardResource.getId()).thenReturn(3);
        when(revealedCardGold.getId()).thenReturn(4);
        revealedResources = new ResourceCard[]{revealedCardResource, null};
        when(game.getRevealedResources()).thenReturn(revealedResources);
        revealedGolds = new GoldCard[]{null, null};
        when(game.getRevealedGolds()).thenReturn(revealedGolds);

        drawCardState = new DrawCardState(game);
    }

    @Test
    void testExecuteDrawFromResourceDeck() throws NotYourTurnException, InvalidActionException {
        ResourceCard drawnCard = mock(ResourceCard.class);
        when(resourceGameDeck.draw()).thenReturn(drawnCard);
        when(resourceGameDeck.size()).thenReturn(1);
        when(goldGameDeck.size()).thenReturn(1);

        DrawCardAction action = new DrawCardAction(player1.getUsername(), DrawPosition.RESOURCE_DECK, 0);
        drawCardState.execute(action);

        verify(resourceGameDeck).draw();
        assertEquals(1, player1.getHand().size());
        assertTrue(player1.getHand().contains(drawnCard));
        verify(game).triggerEvent(any(DrawAreaEvent.class));
        verify(game).triggerEvent(any(HandEvent.class));
        verify(game).handleSwitchToNextTurn();
    }

    @Test
    void testExecuteDrawFromGoldDeck() throws NotYourTurnException, InvalidActionException {
        GoldCard drawnCard = mock(GoldCard.class);
        when(goldGameDeck.draw()).thenReturn(drawnCard);
        when(resourceGameDeck.size()).thenReturn(1);
        when(goldGameDeck.size()).thenReturn(1);

        DrawCardAction action = new DrawCardAction(player1.getUsername(), DrawPosition.GOLD_DECK, 0);
        drawCardState.execute(action);

        verify(goldGameDeck).draw();
        assertEquals(1, player1.getHand().size());
        assertTrue(player1.getHand().contains(drawnCard));
        verify(game).triggerEvent(any(DrawAreaEvent.class));
        verify(game).triggerEvent(any(HandEvent.class));
        verify(game).handleSwitchToNextTurn();
    }

    @Test
    void testExecuteNonExistentRevealed() {
        DrawCardAction action = new DrawCardAction(player1.getUsername(), DrawPosition.REVEALED, 2);

        assertThrows(InvalidActionException.class, () -> drawCardState.execute(action));

        assertEquals(0, player1.getHand().size());
        verify(game, never()).triggerEvent(any(DrawAreaEvent.class));
        verify(game, never()).triggerEvent(any(HandEvent.class));
        verify(game, never()).handleSwitchToNextTurn();
    }

    @Test
    void testExecuteDrawFromRevealedResource() throws NotYourTurnException, InvalidActionException {
        when(resourceGameDeck.draw()).thenReturn(mock(ResourceCard.class));
        when(resourceGameDeck.size()).thenReturn(1);
        when(goldGameDeck.size()).thenReturn(1);

        DrawCardAction action = new DrawCardAction(player1.getUsername(), DrawPosition.REVEALED, 3);
        drawCardState.execute(action);

        assertEquals(1, player1.getHand().size());
        assertTrue(player1.getHand().contains(revealedCardResource));
        assertNotNull(revealedResources[0]);
        verify(game).triggerEvent(any(DrawAreaEvent.class));
        verify(game).triggerEvent(any(HandEvent.class));
        verify(game).handleSwitchToNextTurn();
    }

    @Test
    void testExecuteDrawFromRevealedGold() throws NotYourTurnException, InvalidActionException {
        GoldCard goldCard = mock(GoldCard.class);
        when(goldCard.getId()).thenReturn(7);
        revealedGolds = new GoldCard[]{null, goldCard};
        when(game.getRevealedGolds()).thenReturn(revealedGolds);

        drawCardState = new DrawCardState(game);

        when(goldGameDeck.draw()).thenReturn(mock(GoldCard.class));
        when(goldGameDeck.size()).thenReturn(1);
        when(goldGameDeck.size()).thenReturn(1);

        DrawCardAction action = new DrawCardAction(player1.getUsername(), DrawPosition.REVEALED, 7);
        drawCardState.execute(action);

        assertEquals(1, player1.getHand().size());
        assertTrue(player1.getHand().contains(goldCard));
        verify(game).triggerEvent(any(DrawAreaEvent.class));
        verify(game).triggerEvent(any(HandEvent.class));
        verify(game).handleSwitchToNextTurn();
    }

    @Test
    void testExecuteDrawFromEmptyDeck() {
        when(resourceGameDeck.size()).thenReturn(0);
        DrawCardAction action = new DrawCardAction(player1.getUsername(), DrawPosition.RESOURCE_DECK, 0);

        assertThrows(InvalidActionException.class, () -> drawCardState.execute(action));
    }

    @Test
    void testDisconnectCurrentPlayer() {
        when(resourceGameDeck.isEmpty()).thenReturn(true);
        when(goldGameDeck.isEmpty()).thenReturn(true);
        when(game.getPlayerByUsername(player1.getUsername())).thenReturn(player1);

        drawCardState.disconnectPlayer(player1.getUsername());

        assertFalse(player1.isOnline());
        assertEquals(1, player1.getHand().size());
        assertTrue(player1.getHand().contains(revealedCardResource));
        verify(game).triggerEvent(any(DrawAreaEvent.class));
        verify(game).triggerEvent(any(HandEvent.class));
        verify(game).handleSwitchToNextTurn();
    }

    @Test
    void testDisconnectNonCurrentPlayer() {
        when(game.getPlayerByUsername(player2.getUsername())).thenReturn(player2);

        drawCardState.disconnectPlayer(player2.getUsername());

        assertFalse(player2.isOnline());
        verify(resourceGameDeck, never()).draw();
        verify(goldGameDeck, never()).draw();
        verify(game, never()).triggerEvent(any());
        verify(game, never()).handleSwitchToNextTurn();
    }
}