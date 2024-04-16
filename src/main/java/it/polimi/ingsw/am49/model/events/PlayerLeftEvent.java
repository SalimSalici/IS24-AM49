package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an event that occurs when a player leaves the game session.
 * This event is triggered to update all connected clients about the players currently in the session.
 *
 * @param remainingPlayers a list of {@link Player}s representing all players still in the game
 * @param playerWhoLeft the {@link Player} who just left the game
 */
public record PlayerLeftEvent(List<Player> remainingPlayers, Player playerWhoLeft) implements GameEvent {

    public PlayerLeftEvent(List<Player> remainingPlayers, Player playerWhoLeft) {
        this.remainingPlayers = new LinkedList<>(remainingPlayers);
        this.playerWhoLeft = playerWhoLeft;
    }

    @Override
    public GameEventType getType() {
        return GameEventType.PLAYER_LEFT_EVENT;
    }
}