package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.server.Server;

import java.rmi.RemoteException;

public interface ServerConnector {
    Server connect(String host, int port, ClientApp client) throws RemoteException;
}
