package it.polimi.ingsw.am49.messages.mts;

public class ChooseObjectiveMTS extends MessageToServer {

    private final int objectiveId;
    public ChooseObjectiveMTS(String username, int objectiveId) {
        super(MessageToServerType.CHOOSE_OBJECTIVE, username);
        this.objectiveId = objectiveId;
    }

    public int getObjectiveId() {
        return objectiveId;
    }
}
