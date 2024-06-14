package it.polimi.ingsw.am49.controller.gameupdates;

import java.time.LocalTime;

/**
 * Represents an update for a new chat message in the game.
 * This is a record class that holds the text of the message, the sender, the receiver, and the time of the message.
 * It implements the {@link GameUpdate} interface.
 */
public record NewChatMessageUpdate(
        String text,
        String sender,
        String receiver,
        LocalTime time
) implements GameUpdate {

    /**
     * Returns the type of the game update.
     *
     * @return the type of the game update, which is {@code GameUpdateType.NEW_CHAT_MESSAGE_UPDATE}
     */
    @Override
    public GameUpdateType getType() {
        return GameUpdateType.NEW_CHAT_MESSAGE_UPDATE;
    }
}
