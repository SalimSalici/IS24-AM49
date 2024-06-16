package it.polimi.ingsw.am49.server;

import it.polimi.ingsw.am49.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketManager {
    private final Server server;
    private final ServerSocket serverSocket;
    private final List<SocketClientHandler> socketClientHandlers;

    public ServerSocketManager(Server server, int port) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(port);
        this.socketClientHandlers = new ArrayList<>();
        new Thread(this::listenForNewClients).start();
    }

    public void listenForNewClients() {
        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                this.socketClientHandlers.add(new SocketClientHandler(clientSocket, this.server));
                Log.getLogger().info("Accepted new client with address: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                Log.getLogger().severe("Error accpeting a client...");
            }

        }
    }
}
