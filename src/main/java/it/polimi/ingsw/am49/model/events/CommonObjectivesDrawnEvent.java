package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;

import java.util.List;

/**
 * Represents an event that notifies when common objectives are drawn for the game.
 * These objectives are visible and applicable to all players.
 *
 * @param commonObjectives a list of {@link ObjectiveCard} objects representing the common objectives drawn
 */
public record CommonObjectivesDrawnEvent(List<ObjectiveCard> commonObjectives) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.COMMON_OBJECTIVES_DRAWN;
    }

    @Override
    public GameUpdate toGameUpdate() {
        return null;
    }
}
