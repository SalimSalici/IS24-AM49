package it.polimi.ingsw.am49.server.model.states;

import it.polimi.ingsw.am49.common.actions.ChooseObjectiveAction;
import it.polimi.ingsw.am49.common.actions.GameAction;
import it.polimi.ingsw.am49.common.actions.GameActionType;
import it.polimi.ingsw.am49.server.model.Game;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.server.model.decks.GameDeck;
import it.polimi.ingsw.am49.common.enumerations.GameStateType;
import it.polimi.ingsw.am49.server.model.events.ChoosableObjectivesEvent;
import it.polimi.ingsw.am49.server.model.events.CommonObjectivesDrawnEvent;
import it.polimi.ingsw.am49.server.model.events.GameStateChangedEvent;
import it.polimi.ingsw.am49.server.model.events.PersonalObjectiveChosenEvent;
import it.polimi.ingsw.am49.server.model.players.Player;
import it.polimi.ingsw.am49.common.exceptions.InvalidActionException;
import it.polimi.ingsw.am49.common.exceptions.NotYourTurnException;
import it.polimi.ingsw.am49.common.util.Log;

import java.util.*;

/**
 * Rapresents the game state in witch every player selects his personal objective from a set of options.
 * The choosen objective will be the secret personal objective of the player for ste rest of the match.
 * <p>
 * This class is resposible for dealing the personal {@link ObjectiveCard} options to eatch {@link Player} and
 * handling the chosing process.
 * <p>
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

        this.game.triggerEvent(new GameStateChangedEvent(
                this.type,
                this.game.getCurrentPlayer(),
                this.game.getTurn(),
                this.game.getRound(),
                this.game.isEndGame(),
                this.game.isFinalRound()
        ));

        for (Player p : this.game.getPlayers())
            if (!p.isOnline())
                this.chooseRandomly(p);
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
        if (playerObjectives == null)
            throw new InvalidActionException("You have alredy chosen your objective card.");

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

    /**
     * Handles the disconnection of a player.
     * 
     * @param username the username of the player to be disconnected.
     */
    public void disconnectPlayer(String username) {
        Player player = this.game.getPlayerByUsername(username);
        if (player == null || !player.isOnline()) return;

        player.setIsOnline(false);

        this.chooseRandomly(player);
    }

    /**
     * Chooses an objective card randomly for the given player.
     * This method is used when a player disconnects and cannot choose an objective card manually.
     *
     * @param player the player for whom an objective card is to be chosen randomly.
     */
    private void chooseRandomly(Player player) {
        List<ObjectiveCard> objectives = this.playersToObjectives.get(player);
        if (objectives == null) return;

        int randomObjectiveId = objectives.get(new Random().nextInt(objectives.size())).getId();
        try {
            this.execute(new ChooseObjectiveAction(player.getUsername(), randomObjectiveId));
        } catch (InvalidActionException | NotYourTurnException e) {
            Log.getLogger().severe("Disconnect player anomaly... Exception message: " + e.getMessage());
        }
    }
}
