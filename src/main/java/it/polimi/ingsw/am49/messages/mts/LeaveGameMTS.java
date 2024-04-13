package it.polimi.ingsw.am49.messages.mts;

public class LeaveGameMTS extends MessageToServer {
    public LeaveGameMTS(String username) {
        super(MessageToServerType.LEAVE_GAME, username);
    }
}
