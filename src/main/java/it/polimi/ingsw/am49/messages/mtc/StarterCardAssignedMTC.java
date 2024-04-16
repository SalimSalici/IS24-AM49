package it.polimi.ingsw.am49.messages.mtc;

public class StarterCardAssignedMTC extends MessageToClient{
    private final int starterId;

    public StarterCardAssignedMTC(int starterId){
        super(MessageToClientType.CHOOSE_STARTER_SIDE, "Your starter card is: ");
        this.starterId = starterId;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.starterId;
    }

    public int getStarterId() {
        return starterId;
    }
}