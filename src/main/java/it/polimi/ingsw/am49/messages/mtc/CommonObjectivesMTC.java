package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class CommonObjectivesMTC extends MessageToClient{
    private final List<Integer> objectivesIds;

    public CommonObjectivesMTC(List<Integer> objectivesIds){
        super(MessageToClientType.COMMON_OBJECTIVES, "These are the common objectives: ");
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
