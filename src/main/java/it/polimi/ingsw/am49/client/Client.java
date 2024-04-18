package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.messages.mtc.MessageToClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Client extends Remote {
    public void loginOutcome(boolean outcome) throws RemoteException;
    public void lobbyList(List<String> lobbies) throws RemoteException;
    public void joinGame(MessageToClient msg) throws RemoteException;
    public void receiveGameUpdate(MessageToClient msg) throws RemoteException;
    public void ping() throws RemoteException;
}
