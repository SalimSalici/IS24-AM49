package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.EndGameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an event that signifies the end of the game, detailing the objectives achieved by each player
 * and the player who won by forfeit, if any.
 *
 * @param playersToAchievedObjectives A map linking each player to the number of objectives they achieved.
 * @param forfeitWinner The player who won by forfeit, or null if there was no forfeit winner.
 */
public record EndgameEvent(Map<Player, Integer> playersToAchievedObjectives, Player forfeitWinner) implements GameEvent {

    /**
     * Returns the type of the game event.
     *
     * @return The game event type, specifically END_GAME for this event.
     */
    @Override
    public GameEventType getType() {
        return GameEventType.END_GAME;
    }

    /**
     * Converts the event details into a game update format suitable for notifying observers about the end of the game.
     *
     * @return A {@link GameUpdate} object containing the end game details formatted for game update observers.
     */
    @Override
    public GameUpdate toGameUpdate() {
        HashMap<String, Integer[]> updateMap = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : this.playersToAchievedObjectives.entrySet()) {
            Player p = entry.getKey();
            Integer[] pointsAndObjectives = new Integer[3];
            pointsAndObjectives[0] = p.getPoints();
            pointsAndObjectives[1] = entry.getValue();
            pointsAndObjectives[2] = p.getPersonalObjective().getId();
            updateMap.put(entry.getKey().getUsername(), pointsAndObjectives);
        }
        String forfeitWinnerUsername = forfeitWinner == null ? null : forfeitWinner().getUsername();
        return new EndGameUpdate(updateMap, forfeitWinnerUsername);
    }
}
