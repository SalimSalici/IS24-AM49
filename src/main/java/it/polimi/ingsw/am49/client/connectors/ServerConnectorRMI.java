package it.polimi.ingsw.am49.client.connectors;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.common.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class ServerConnectorRMI implements ServerConnector {

    private static boolean initialized = false;
    @Override
    public Server connect(String host, int port, ClientApp client) throws RemoteException {
        if (!ServerConnectorRMI.initialized) {
            try {
                this.initialize();
            } catch (IOException e) {
                throw new RemoteException("Could not connect to server.");
            }
            ServerConnectorRMI.initialized = true;
        }

        Registry registry = LocateRegistry.getRegistry(host, port);
        try {
            Server server = (Server) registry.lookup("server.am49.codex_naturalis");
            System.setProperty("java.rmi.server.hostname", server.getClientHostAddress());
            UnicastRemoteObject.exportObject(client, 0);
            return server;
        } catch (NotBoundException | RemoteException e) {
            throw new RemoteException("Could not connect to server: " + e.getMessage());
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

    private void initialize() throws IOException {
        RMISocketFactory.setSocketFactory(new RMISocketFactory() {
            public Socket createSocket(String host, int port ) throws IOException {
                Socket socket = new Socket();
                socket.setSoTimeout( 2000 );
                socket.setSoLinger(false, 0);
                socket.connect(new InetSocketAddress(host, port), 2000);
                return socket;
            }

            public ServerSocket createServerSocket(int port ) throws IOException {
                return new ServerSocket(port);
            }
        });
    }
}
