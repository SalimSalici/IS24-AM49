package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.ChoosableObjectivesUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * This event is triggered when a player receives a list of objective cards from which
 * they can choose their personal objective
 *
 * @param player the {@link Player} receiving the cards
 * @param objectiveCards the list of {@link ObjectiveCard} to choose from
 */
public record ChoosableObjectivesEvent(
        Player player,
        List<ObjectiveCard> objectiveCards
) implements GameEvent {

    public ChoosableObjectivesEvent(Player player, List<ObjectiveCard> objectiveCards) {
        this.player = player;
        this.objectiveCards = new LinkedList<>(objectiveCards);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.CHOOSABLE_OBJECTIVES_EVENT;
    }

    @Override
    public ChoosableObjectivesUpdate toGameUpdate() {
        return new ChoosableObjectivesUpdate(player.getUsername(), objectiveCards.stream().map(Card::getId).toList());
    }
}
