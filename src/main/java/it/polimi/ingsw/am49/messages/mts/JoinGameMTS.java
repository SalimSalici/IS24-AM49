package it.polimi.ingsw.am49.messages.mts;

public class JoinGameMTS extends MessageToServer {
    public JoinGameMTS(String username) {
        super(MessageToServerType.JOIN_GAME, username);
    }
}
