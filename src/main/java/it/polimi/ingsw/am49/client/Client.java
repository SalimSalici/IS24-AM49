package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.messages.mtc.MessageToClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    public void receiveGameUpdate(MessageToClient msg) throws RemoteException;
    public void playerDisconnected(String username) throws RemoteException;
    public void ping() throws RemoteException;
}
