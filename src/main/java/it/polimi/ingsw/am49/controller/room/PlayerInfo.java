package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.ClientHandler;

/**
 * This class is used by the {@link Room} to store information about a specific client, like it's username, color and
 * ready state. It halso manages the {@link VirtualView} of the client.
 */
public class PlayerInfo {
    private final String username;
    private final ClientHandler client;
    private Color color;
    private boolean readyToPlay;
    private VirtualView virtualView;

    public PlayerInfo(String username, ClientHandler client) {
        this.username = username;
        this.client = client;
        this.color = null;
        this.readyToPlay = false;
        this.virtualView = null;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setNullColor(){
        this.color = null;
    }

    public ClientHandler getClient() {
        return client;
    }

    public String getUsername() {
        return username;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }
}