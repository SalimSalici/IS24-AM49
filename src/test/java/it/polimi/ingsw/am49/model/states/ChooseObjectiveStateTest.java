package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.server.controller.VirtualView;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.common.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.common.actions.ChooseStarterSideAction;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.events.ChoosableObjectivesEvent;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChooseObjectiveStateTest {

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
        startAndSkipToChooseObjectiveState(game);

        ArgumentCaptor<ChoosableObjectivesEvent> captor = ArgumentCaptor.forClass(ChoosableObjectivesEvent.class);
        verify(virtualView, times(2)).onEventTrigger(captor.capture());

        for (ChoosableObjectivesEvent event : captor.getAllValues())
            playersToAssignedObjective.put(event.player(), event.objectiveCards());
    }

    @Test
    void testExecute() throws InvalidActionException, NotYourTurnException {
        List<ObjectiveCard> objectives1 = playersToAssignedObjective.get(player1);
        ChooseObjectiveAction invalidAction1 = new ChooseObjectiveAction(player1.getUsername(), -1);
        assertThrows(InvalidActionException.class, () -> game.executeAction(invalidAction1));
        ChooseObjectiveAction validAction1 = new ChooseObjectiveAction(player1.getUsername(), objectives1.getFirst().getId());
        game.executeAction(validAction1);

        assertThrows(NotYourTurnException.class, () -> game.executeAction(validAction1));

        List<ObjectiveCard> objectives2 = playersToAssignedObjective.get(player2);
        ChooseObjectiveAction invalidAction2 = new ChooseObjectiveAction(player2.getUsername(), -1);
        assertThrows(InvalidActionException.class, () -> game.executeAction(invalidAction2));
        ChooseObjectiveAction validAction2 = new ChooseObjectiveAction(player2.getUsername(), objectives2.getFirst().getId());
        game.executeAction(validAction2);
    }

    @Test
    void testPlayerDisconnected() {
        game.disconnectPlayer(player1.getUsername());
        assertFalse(player1.isOnline());
        assertTrue(playersToAssignedObjective.get(player1).contains(player1.getPersonalObjective()));
    }

    void startAndSkipToChooseObjectiveState(Game game) throws InvalidActionException, NotYourTurnException {
        VirtualView virtualView = mock(VirtualView.class);
        game.addEventListener(GameEventType.CHOOSABLE_OBJECTIVES_EVENT, virtualView);
        game.startGame();

        for (Player p : game.getPlayers())
            game.executeAction(new ChooseStarterSideAction(p.getUsername(), false));
    }

}