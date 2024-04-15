package it.polimi.ingsw.am49.model.actions;

public class ChooseObjectiveAction extends GameAction {

    private final int objectiveId;

    public ChooseObjectiveAction(String username, int objectiveId) {
        super(GameActionType.CHOOSE_OBJECTIVE, username);
        this.objectiveId = objectiveId;
    }

    public int getObjectiveId() {
        return objectiveId;
    }
}
