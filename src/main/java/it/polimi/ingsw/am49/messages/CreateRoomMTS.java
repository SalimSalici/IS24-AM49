package it.polimi.ingsw.am49.messages;

public record CreateRoomMTS(
        int id,
        String roomName,
        int numPlayers,
        String creatorUsername
) implements SocketMessage {}

