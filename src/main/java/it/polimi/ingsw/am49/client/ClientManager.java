package it.polimi.ingsw.am49.client;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.server.Server;

public class ClientManager {
    private String username;
    private Server server;
    private VirtualGame virtualGame;
    private static ClientManager instance;

    public static ClientManager getInstance() {
        if (ClientManager.instance == null) {
            ClientManager.instance =  new ClientManager();
            return ClientManager.instance;
        }
        return ClientManager.instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public VirtualGame getVirtualGame() {
        return virtualGame;
    }

    public void setVirtualGame(VirtualGame virtualGame) {
        this.virtualGame = virtualGame;
    }
}
