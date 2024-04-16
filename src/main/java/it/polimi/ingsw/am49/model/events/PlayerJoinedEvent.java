package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an event that occurs when a player joins a game session.
 * This event is triggered to update all connected clients about the players currently in the session.
 *
 * @param players a list of {@link Player} instances representing all players currently in the game
 */
public record PlayerJoinedEvent(List<Player> players) implements GameEvent {

    public PlayerJoinedEvent(List<Player> players) {
        this.players = new LinkedList<>(players);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.PLAYER_JOINED_EVENT;
    }
}