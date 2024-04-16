package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an event that notifies players about the personal objectives from which they can choose.
 * Each player is associated with a list of potential ObjectiveCards to select from.
 *
 * @param playersToObjectives a map linking each player to a list of {@link ObjectiveCard} objectives they can select
 *                            as their personal objectives
 */
public record ChoosableObjectivesAssignedEvent(
        Map<Player, List<ObjectiveCard>> playersToObjectives
) implements GameEvent {

    public ChoosableObjectivesAssignedEvent(Map<Player, List<ObjectiveCard>> playersToObjectives) {
        this.playersToObjectives = new HashMap<>(playersToObjectives);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.CHOOSABLE_OBJECTIVES_ASSIGNED_EVENT;
    }
}
