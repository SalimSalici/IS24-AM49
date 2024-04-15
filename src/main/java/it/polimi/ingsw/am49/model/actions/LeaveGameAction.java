package it.polimi.ingsw.am49.model.actions;

public class LeaveGameAction extends GameAction {
    public LeaveGameAction(String username) {
        super(GameActionType.LEAVE_GAME, username);
    }
}
