package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.PlayerJoinedUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an event that occurs when a player joins a game session.
 * This event is triggered to update all connected clients about the players currently in the session.
 *
 * @param playerWhoJoined the {@link Player} who just joined the game
 * @param playersInGame a list of {@link Player} instances representing all players currently in the game
 */
public record PlayerJoinedEvent(Player playerWhoJoined, List<Player> playersInGame) implements GameEvent {

    public PlayerJoinedEvent(Player playerWhoJoined, List<Player> playersInGame) {
        this.playerWhoJoined = playerWhoJoined;
        this.playersInGame = new LinkedList<>(playersInGame);
    }

    @Override
    public GameEventType getType() {
        return GameEventType.PLAYER_JOINED_EVENT;
    }

    @Override
    public PlayerJoinedUpdate toGameUpdate() {
        return new PlayerJoinedUpdate(playerWhoJoined().getUsername());
    }
}