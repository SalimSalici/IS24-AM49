package it.polimi.ingsw.am49.common.messages;

public record ReconnectMTS(int id, String roomName, String username) implements SocketMessage {}
