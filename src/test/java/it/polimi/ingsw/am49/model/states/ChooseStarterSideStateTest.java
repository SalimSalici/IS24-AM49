package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.actions.DrawCardAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard;
import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.DrawPosition;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.events.*;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.model.players.PlayerBoard;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChooseStarterSideStateTest {

    @Mock
    private Game game;

    @Mock
    private VirtualView virtualView;

    @Mock
    private GameDeck<ResourceCard> resourceDeck;

    @Mock
    private GameDeck<GoldCard> goldDeck;

    @Mock
    private GameDeck<ObjectiveCard> objectiveDeck;

    @Mock
    private GameDeck<StarterCard> starterDeck;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @InjectMocks
    private ChooseStarterSideState chooseStarterSideState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<Player> players = new ArrayList<>(Arrays.asList(player1, player2));
        when(game.getPlayers()).thenReturn(players);
        when(game.getStartingPlayer()).thenReturn(player1);
        when(game.getCommonObjectives()).thenReturn(new ObjectiveCard[2]);
        when(game.getResourceGameDeck()).thenReturn(resourceDeck);
        when(game.getGoldGameDeck()).thenReturn(goldDeck);

        when(resourceDeck.draw()).thenReturn(mock(ResourceCard.class));
        when(goldDeck.draw()).thenReturn(mock(GoldCard.class));

        when(game.getPlayerByUsername("player1")).thenReturn(player1);
        when(game.getPlayerByUsername("player2")).thenReturn(player2);
        when(player1.isOnline()).thenReturn(true);
        when(player2.isOnline()).thenReturn(true);

        PlayerBoard mockBoard = mock(PlayerBoard.class);
        when(mockBoard.getStarterTile()).thenReturn(mock(BoardTile.class));
        when(player1.getBoard()).thenReturn(mockBoard);
        when(player2.getBoard()).thenReturn(mockBoard);

        try (MockedStatic<DeckLoader> mockedDeckLoader = mockStatic(DeckLoader.class)) {
            DeckLoader deckLoaderInstance = mock(DeckLoader.class);
            mockedDeckLoader.when(DeckLoader::getInstance).thenReturn(deckLoaderInstance);
            when(deckLoaderInstance.getNewObjectiveDeck()).thenReturn(objectiveDeck);
            when(deckLoaderInstance.getNewStarterDeck()).thenReturn(starterDeck);

            chooseStarterSideState = new ChooseStarterSideState(game);
        }

        doAnswer(invocation -> {
            GameEvent event = invocation.getArgument(0);
            virtualView.onEventTrigger(event);
            return null;
        }).when(game).triggerEvent(any(GameEvent.class));

        chooseStarterSideState = new ChooseStarterSideState(game);
    }

    @Test
    void testSetUpMethod() {
        StarterCard starterCard = mock(StarterCard.class);
        when(starterDeck.draw()).thenReturn(starterCard);

        chooseStarterSideState.setUp();

        verify(game).triggerEvent(any(PlayersOrderEvent.class));
        verify(game).triggerEvent(any(CommonObjectivesDrawnEvent.class));
        verify(game, times(2)).triggerEvent(any(StarterCardAssignedEvent.class));
        verify(game).triggerEvent(any(GameStateChangedEvent.class));
    }

    @Test
    void testExecuteActionValid() throws InvalidActionException, NotYourTurnException {
        PlayerBoard mockBoard = mock(PlayerBoard.class);
        when(mockBoard.getStarterTile()).thenReturn(mock(BoardTile.class));
        when(player1.getBoard()).thenReturn(mockBoard);

        chooseStarterSideState.execute(new ChooseStarterSideAction("player1", true));

        verify(player1).chooseStarterSide(true);
        verify(game).triggerEvent(any(CardPlacedEvent.class));
    }

    @Test
    void testExecuteActionNotValid() {
        GameAction invalidAction = new DrawCardAction(player1.getUsername(), DrawPosition.RESOURCE_DECK, 0);
        assertThrows(InvalidActionException.class, () -> chooseStarterSideState.execute(invalidAction));
    }

    @Test
    void disconnectPlayer() throws InvalidActionException, NotYourTurnException {
        chooseStarterSideState.disconnectPlayer("player1");
        verify(player1).setIsOnline(false);
        verify(player1).chooseStarterSide(any(Boolean.class));
    }

    @Test
    void testAssignInitialHand() throws InvalidActionException, NotYourTurnException {
        List<PlaceableCard> mockHand = mock(List.class);
        when(player1.getHand()).thenReturn(mockHand);
        when(player2.getHand()).thenReturn(mockHand);

        ResourceCard mockRes = mock(ResourceCard.class);
        GoldCard mockGold = mock(GoldCard.class);

        when(mockRes.getResource()).thenReturn(Resource.LEAVES);
        when(mockGold.getResource()).thenReturn(Resource.LEAVES);

        when(resourceDeck.peek()).thenReturn(mockRes);
        when(goldDeck.peek()).thenReturn(mockGold);
        when(game.getRevealedResources()).thenReturn(new ResourceCard[0]);
        when(game.getRevealedGolds()).thenReturn(new GoldCard[0]);

        chooseStarterSideState.execute(new ChooseStarterSideAction("player1", true));
        chooseStarterSideState.execute(new ChooseStarterSideAction("player2", true));

        verify(mockHand, times(6)).add(any(PlaceableCard.class));
    }
}
