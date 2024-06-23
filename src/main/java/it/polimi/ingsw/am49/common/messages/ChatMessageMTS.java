package it.polimi.ingsw.am49.common.messages;

import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;

public record ChatMessageMTS(int id, ChatMSG chatMSG) implements SocketMessage {}