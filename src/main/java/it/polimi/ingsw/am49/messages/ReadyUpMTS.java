package it.polimi.ingsw.am49.messages;

import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;

public record ReadyUpMTS(
        int id,
        Color color
) implements SocketMessage {}
