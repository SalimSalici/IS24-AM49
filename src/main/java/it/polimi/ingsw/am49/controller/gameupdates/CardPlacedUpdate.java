package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.util.Map;

/**
 * Represents an update when a card is placed in the game.
 * This is a record class that holds the username of the player who placed the card,
 * the card ID, the position of the card (row and column), whether the card is flipped,
 * the active symbols on the card, and the points scored by placing the card.
 */
public record CardPlacedUpdate(
        String username,
        int cardId,
        int row,
        int col,
        boolean flipped,
        Map<Symbol, Integer> activeSymbols,
        int points
) implements GameUpdate {
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.CARD_PLACED_UPDATE;
    }
}
