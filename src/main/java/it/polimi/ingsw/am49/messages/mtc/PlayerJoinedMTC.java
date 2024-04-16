package it.polimi.ingsw.am49.messages.mtc;

import java.util.List;

public class PlayerJoinedMTC extends MessageToClient{
    private final List<String> usernames;

    public PlayerJoinedMTC(List<String> usernames){
        super(
                MessageToClientType.PLAYER_JOINED,
                "player just joined. current players in the lobby: " + usernames
        );
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }
}