package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.List;

public record CommonObjectivesDrawn(List<ObjectiveCard> commonObjectives) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.COMMON_OBJECTIVES_DRAWN;
    }
}
