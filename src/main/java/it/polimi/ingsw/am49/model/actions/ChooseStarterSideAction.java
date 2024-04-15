package it.polimi.ingsw.am49.model.actions;

public class ChooseStarterSideAction extends GameAction {

    private final boolean flipped;

    public ChooseStarterSideAction(String username, boolean flipped) {
        super(GameActionType.CHOOSE_STARTER_SIDE, username);
        this.flipped = flipped;
    }

    public boolean getFlipped() {
        return flipped;
    }
}
