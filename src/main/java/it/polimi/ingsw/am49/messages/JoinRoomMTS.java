package it.polimi.ingsw.am49.messages;

public record JoinRoomMTS(
        int id,
        String roomName,
        String username
) implements SocketMessage {}