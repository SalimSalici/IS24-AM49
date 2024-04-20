package it.polimi.ingsw.am49.controller;

import java.io.Serializable;
import java.util.List;

public record RoomInfo(String roomName, int maxPlayers, List<String> playersInRoom) implements Serializable {}
