package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.PlayerLeftUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an event that occurs when a player leaves the game session.
 * This event is triggered to update all connected clients about the players currently in the session.
 *
 * @param playerWhoLeft the {@link Player} who just left the game
 * @param remainingPlayers a list of {@link Player}s representing all players still in the game
 */
public record PlayerLeftEvent(Player playerWhoLeft, List<Player> remainingPlayers) implements GameEvent {

    public PlayerLeftEvent(Player playerWhoLeft, List<Player> remainingPlayers) {
        this.playerWhoLeft = playerWhoLeft;
        this.remainingPlayers = new LinkedList<>(remainingPlayers);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.PLAYER_LEFT_EVENT;
    }

    @Override
    public PlayerLeftUpdate toGameUpdate() {
        return null;
    }
}