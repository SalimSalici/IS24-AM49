package it.polimi.ingsw.am49.common.messages;

public record ReturnMessage(int id, Object returnValue) implements SocketMessage {}
