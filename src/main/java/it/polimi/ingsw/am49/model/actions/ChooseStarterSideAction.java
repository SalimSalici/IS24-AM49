package it.polimi.ingsw.am49.model.actions;

/**
 * This class represents an action to choose the starter side in the game.
 */
public class ChooseStarterSideAction extends GameAction {

    private final boolean flipped;

    /**
     * Constructs a new ChooseStarterSideAction.
     *
     * @param username the username of the player performing the action
     * @param flipped  whether the starter side is flipped
     */
    public ChooseStarterSideAction(String username, boolean flipped) {
        super(GameActionType.CHOOSE_STARTER_SIDE, username);
        this.flipped = flipped;
    }

    /**
     * Returns whether the starter side is flipped.
     *
     * @return true if the starter side is flipped, false otherwise
     */
    public boolean getFlipped() {
        return flipped;
    }
}
