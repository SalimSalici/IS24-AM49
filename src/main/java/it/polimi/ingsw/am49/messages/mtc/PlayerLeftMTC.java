package it.polimi.ingsw.am49.messages.mtc;

public class PlayerLeftMTC extends MessageToClient{
    private final String userName;

    public PlayerLeftMTC(String userName){
        super(MessageToClientType.PLAYER_LEFT, " left the game");
        this.userName = userName;
    }

    @Override
    public String getMessage() {
        return this.userName + super.getMessage();
    }
}
