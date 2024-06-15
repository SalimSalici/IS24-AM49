package it.polimi.ingsw.am49.chat;

import java.io.Serializable;

public record ChatMSG(
        String text,
        String sender,
        String recipient
) implements Serializable{
}