package it.polimi.ingsw.am49.messages;

public record ReconnectMTS(int id, String roomName, String useraname) implements SocketMessage {}
