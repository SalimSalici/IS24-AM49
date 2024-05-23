package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.ChoosableObjectivesEvent;
import it.polimi.ingsw.am49.model.events.CommonObjectivesDrawnEvent;
import it.polimi.ingsw.am49.model.events.GameStateChangedEvent;
import it.polimi.ingsw.am49.model.events.PersonalObjectiveChosenEvent;
import it.polimi.ingsw.am49.model.players.Player;
import it.polimi.ingsw.am49.server.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.server.exceptions.NotYourTurnException;

import java.util.*;

/**
 * Rapresents the game state in witch every player selects his personal objective from a set of options.
 * The choosen objective will be the secret personal objective of the player for ste rest of the match.
 *
 * This class is resposible for dealing the personal {@link ObjectiveCard} options to eatch {@link Player} and
 * handling the chosing process.
 *
 * This class extends {@link GameState} and utilizes events such as {@link ChoosableObjectivesEvent},
 * {@link CommonObjectivesDrawnEvent}, and {@link PersonalObjectiveChosenEvent} to manage the flow of the game state
 * and communicate state changes.
 */
public class ChooseObjectiveState extends GameState {

    /**
     * Mapps every player to a list of the possible objective cards from witch che can choose.
     */
    private final Map<Player, List<ObjectiveCard>> playersToObjectives;

    private final GameDeck<ObjectiveCard> objectiveDeck;

    /**
     * Constructs a new ChooseObjectiveState for a given game.
     * @param game the game this state is associated with.
     */
    protected ChooseObjectiveState(Game game, GameDeck<ObjectiveCard> objectiveDeck) {
        super(GameStateType.CHOOSE_OBJECTIVE, game, Set.of(GameActionType.CHOOSE_OBJECTIVE));
        this.playersToObjectives = new HashMap<>();
        this.notYourTurnMessage = "You have already choosen your personal objective. You must wait for the other players.";
        this.objectiveDeck = objectiveDeck;
    }

    /**
     * Draws the common objective to all the players and the personal objective options that are distinct for every player.
     */
    @Override
    public void setUp() {

        // Draw objectives for players to choose from
        for (Player p : this.game.getPlayers()) {
            List<ObjectiveCard> drawnObjectives = new LinkedList<>();
            for (int i = 0; i < 2; i++)
                drawnObjectives.add(this.objectiveDeck.draw());
            this.playersToObjectives.put(p, drawnObjectives);
            this.game.triggerEvent(new ChoosableObjectivesEvent(p, drawnObjectives));
        }

        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound(), this.game.getCurrentPlayer()));
    }

    /**
     * Handles the logic to choose witch prsonal objective to pick from the options presented.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws InvalidActionException if the player didn't choose an objetive of the ones dealt to him.
     * @throws NotYourTurnException if the player making the action is not the current player.
     */
    @Override
    public void execute(GameAction action) throws InvalidActionException, NotYourTurnException {
        this.checkActionValidity(action);
        ChooseObjectiveAction chooseObjectiveAction = (ChooseObjectiveAction) action;

        Player player = this.game.getPlayerByUsername(action.getUsername());
        int objectiveId = chooseObjectiveAction.getObjectiveId();
        List<ObjectiveCard> playerObjectives = this.playersToObjectives.get(player);
        ObjectiveCard chosenObjectiveCard = this.getObjectiveById(objectiveId, playerObjectives);

        if (chosenObjectiveCard == null)
            throw new InvalidActionException("You must choose one of the objectives that was dealt to you.");

        player.setPersonalObjective(chosenObjectiveCard);
        this.playersToObjectives.remove(player);
        this.game.triggerEvent(new PersonalObjectiveChosenEvent(player, chosenObjectiveCard));

        if (this.playersToObjectives.isEmpty())
            this.goToNextState(new PlaceCardState(this.game));
    }

    @Override
    protected boolean isYourTurn(GameAction action) {
        return this.playersToObjectives.containsKey(this.game.getPlayerByUsername(action.getUsername()));
    }

    /**
     * Given the id of an ObjectiveCard provides the {@link ObjectiveCard}.
     * @param id identifies the card.
     * @param objectiveCards list of the {@link ObjectiveCard}s from witch to extract the one with the provided id.
     * @return the {@link ObjectiveCard} with the provided id.
     */
    private ObjectiveCard getObjectiveById(int id, List<ObjectiveCard> objectiveCards) {
        for (ObjectiveCard objCard : objectiveCards)
            if (objCard.getId() == id)
                return objCard;
        return null;
    }
}
