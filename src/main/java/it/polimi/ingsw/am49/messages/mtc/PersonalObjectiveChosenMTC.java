package it.polimi.ingsw.am49.messages.mtc;

public class PersonalObjectiveChosenMTC extends MessageToClient{
    private final int objectiveCardId;

    public PersonalObjectiveChosenMTC(int objectiveCardId){
        super(MessageToClientType.CHOOSEN_PERSONAL_OBJECTIVE, "You've choosen this objective: ");
        this.objectiveCardId = objectiveCardId;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.objectiveCardId;
    }
}