package it.polimi.ingsw.am49.common.messages;


import it.polimi.ingsw.am49.common.reconnectioninfo.RoomInfo;

public record RoomUpdateMTC(int id, RoomInfo roomInfo, String message) implements SocketMessage {}