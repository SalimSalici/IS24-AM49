package it.polimi.ingsw.am49.common.gameupdates;

import java.io.Serializable;

/**
 * A record that represents information about a tile in the game.
 *
 * @param cardId the ID of the card associated with the tile
 * @param row the row position of the tile
 * @param col the column position of the tile
 * @param flipped the state of the tile, whether it is flipped or not
 */
public record TileInfo(
        int cardId,
        int row,
        int col,
        boolean flipped
) implements Serializable {}
