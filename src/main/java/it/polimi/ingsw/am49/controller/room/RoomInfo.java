package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.enumerations.Color;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This record is used to represent useful information for the clients about the a {@link Room}.
 */
public record RoomInfo(String roomName, int maxPlayers, Map<String, Color> playersToColors) implements Serializable {}
