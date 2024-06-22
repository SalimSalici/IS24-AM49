package it.polimi.ingsw.am49.client;

import it.polimi.ingsw.am49.client.sockets.ServerSocketHandler;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.ServerSocketManager;

import java.io.IOException;
import java.rmi.RemoteException;

public class ServerConnectorSocket implements ServerConnector {
    @Override
    public Server connect(String host, int port, ClientApp client) throws RemoteException {
        Server server = client.getServer();
        if (server != null && client.getServer() instanceof ServerSocketHandler)
            ((ServerSocketHandler) server).close();

        try {
            return new ServerSocketHandler(host, port, client);
        } catch (Exception e) {
            throw new RemoteException("Could not connect to server.");
        }
    }
}
