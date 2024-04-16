package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class ChoosableObjectiveAssignedMTC extends MessageToClient{
    private final List<Integer> objectivesIds;

    public ChoosableObjectiveAssignedMTC(List<Integer> objectivesIds){
        super(MessageToClientType.CHOOSE_OBJECTIVE, "You can choose between these objectives: ");
        this.objectivesIds = new ArrayList<>(objectivesIds);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.objectivesIds.toString();
    }

    public List<Integer> getObjectivesIds() {
        return objectivesIds;
    }
}
