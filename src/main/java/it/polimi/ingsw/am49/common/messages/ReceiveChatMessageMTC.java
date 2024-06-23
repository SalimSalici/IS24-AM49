package it.polimi.ingsw.am49.common.messages;

import it.polimi.ingsw.am49.common.gameupdates.ChatMSG;

public record ReceiveChatMessageMTC(int id, ChatMSG chatMSG) implements SocketMessage {}