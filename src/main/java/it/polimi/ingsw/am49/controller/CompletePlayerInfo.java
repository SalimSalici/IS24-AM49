package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.controller.gameupdates.HandUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.HiddenHandUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.TileInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

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
