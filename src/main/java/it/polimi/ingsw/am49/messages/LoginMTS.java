package it.polimi.ingsw.am49.messages;

public record LoginMTS(int id, String username) implements SocketMessage {}
