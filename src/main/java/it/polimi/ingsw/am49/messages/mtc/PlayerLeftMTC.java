package it.polimi.ingsw.am49.messages.mtc;

import java.util.LinkedList;
import java.util.List;

public class PlayerLeftMTC extends MessageToClient{
    private final List<String> usernamesRemaining;
    private final String usernameWhoLeft;

    public PlayerLeftMTC(List<String> usernamesRemaining,  String usernameWhoLeft){
        super(MessageToClientType.PLAYER_LEFT, " left the game");
        this.usernamesRemaining = new LinkedList<>(usernamesRemaining);
        this.usernameWhoLeft = usernameWhoLeft;
    }

    @Override
    public String getMessage() {
        return this.usernameWhoLeft + super.getMessage();
    }

    public List<String> getUsernamesRemaining() {
        return usernamesRemaining;
    }

    public String getUsernameWhoLeft() {
        return usernameWhoLeft;
    }
}
