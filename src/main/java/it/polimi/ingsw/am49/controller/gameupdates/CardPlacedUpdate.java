package it.polimi.ingsw.am49.controller.gameupdates;

import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.util.Map;

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
