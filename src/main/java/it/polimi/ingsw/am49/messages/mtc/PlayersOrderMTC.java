package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class PlayersOrderMTC extends MessageToClient{
    private final List<String> playersOrder;

    public PlayersOrderMTC(List<String> playersOrder){
        super(MessageToClientType.PLAYERS_ORDER, "This is the order of players: ");
        this.playersOrder = new ArrayList<>(playersOrder);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.playersOrder.toString();
    }
}
