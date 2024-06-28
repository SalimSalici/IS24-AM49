package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.common.Server;
import it.polimi.ingsw.am49.common.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Manages a server socket that listens for incoming client connections.
 */
public class ServerSocketManager {

    /**
     * The server instance to handle client connections.
     */
    private final Server server;

    /**
     * The server socket that listens for incoming connections.
     */
    private final ServerSocket serverSocket;

    // private final List<SocketClientHandler> socketClientHandlers;

    /**
     * Creates a new ServerSocketManager.
     *
     * @param server the server instance to handle client connections
     * @param port the port on which the server socket will listen
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public ServerSocketManager(Server server, int port) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(port);
        // this.socketClientHandlers = new ArrayList<>();
        new Thread(this::listenForNewClients).start();
    }

    /**
     * Listens for new client connections in a continuous loop.
     * When a client connects, it creates a new SocketClientHandler to manage the connection.
     */
    public void listenForNewClients() {
        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                // this.socketClientHandlers.add(new SocketClientHandler(clientSocket, this.server));
                new SocketClientHandler(clientSocket, this.server);
                Log.getLogger().info("Accepted new client with address: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                Log.getLogger().severe("Error accepting a client...");
            }
        }
    }
}
