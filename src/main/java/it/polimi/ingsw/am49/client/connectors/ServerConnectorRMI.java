package it.polimi.ingsw.am49.client.connectors;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.common.Server;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerConnectorRMI implements ServerConnector {
    @Override
    public Server connect(String host, int port, ClientApp client) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        try {
            Server server = (Server) registry.lookup("server.am49.codex_naturalis");
            System.setProperty("java.rmi.server.hostname", server.getClientHostAddress());
            UnicastRemoteObject.exportObject(client, 0);
            return server;
        } catch (NotBoundException | RemoteException e) {
            throw new RemoteException("Could not connect to server.");
        }
    }

    @Override
    public void disconnect(ClientApp client) {
        try {
            UnicastRemoteObject.unexportObject(client, true);
        } catch (NoSuchObjectException ignored) {}
    }

    @Override
    public ConnectorType getConnectorType() {
        return ConnectorType.RMI;
    }
}
