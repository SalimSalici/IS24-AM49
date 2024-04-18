package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.model.actions.GameAction;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    public void login(Client client, String username) throws RemoteException;
    public void logout(Client client, String username) throws RemoteException;
    public void fetchLobbies(Client client) throws RemoteException;
    public void createGame(Client client, String gameName, int numPlayers) throws RemoteException;
    public void joinGame(Client client, String gameName) throws RemoteException;
    public void executeAction(Client c, GameAction action) throws RemoteException;
    public void reconnect(Client c, String gameName) throws RemoteException;
    public void ping(Client c) throws RemoteException;
}
