package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.messages.mtc.MessageToClient;

import java.rmi.RemoteException;

public class ClientHandler implements Client {
    private final Client client;
    private final String username;

    public ClientHandler(Client client, String username) {
        this.client = client;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void receiveGameUpdate(MessageToClient msg) throws RemoteException {
        this.client.receiveGameUpdate(msg);
    }

    @Override
    public void playerDisconnected(String username) throws RemoteException {

    }

    @Override
    public void ping() throws RemoteException {
        this.client.ping();
    }
}
