package it.polimi.ingsw.am49.common.messages;

import it.polimi.ingsw.am49.common.gameupdates.GameUpdate;

public record ReceiveGameUpdateMTC(int id, GameUpdate gameUpdate) implements SocketMessage {}