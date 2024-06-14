package it.polimi.ingsw.am49.model.chat;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Represents a single chat message in the chat system.
 */
public class ChatMessage implements Serializable {

    private final String text;
    private final String sender;
    private final LocalTime time;
    private final String recipient;

    /**
     * Constructs a new ChatMessage instance with the specified text, sender, and recipient.
     * The time of the message is set to the current local time.
     *
     * @param text      the text of the message
     * @param sender    the sender of the message
     * @param recipient the recipient of the message
     */
    public ChatMessage(String text, String sender, String recipient) {
        this.text = text;
        this.sender = sender;
        this.time = LocalTime.now();
        this.recipient = recipient;
    }

    /**
     * Returns the text of the message.
     *
     * @return the text of the message
     */
    public String getText() { return text; }

    /**
     * Returns the sender of the message.
     *
     * @return the sender of the message
     */
    public String getSender() { return sender; }

    /**
     * Returns the time when the message was sent.
     *
     * @return the time when the message was sent
     */
    public LocalTime getTime() { return time; }

    /**
     * Returns the recipient of the message.
     *
     * @return the recipient of the message
     */
    public String getRecipient() { return recipient; }

}
