package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class ChooseObjectiveMTC extends MessageToClient{
    List<Integer> objectivesIds;

    ChooseObjectiveMTC(List<Integer> objectivesIds){
        super(MessageToClientType.CHOOSE_OBJECTIVE, "You can choose between these objectives: ");
        this.objectivesIds = new ArrayList<>(objectivesIds);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.objectivesIds.toString();
    }
}
