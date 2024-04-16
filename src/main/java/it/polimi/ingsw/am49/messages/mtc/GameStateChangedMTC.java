package it.polimi.ingsw.am49.messages.mtc;

import it.polimi.ingsw.am49.model.enumerations.GameStateType;

public class GameStateChangedMTC extends MessageToClient{
    private final GameStateType state;
    private final int round;
    private final int turn;
    private final String currentPlayer;

    public GameStateChangedMTC(GameStateType state, int round, int turn, String currentPlayer){
        super(MessageToClientType.COMMON_OBJECTIVES, "STATE | ROUND | TURN | CURRENT_PLAYER : ");
        this.state = state;
        this.round = round;
        this.turn = turn;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.state + " | " + this.round + " | " + this.turn + " | " + this.currentPlayer;
    }
}
