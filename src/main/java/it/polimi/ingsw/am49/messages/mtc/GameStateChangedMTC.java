package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class GameStateChangedMTC extends MessageToClient{
    private final int round;
    private final int turn;
    private final String currentPlayer;

    public GameStateChangedMTC(int round, int turn, String currentPlayer){
        super(MessageToClientType.COMMON_OBJECTIVES, "ROUND | TURN | CURRENT_PLAYER : ");
        this.round = round;
        this.turn = turn;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.round + " | " + this.turn + " | " + this.currentPlayer;
    }
}
