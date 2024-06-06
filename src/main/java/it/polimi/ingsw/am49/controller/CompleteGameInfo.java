package it.polimi.ingsw.am49.controller;

import it.polimi.ingsw.am49.controller.gameupdates.DrawAreaUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameStateChangedUpdate;

import java.io.Serializable;
import java.util.List;

public record CompleteGameInfo(
        String usernameOfReconnectingClient,
        DrawAreaUpdate drawArea,
        GameStateChangedUpdate gameState,
        List<Integer> commonObjectiveIds,
        List<CompletePlayerInfo> players
) implements Serializable {}
