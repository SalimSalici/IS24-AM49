package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.model.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.model.actions.GameActionType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.ChoosableObjectivesAssignedEvent;
import it.polimi.ingsw.am49.model.events.CommonObjectivesDrawn;
import it.polimi.ingsw.am49.model.events.GameStateChangedEvent;
import it.polimi.ingsw.am49.model.events.PersonalObjectiveChosenEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.*;

/**
 * Rapresents the game state in witch every player selects his personal objective from a set of options.
 * The choosen objective will be the secret personal objective of the player for ste rest of the match.
 *
 * This class is resposible for dealing the personal {@link ObjectiveCard} options to eatch {@link Player} and
 * handling the chosing process.
 *
 * This class extends {@link GameState} and utilizes events such as {@link ChoosableObjectivesAssignedEvent},
 * {@link CommonObjectivesDrawn}, and {@link PersonalObjectiveChosenEvent} to manage the flow of the game state
 * and communicate state changes.
 */
public class ChooseObjectiveState extends GameState {

    /**
     * Mapps every player to a list of the possible objective cards from witch che can choose.
     */
    private final Map<Player, List<ObjectiveCard>> playersToObjectives;

    /**
     * Constructs a new ChooseObjectiveState for a given game.
     * @param game the game this state is associated with.
     */
    protected ChooseObjectiveState(Game game) {
        super(GameStateType.CHOOSE_OBJECTIVE, game, Set.of(GameActionType.CHOOSE_OBJECTIVE));
        this.playersToObjectives = new HashMap<>();
        this.notYourTurnMessage =
                "You have already choosen your personal objective. You must wait for the other players.";
    }

    /**
     * Draws the common objective to all the players and the personal objective options that are distinct for every player.
     */
    @Override
    public void setUp() {
        GameDeck<ObjectiveCard> objectiveDeck = DeckLoader.getInstance().getNewObjectiveDeck();

        // Draw common objectives

        ObjectiveCard[] commonObjectives = this.game.getCommonObjectives();
        for (int i = 0; i < commonObjectives.length; i++)
            commonObjectives[i] = objectiveDeck.draw();
        this.game.triggerEvent(new CommonObjectivesDrawn(List.of(commonObjectives)));

        // Draw objectives for players to choose from

        for (Player p : this.game.getPlayers()) {
            List<ObjectiveCard> drawnObjectives = new LinkedList<>();
            for (int i = 0; i < 2; i++)
                drawnObjectives.add(objectiveDeck.draw());
            this.playersToObjectives.put(p, drawnObjectives);
        }

        // TODO: change the ChoosableObjectivesAssignedEvent to be specific for each player, instead of aggretating them
        this.game.triggerEvent(new ChoosableObjectivesAssignedEvent(this.playersToObjectives));
        this.game.triggerEvent(new GameStateChangedEvent(this.type, this.game.getTurn(), this.game.getRound()));
    }

    /**
     * Handles the logic to choose witch prsonal objective to pick from the options presented.
     * @param action tells witch type of {@link GameAction} neds to be handled.
     * @throws Exception if the player didn't choose an objetive of the ones dealt to him.
     */
    @Override
    public void execute(GameAction action) throws Exception {
        this.checkActionValidity(action);
        ChooseObjectiveAction chooseObjectiveAction = (ChooseObjectiveAction) action;

        Player player = this.game.getPlayerByUsername(action.getUsername());
        int objectiveId = chooseObjectiveAction.getObjectiveId();
        List<ObjectiveCard> playerObjectives = this.playersToObjectives.get(player);
        ObjectiveCard chosenObjectiveCard = this.getObjectiveById(objectiveId, playerObjectives);

        // TODO: custom exception...
        if (chosenObjectiveCard == null)
            throw new Exception("You must choose one of the objectives that was dealt to you");

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
