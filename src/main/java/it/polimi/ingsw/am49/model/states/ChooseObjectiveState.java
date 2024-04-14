package it.polimi.ingsw.am49.model.states;

import it.polimi.ingsw.am49.messages.mts.ChooseObjectiveMTS;
import it.polimi.ingsw.am49.messages.mts.MessageToServer;
import it.polimi.ingsw.am49.messages.mts.MessageToServerType;
import it.polimi.ingsw.am49.model.Game;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;
import it.polimi.ingsw.am49.model.enumerations.GameStateType;
import it.polimi.ingsw.am49.model.events.ChoosableObjectivesAssignedEvent;
import it.polimi.ingsw.am49.model.events.GameStateChangedEvent;
import it.polimi.ingsw.am49.model.events.PersonalObjectiveChosenEvent;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.*;

public class ChooseObjectiveState extends GameState {

    private final Map<Player, List<ObjectiveCard>> playersToObjectives;

    protected ChooseObjectiveState(Game game) {
        super(GameStateType.CHOOSE_OBJECTIVE, game, Set.of(MessageToServerType.CHOOSE_OBJECTIVE));
        this.playersToObjectives = new HashMap<>();
        this.notYourTurnMessage =
                "You have already choosen your personal objective. You must wait for the other players.";
    }

    @Override
    public void setUp() {
        GameDeck<ObjectiveCard> objectiveDeck = DeckLoader.getInstance().getNewObjectiveDeck();
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

    @Override
    public void execute(MessageToServer msg) throws Exception {
        this.checkMsgValidity(msg);
        ChooseObjectiveMTS chooseObjectiveMsg = (ChooseObjectiveMTS) msg;

        Player player = this.game.getPlayerByUsername(msg.getUsername());
        int objectiveId = chooseObjectiveMsg.getObjectiveId();
        List<ObjectiveCard> playerObjectives = this.playersToObjectives.get(player);
        ObjectiveCard chosenObjectiveCard = this.getObjectiveById(objectiveId, playerObjectives);

        // TODO: custom exception...
        if (chosenObjectiveCard == null)
            throw new Exception("You must choose one of the objectives that was dealt to you");

        player.setPersonalObjective(chosenObjectiveCard);
        this.playersToObjectives.remove(player);
        this.game.triggerEvent(new PersonalObjectiveChosenEvent(player, chosenObjectiveCard));

        if (this.playersToObjectives.isEmpty()) {
            this.nextState = new PlaceCardState(this.game);
            this.goToNextState();
        }
    }

    @Override
    protected boolean isYourTurn(MessageToServer msg) {
        return this.playersToObjectives.containsKey(this.game.getPlayerByUsername(msg.getUsername()));
    }

    private ObjectiveCard getObjectiveById(int id, List<ObjectiveCard> objectiveCards) {
        for (ObjectiveCard objCard : objectiveCards)
            if (objCard.getId() == id)
                return objCard;
        return null;
    }
}
