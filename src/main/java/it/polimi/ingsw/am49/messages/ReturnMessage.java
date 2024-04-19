package it.polimi.ingsw.am49.messages;

public record ReturnMessage(int id, Object returnValue) implements SocketMessage {}
