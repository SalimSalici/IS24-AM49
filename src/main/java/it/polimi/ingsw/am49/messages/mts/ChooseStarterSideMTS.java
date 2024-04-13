package it.polimi.ingsw.am49.messages.mts;

public class ChooseStarterSideMTS extends MessageToServer {

    private final boolean flipped;
    public ChooseStarterSideMTS(String username, boolean flipped) {
        super(MessageToServerType.CHOOSE_STARTER_SIDE, username);
        this.flipped = flipped;
    }

    public boolean getFlipped() {
        return flipped;
    }
}
