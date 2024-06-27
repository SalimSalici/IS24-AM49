package it.polimi.ingsw.am49.common.messages;

public record JoinRoomMTS(int id, String roomName, String username) implements SocketMessage {}
