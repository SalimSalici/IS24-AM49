package it.polimi.ingsw.am49.messages.mtc;

import java.util.List;

public class PlayerJoinedMTC extends MessageToClient{
    private final List<String> userNames;

    public PlayerJoinedMTC(List<String> userNames){
        super(
                MessageToClientType.PLAYER_JOINED,
                "player just joined. current players in the lobby: " + userNames
        );
        this.userNames = userNames;
    }
}