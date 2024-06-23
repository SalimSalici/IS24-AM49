package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.events.EndgameEvent;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.model.states.EndGameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndGameStateTest {

    private EndGameState endGameState;
    private Game game;
    private Player player1;
    private Player player2;
    private ObjectiveCard commonObjective1;
    private ObjectiveCard commonObjective2;

    @BeforeEach
    void setUp() {
        game = mock(Game.class);
        player1 = mock(Player.class);
        player2 = mock(Player.class);
        commonObjective1 = mock(ObjectiveCard.class);
        commonObjective2 = mock(ObjectiveCard.class);

        List<Player> players = Arrays.asList(player1, player2);
        when(game.getPlayers()).thenReturn(players);
        when(game.getCommonObjectives()).thenReturn(new ObjectiveCard[]{commonObjective1, commonObjective2});

        endGameState = new EndGameState(game);
    }

    @Test
    void testSetUp() {
        when(player1.calculateFinalPoints(any())).thenReturn(2);
        when(player2.calculateFinalPoints(any())).thenReturn(1);

        endGameState.setUp();

        verify(player1).calculateFinalPoints(Arrays.asList(commonObjective1, commonObjective2));
        verify(player2).calculateFinalPoints(Arrays.asList(commonObjective1, commonObjective2));

        ArgumentCaptor<EndgameEvent> eventCaptor = ArgumentCaptor.forClass(EndgameEvent.class);
        verify(game).triggerEvent(eventCaptor.capture());

        EndgameEvent capturedEvent = eventCaptor.getValue();
        Map<Player, Integer> playersToAchievedObjectives = capturedEvent.playersToAchievedObjectives();
        assertEquals(2, playersToAchievedObjectives.size());
        assertEquals(2, playersToAchievedObjectives.get(player1));
        assertEquals(1, playersToAchievedObjectives.get(player2));
        assertNull(capturedEvent.forfeitWinner());
    }

    @Test
    void testSetUpWithForfeitWinner() {
        Player forfeitWinner = mock(Player.class);
        endGameState = new EndGameState(game, forfeitWinner);

        when(player1.calculateFinalPoints(any())).thenReturn(2);
        when(player2.calculateFinalPoints(any())).thenReturn(1);

        endGameState.setUp();

        ArgumentCaptor<EndgameEvent> eventCaptor = ArgumentCaptor.forClass(EndgameEvent.class);
        verify(game).triggerEvent(eventCaptor.capture());

        EndgameEvent capturedEvent = eventCaptor.getValue();
        assertEquals(forfeitWinner, capturedEvent.forfeitWinner());
    }

    @Test
    void testExecute() {
        assertThrows(InvalidActionException.class, () -> endGameState.execute(mock(GameAction.class)));
    }

    @Test
    void testDisconnectPlayer() {
        endGameState.disconnectPlayer("testPlayer");
    }

    @Test
    void testGetType() {
        assertEquals(GameStateType.END_GAME, endGameState.getType());
    }
}