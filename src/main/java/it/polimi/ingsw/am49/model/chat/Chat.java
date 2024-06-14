package it.polimi.ingsw.am49.model.chat;

import it.polimi.ingsw.am49.model.players.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chat system that handles the storage and retrieval of chat messages.
 */
public class Chat implements Serializable {

    private final List<ChatMessage> messages;

    /**
     * Constructs a new empty Chat instance.
     */
    public Chat(){
        this.messages = new ArrayList<>();
    }

    /**
     * Constructs a new Chat instance with an initial list of messages.
     *
     * @param messages the initial list of chat messages
     */
    public Chat(List<ChatMessage> messages){
        this.messages = messages;
    }

    /**
     * Returns the list of chat messages.
     *
     * @return the list of chat messages
     */
    public List<ChatMessage> getMessages(){
        return messages;
    }

    /**
     * Adds a new message to the chat.
     *
     * @param text      the text of the message
     * @param sender    the sender of the message
     * @param recipient the recipient of the message
     */
    public void addMessage(String text, String sender, String recipient){
        messages.add(new ChatMessage(text, sender, recipient));
    }
}
