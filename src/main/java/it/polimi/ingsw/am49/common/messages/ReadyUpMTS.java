package it.polimi.ingsw.am49.common.messages;

import it.polimi.ingsw.am49.common.enumerations.Color;

public record ReadyUpMTS(int id, Color color) implements SocketMessage {}
