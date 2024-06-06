package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.CardPlacedUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.Player;

/**
 * Implements the event that notifies when a card gets placed.
 * @param player the player that placed the card
 * @param boardTile the boardTile in whitch the card gets placed
 */
public record CardPlacedEvent(Player player, BoardTile boardTile) implements GameEvent {

    @Override
    public GameEventType getType() {
        return GameEventType.CARD_PLACED_EVENT;
    }

    @Override
    public CardPlacedUpdate toGameUpdate() {
        return new CardPlacedUpdate(
                player.getUsername(),
                boardTile.getCard().getId(),
                boardTile.getRow(),
                boardTile.getCol(),
                boardTile.getCard().isFlipped(),
                player.getBoard().getAvailableResources(),
                player.getPoints()
        );
    }
}
