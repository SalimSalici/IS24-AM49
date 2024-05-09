package it.polimi.ingsw.am49.messages;


import it.polimi.ingsw.am49.controller.room.RoomInfo;

public record RoomUpdateMTC(int id, RoomInfo roomInfo, String message) implements SocketMessage {}