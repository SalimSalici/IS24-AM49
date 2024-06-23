package it.polimi.ingsw.am49.common.actions;

public class JoinGameAction extends GameAction {
    public JoinGameAction(String username) {
        super(GameActionType.JOIN_GAME, username);
    }
}
