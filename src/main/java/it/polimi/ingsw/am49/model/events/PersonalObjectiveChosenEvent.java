package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.PersonalObjectiveChosenUpdate;
import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

/**
 * Represents an event that occurs when a player chooses their personal objective card.
 *
 * @param player the {@link Player} who has made the choice
 * @param objectiveCard the {@link ObjectiveCard} chosen by the player
 */
public record PersonalObjectiveChosenEvent(Player player, ObjectiveCard objectiveCard) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.PERSONAL_OBJECTIVE_CHOSEN_EVENT;
    }

    @Override
    public GameUpdate toGameUpdate() {
        return new PersonalObjectiveChosenUpdate(player().getUsername(), objectiveCard().getId());
    }
}
