package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.controller.gameupdates.HandUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.HiddenHandUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.TileInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is used to represent the complete set of player information in an already started game, which
 * will be needed by a reconnecting client (a client that reconnects mid-game).
 */
public record CompletePlayerInfo(
        boolean hidden,
        String username,
        int points,
        Color color,
        int personalObjectiveId,
        LinkedList<TileInfo> tiles,
        HandUpdate hand,
        HiddenHandUpdate hiddenHand,
        Map<Symbol, Integer> activeSymbols
) implements Serializable {}
