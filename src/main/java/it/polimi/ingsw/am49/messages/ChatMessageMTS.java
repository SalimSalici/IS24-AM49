package it.polimi.ingsw.am49.messages;

import it.polimi.ingsw.am49.chat.ChatMSG;

public record ChatMessageMTS(int id, ChatMSG chatMSG) implements SocketMessage {}