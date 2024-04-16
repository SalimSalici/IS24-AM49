package it.polimi.ingsw.am49.messages.mtc;

import java.util.ArrayList;
import java.util.List;

public class ChooseLobbyMTC extends MessageToClient{
    List<Integer> gamesIds;

    ChooseLobbyMTC(List<Integer> gamesIds){
        super(MessageToClientType.CHOOSE_LOBBY, "You can choose between these lobbies: ");
        this.gamesIds = new ArrayList<>(gamesIds);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.gamesIds.toString();
    }

    public List<Integer> getGamesIds() {
        return gamesIds;
    }
}