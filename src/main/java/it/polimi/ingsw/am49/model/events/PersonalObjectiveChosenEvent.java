package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

public record PersonalObjectiveChosenEvent(Player player, ObjectiveCard objectiveCard) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.PERSONAL_OBJECTIVE_CHOSEN_EVENT;
    }
}
