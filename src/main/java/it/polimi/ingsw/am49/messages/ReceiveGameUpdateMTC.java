package it.polimi.ingsw.am49.messages;

import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;

public record ReceiveGameUpdateMTC(int id, GameUpdate gameUpdate) implements SocketMessage {}