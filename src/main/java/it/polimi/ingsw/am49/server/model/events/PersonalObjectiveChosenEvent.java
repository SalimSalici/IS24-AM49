package it.polimi.ingsw.am49.server.model.events;

import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.common.gameupdates.PersonalObjectiveChosenUpdate;
import it.polimi.ingsw.am49.server.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.common.enumerations.GameEventType;
import it.polimi.ingsw.am49.server.model.players.Player;

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
