package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.ClientOld;

public class CreateNewGameMTS extends MessageToServer {
    int numOfPlayers;
    public CreateNewGameMTS(ClientOld client, int numOfPlayers) {
        super(MessageToServerType.CREATE_NEW_GAME, client);
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
