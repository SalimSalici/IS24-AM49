package it.polimi.ingsw.am49.messages.mtc;

public class PlayerJoinedMTC extends MessageToClient{
    private final String userName;

    public PlayerJoinedMTC(String userName){
        super(MessageToClientType.PLAYER_JOINED, " joined the game");
        this.userName = userName;
    }

    @Override
    public String getMessage() {
        return this.userName + super.getMessage();
    }
}