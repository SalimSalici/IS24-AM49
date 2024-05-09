package it.polimi.ingsw.am49.messages;

import it.polimi.ingsw.am49.controller.room.RoomInfo;

public record PlayerLeftYourRoomMTC(int id, RoomInfo roomInfo, String username) implements SocketMessage {}