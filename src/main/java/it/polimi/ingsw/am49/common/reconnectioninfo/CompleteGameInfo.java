package it.polimi.ingsw.am49.common.reconnectioninfo;

import it.polimi.ingsw.am49.common.gameupdates.DrawAreaUpdate;
import it.polimi.ingsw.am49.common.gameupdates.GameStateChangedUpdate;

import java.io.Serializable;
import java.util.List;

/**
 * This record is used to represent the complete state of an already started game, 
 * which will be needed by a reconnecting client (a client that reconnects mid-game).
 * 
 * @param usernameOfReconnectingClient the username of the client that requested the CompleteGameInfo
 * @param drawArea the current state of the draw area
 * @param gameState the current state of the game
 * @param commonObjectiveIds the IDs of the common objectives
 * @param players the list of players in the game. Players with a different username than the one of the
 *                reconnecting client will have some of their information hidden (hand and personal objective)
 */
public record CompleteGameInfo(
        String usernameOfReconnectingClient,
        DrawAreaUpdate drawArea,
        GameStateChangedUpdate gameState,
        List<Integer> commonObjectiveIds,
        List<CompletePlayerInfo> players
) implements Serializable {}
