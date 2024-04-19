package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.client.Client;
import it.polimi.ingsw.am49.model.actions.GameAction;
import it.polimi.ingsw.am49.server.exceptions.InvalidUsernameException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {

    /**
     * @param client the client trying to log in
     * @param username the username chosen by the client
     * @return true if the login is successful, false if the username chosen is not available
     * @throws RemoteException if there is a network issue while invoking the remote method
     * @throws InvalidUsernameException if the chosen username is not valid (too short or too long)
     */
    public boolean login(Client client, String username) throws RemoteException, InvalidUsernameException;
    public void logout(Client client, String username) throws RemoteException;
    public void fetchLobbies(Client client) throws RemoteException;
    public void createGame(Client client, String gameName, int numPlayers) throws RemoteException;
    public void joinGame(Client client, String gameName) throws RemoteException;
    public void executeAction(Client c, GameAction action) throws RemoteException;
    public void reconnect(Client c, String gameName) throws RemoteException;
    public void ping(Client c) throws RemoteException;
}
