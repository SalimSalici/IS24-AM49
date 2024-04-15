package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.controller.Client;

public class CreateGameMTS extends MessageToServer{
    private final Client client;
    private final int numOfPlayers;

    public CreateGameMTS(Client client, int numOfPlayers){
        super(MessageToServerType.CREATE_NEW_GAME, client.getUserName());
        this.client = client;
        this.numOfPlayers = numOfPlayers;
    }

    public Client getClient() {
        return client;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
