package it.polimi.ingsw.am49.client.virtualmodel;

import java.time.LocalTime;

/**
 * Represents a virtual chat message in the virtual model.
 * This class holds the text, sender, recipient, and the time of the message.
 */
public class VirtualChatMessage {

    private final String text;
    private final String sender;
    private final String recipient;
    private final LocalTime time;

    /**
     * Constructs a new VirtualChatMessage instance with the specified text, sender, recipient, and time.
     *
     * @param text      the text of the message
     * @param sender    the sender of the message
     * @param recipient the recipient of the message
     * @param time      the time when the message was sent
     */
    public VirtualChatMessage(String text, String sender, String recipient, LocalTime time) {
        this.text = text;
        this.sender = sender;
        this.recipient = recipient;
        this.time = time;
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
     * Returns the recipient of the message.
     *
     * @return the recipient of the message
     */
    public String getRecipient() { return recipient; }

    /**
     * Returns the time when the message was sent.
     *
     * @return the time when the message was sent
     */
    public LocalTime getTime() { return time; }

    public String getTimeAsString() { return time.toString(); }

    public boolean isPrivate(){
        return !recipient.equals("*");
    }
}
