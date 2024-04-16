package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.Client;
import it.polimi.ingsw.am49.model.actions.GameAction;

public class CreateNewGameMTS extends MessageToServer {
    int numOfPlayers;
    public CreateNewGameMTS(Client client, int numOfPlayers) {
        super(MessageToServerType.CREATE_NEW_GAME, client);
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
