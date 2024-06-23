package it.polimi.ingsw.am49.common.reconnectioninfo;

import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.server.controller.room.Room;

import java.io.Serializable;
import java.util.Map;

/**
 * This record is used to represent useful information for the clients about the a {@link Room}.
 */
public record RoomInfo(String roomName, int maxPlayers, Map<String, Color> playersToColors) implements Serializable {}
