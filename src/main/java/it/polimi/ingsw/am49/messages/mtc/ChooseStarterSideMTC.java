package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class ChooseStarterSideMTC extends MessageToClient{
    private final int starterId;

    public ChooseStarterSideMTC(int starterId){
        super(MessageToClientType.CHOOSE_STARTER_SIDE, "Your starter card is: ");
        this.starterId = starterId;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.starterId;
    }
}