package it.polimi.ingsw.am49.messages;

/**
 * Represents a message to the server (MTS) containing chat message details.
 * This is a record class that holds the text of the message, the sender, and the recipient.
 */
public record ChatMessageMTS(
        String text,
        String sender,
        String recipient
) {
}
