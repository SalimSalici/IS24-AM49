package it.polimi.ingsw.am49.model;

import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.ChoosableObjectivesEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    private Game game;

    @Mock
    private Player player1;
    @Mock
    private Player player2;
    @Mock
    private Player player3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(player1.getUsername()).thenReturn("player1");
        when(player1.getColor()).thenReturn(Color.RED);
        when(player1.getPersonalObjective()).thenReturn(DeckLoader.getInstance().getNewObjectiveCardById(91));
        when(player2.getUsername()).thenReturn("player2");
        when(player2.getColor()).thenReturn(Color.BLUE);
        when(player2.getPersonalObjective()).thenReturn(DeckLoader.getInstance().getNewObjectiveCardById(92));
        when(player3.getUsername()).thenReturn("player3");
        when(player3.getColor()).thenReturn(Color.YELLOW);
        when(player3.getPersonalObjective()).thenReturn(DeckLoader.getInstance().getNewObjectiveCardById(93));

        game = new Game(4);
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);

        assertEquals(4, game.getNumPlayers());
    }

    @Test
    void testAddPlayer() {
        Game game = new Game(3);

        game.addPlayer("player1", Color.RED);
        assertEquals(1, game.getPlayers().size());
        assertEquals(game.getPlayers().getLast().getColor(), Color.RED);

        game.addPlayer("player2", Color.BLUE);
        assertEquals(2, game.getPlayers().size());
        assertEquals(game.getPlayers().getLast().getColor(), Color.BLUE);

        game.addPlayer("player3", Color.YELLOW);
        assertEquals(3, game.getPlayers().size());
        assertEquals(game.getPlayers().getLast().getColor(), Color.YELLOW);
        assertEquals(game.getPlayers().getFirst().getColor(), Color.RED);
    }

    @Test
    void testStartGame() {
        game.startGame();
        assertEquals(GameStateType.CHOOSE_STARTER_SIDE, game.getGameState().getType());
        assertFalse(game.isEndGame());
        assertFalse(game.isFinalRound());
    }

    @Test
    void testGetPlayerByUsername() {
        assertEquals(Color.RED, game.getPlayerByUsername("player1").getColor());
        assertEquals(Color.BLUE, game.getPlayerByUsername("player2").getColor());
        assertEquals(Color.YELLOW, game.getPlayerByUsername("player3").getColor());
        assertNull(game.getPlayerByUsername("qwerty"));
    }

    @Test
    void testHandleSwitchToNextTurn() throws InvalidActionException, NotYourTurnException {
        Game game = new Game(3);

        game.addPlayer("player1", Color.RED);
        game.addPlayer("player2", Color.BLUE);
        game.addPlayer("player3", Color.YELLOW);

        startAndSkipToPlaceCardState(game);
        assertEquals(GameStateType.PLACE_CARD, game.getGameState().getType());

        Player starter = game.getStartingPlayer();
        Player second = game.getNextPlayer();
        Player last = game.getLastPlayer();

        assertEquals(starter, game.getCurrentPlayer());
        game.handleSwitchToNextTurn();
        assertEquals(second, game.getCurrentPlayer());
        game.handleSwitchToNextTurn();
        assertEquals(last, game.getCurrentPlayer());

        game.handleSwitchToNextTurn();
        game.setEndGame(true);
        game.setFinalRound(true);

        assertNotEquals(GameStateType.END_GAME, game.getGameState().getType());
        game.handleSwitchToNextTurn();
        assertNotEquals(GameStateType.END_GAME, game.getGameState().getType());
        game.handleSwitchToNextTurn();
        assertNotEquals(GameStateType.END_GAME, game.getGameState().getType());
        game.handleSwitchToNextTurn();
        assertEquals(GameStateType.END_GAME, game.getGameState().getType());
    }

    @Test
    void testReconnectPlayer() throws InvalidActionException, NotYourTurnException {
        Game game = new Game(3);

        game.addPlayer("player1", Color.RED);
        game.addPlayer("player2", Color.BLUE);
        game.addPlayer("player3", Color.YELLOW);

        Player firstPlayer = game.getPlayers().getFirst();

        startAndSkipToPlaceCardState(game);

        assertFalse(game.reconnectPlayer("qwerty"));
        assertTrue(firstPlayer.isOnline());
        assertFalse(game.reconnectPlayer(firstPlayer.getUsername()));

        game.disconnectPlayer(firstPlayer.getUsername());
        assertFalse(firstPlayer.isOnline());

        assertTrue(game.reconnectPlayer(firstPlayer.getUsername()));
        assertTrue(firstPlayer.isOnline());
    }

    @Test
    void testForfeitWinner() throws InvalidActionException, NotYourTurnException {
        Game game = new Game(3);

        game.addPlayer("player1", Color.RED);
        game.addPlayer("player2", Color.BLUE);
        game.addPlayer("player3", Color.YELLOW);

        Player firstPlayer = game.getPlayers().getFirst();

        startAndSkipToPlaceCardState(game);

        game.forfeitWinner(firstPlayer.getUsername());
        assertEquals(GameStateType.END_GAME, game.getGameState().getType());
    }

    @Test
    void testEventListeners() throws InvalidActionException, NotYourTurnException {
        VirtualView virtualView = mock(VirtualView.class);
        Game game = new Game(3);
        game.addEventListener(GameEventType.CARD_PLACED_EVENT, virtualView);

        game.addPlayer("player1", Color.RED);
        game.addPlayer("player2", Color.BLUE);
        game.addPlayer("player3", Color.YELLOW);

        game.startGame();

        game.executeAction(new ChooseStarterSideAction(game.getCurrentPlayer().getUsername(), true));
        game.removeEventListener(GameEventType.CARD_PLACED_EVENT, virtualView);
        game.executeAction(new ChooseStarterSideAction(game.getNextPlayer().getUsername(), true));

        verify(virtualView, times(1)).onEventTrigger(any());
    }

    void startAndSkipToPlaceCardState(Game game) throws InvalidActionException, NotYourTurnException {
        VirtualView virtualView = mock(VirtualView.class);
        game.addEventListener(GameEventType.CHOOSABLE_OBJECTIVES_EVENT, virtualView);
        game.startGame();

        for (Player p : game.getPlayers())
            game.executeAction(new ChooseStarterSideAction(p.getUsername(), false));

        ArgumentCaptor<ChoosableObjectivesEvent> captor = ArgumentCaptor.forClass(ChoosableObjectivesEvent.class);

        verify(virtualView, times(3)).onEventTrigger(captor.capture());

        for (ChoosableObjectivesEvent event : captor.getAllValues()) {
            game.executeAction(new ChooseObjectiveAction(event.player().getUsername(), event.objectiveCards().getFirst().getId()));
        }
    }
}