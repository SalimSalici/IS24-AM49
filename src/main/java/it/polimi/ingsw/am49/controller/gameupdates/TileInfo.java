package it.polimi.ingsw.am49.controller.gameupdates;

import java.io.Serializable;

public record TileInfo(
        int cardId,
        int row,
        int col,
        boolean flipped
) implements Serializable {}
