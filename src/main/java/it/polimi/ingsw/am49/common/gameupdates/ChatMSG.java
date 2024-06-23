package it.polimi.ingsw.am49.common.gameupdates;

import java.io.Serializable;

/**
 * The ChatMSG class represents a chat message.
 * It contains the text of the message, the sender, and the recipient.
 */
public record ChatMSG(
        String text,
        String sender,
        String recipient
) implements Serializable {
}