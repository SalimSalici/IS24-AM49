package it.polimi.ingsw.am49.messages.mts;

import it.polimi.ingsw.am49.controller.Client;

public class LeaveGameMTS extends MessageToServer {
    private Client client;
    public LeaveGameMTS(Client client) {
        super(MessageToServerType.LEAVE_GAME, client.getUserName());
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
