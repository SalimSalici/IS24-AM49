package it.polimi.ingsw.am49.common.actions;

/**
 * Represents an action where a player chooses an objective.
 * This action is part of the game's mechanics in which players select objectives to achieve.
 */
public class ChooseObjectiveAction extends GameAction {

    /**
     * The ID of the objective chosen by the player.
     */
    private final int objectiveId;

    /**
     * Constructs a new ChooseObjectiveAction with the specified username and objective ID.
     * @param username the username of the player performing the action
     * @param objectiveId the ID of the objective being chosen
     */
    public ChooseObjectiveAction(String username, int objectiveId) {
        super(GameActionType.CHOOSE_OBJECTIVE, username);
        this.objectiveId = objectiveId;
    }

    /**
     * Returns the ID of the chosen objective.
     * @return the objective ID
     */
    public int getObjectiveId() {
        return objectiveId;
    }
}
