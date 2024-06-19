package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.model.actions.PlaceCardAction;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.CornerPosition;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.ChoosableObjectivesEvent;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaceCardStateTest {

    private PlaceCardState placeCardState;
    private Game game = new Game(2);
    private Player player1 = new Player("player1");
    private Player player2 = new Player("player2");
    private VirtualView virtualView = mock(VirtualView.class);

    private Map<Player, List<ObjectiveCard>> playersToAssignedObjective = new HashMap<>();

    @BeforeEach
    void setUp() throws InvalidActionException, NotYourTurnException {
        player1.setColor(Color.RED);
        player2.setColor(Color.BLUE);
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addEventListener(GameEventType.CHOOSABLE_OBJECTIVES_EVENT, virtualView);
        startAndSkipToPlaceCardState(game);

        ArgumentCaptor<ChoosableObjectivesEvent> captor = ArgumentCaptor.forClass(ChoosableObjectivesEvent.class);
        verify(virtualView, times(2)).onEventTrigger(captor.capture());

        for (ChoosableObjectivesEvent event : captor.getAllValues())
            playersToAssignedObjective.put(event.player(), event.objectiveCards());

        assertEquals(GameStateType.PLACE_CARD, game.getGameState().getType());
        placeCardState = (PlaceCardState) game.getGameState();
    }

    @Test
    void testExecute() throws InvalidActionException, NotYourTurnException {
        assertThrows(InvalidActionException.class, () -> placeCardState.execute(new ChooseStarterSideAction("qwert", true)));
        Player currentPlayer = game.getCurrentPlayer();
        BoardTile starterTile = currentPlayer.getBoard().getStarterTile();

        placeCardState.execute(new PlaceCardAction(
                currentPlayer.getUsername(),
                currentPlayer.getHand().getFirst().getId(),
                starterTile.getRow(),
                starterTile.getCol(),
                CornerPosition.TOP_RIGHT,
                true
        ));

        assertEquals(2, currentPlayer.getBoard().getPlacementOrder().size());
    }

    @Test
    void testDisconnectPlayer() {
        Player current = game.getCurrentPlayer().equals(player1) ? player1 : player2;
        Player other = game.getCurrentPlayer().equals(player1) ? player2 : player1;
        placeCardState.disconnectPlayer(current.getUsername());
        assertFalse(current.isOnline());
        assertEquals(other, game.getCurrentPlayer());
    }

    void startAndSkipToPlaceCardState(Game game) throws InvalidActionException, NotYourTurnException {
        VirtualView virtualView = mock(VirtualView.class);
        game.addEventListener(GameEventType.CHOOSABLE_OBJECTIVES_EVENT, virtualView);
        game.startGame();

        for (Player p : game.getPlayers())
            game.executeAction(new ChooseStarterSideAction(p.getUsername(), true));

        ArgumentCaptor<ChoosableObjectivesEvent> captor = ArgumentCaptor.forClass(ChoosableObjectivesEvent.class);

        verify(virtualView, times(game.getPlayers().size())).onEventTrigger(captor.capture());

        for (ChoosableObjectivesEvent event : captor.getAllValues())
            game.executeAction(new ChooseObjectiveAction(event.player().getUsername(), event.objectiveCards().getFirst().getId()));
    }
}