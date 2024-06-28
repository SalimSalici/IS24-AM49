package it.polimi.ingsw.am49.common.gameupdates;

import it.polimi.ingsw.am49.common.enumerations.Symbol;

import java.util.Map;

/**
 * Represents an update when a card is placed in the game.
 * This is a record class that holds the username of the player who placed the card,
 * the card ID, the position of the card (row and column), whether the card is flipped,
 * the active symbols on the card, and the points scored by placing the card.
 *
 * @param username the username of the player who placed the card.
 * @param cardId the ID of the card that was placed.
 * @param row the row position where the card was placed.
 * @param col the column position where the card was placed.
 * @param flipped indicates whether the card is flipped.
 * @param activeSymbols a map of active symbols on the card and their respective counts.
 * @param points the points scored by placing the card.
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
    /**
     * Returns the type of game update.
     *
     * @return the game update type specific to card placement.
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.CARD_PLACED_UPDATE;
    }
}
