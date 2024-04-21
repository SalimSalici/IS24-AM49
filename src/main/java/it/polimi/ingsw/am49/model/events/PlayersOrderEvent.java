package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.PlayerOrderUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.List;

/**
 * Represents an event that is triggered when the order of players for a game session is set.
 *
 * @param playersOrder a list of {@link Player} objects representing the order in which players will participate in the game
 */
public record PlayersOrderEvent(List<Player> playersOrder) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.PLAYERS_ORDER_SET_EVENT;
    }

    @Override
    public PlayerOrderUpdate toGameUpdate() {
        return new PlayerOrderUpdate(playersOrder.stream().map(Player::getUsername).toList());
    }
}

