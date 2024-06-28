package it.polimi.ingsw.am49.common.gameupdates;

import java.io.Serializable;

/**
 * The ChatMSG class represents a chat message.
 * It contains the text of the message, the sender, and the recipient.
 *
 * @param text the text of the chat message
 * @param sender the username of the sender of the chat message
 * @param recipient the username of the recipient of the chat message
 */
public record ChatMSG(
        String text,
        String sender,
        String recipient
) implements Serializable {
}
