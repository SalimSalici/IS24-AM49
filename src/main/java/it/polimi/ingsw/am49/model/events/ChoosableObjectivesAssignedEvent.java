package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
