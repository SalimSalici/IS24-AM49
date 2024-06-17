package it.polimi.ingsw.am49.controller.room;

import it.polimi.ingsw.am49.controller.VirtualView;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.ClientHandler;

/**
 * This class is used by the {@link Room} to store information about a specific client, like its username, color and
 * ready state. It also manages the {@link VirtualView} of the client.
 */
public class PlayerInfo {
    private final String username;
    private final ClientHandler client;
    private Color color;
    private boolean readyToPlay;
    private VirtualView virtualView;

    /**
     * Constructs a PlayerInfo object with the specified username and client handler.
     *
     * @param username the username of the client
     * @param client the client handler associated with the client
     */
    public PlayerInfo(String username, ClientHandler client) {
        this.username = username;
        this.client = client;
        this.color = null;
        this.readyToPlay = false;
        this.virtualView = null;
    }

    /**
     * Checks if the player is ready to play.
     *
     * @return true if the player is ready to play, false otherwise
     */
    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    /**
     * Sets the ready state of the player.
     *
     * @param readyToPlay true if the player is ready to play, false otherwise
     */
    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    /**
     * Gets the color associated with the player.
     *
     * @return the color associated with the player
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color associated with the player.
     *
     * @param color the color to be associated with the player
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets the color associated with the player to null.
     */
    public void setNullColor() {
        this.color = null;
    }

    /**
     * Gets the client handler associated with the player.
     *
     * @return the client handler associated with the player
     */
    public ClientHandler getClient() {
        return client;
    }

    /**
     * Gets the username of the player.
     *
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the virtual view associated with the player.
     *
     * @param virtualView the virtual view to be associated with the player
     */
    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    /**
     * Gets the virtual view associated with the player.
     *
     * @return the virtual view associated with the player
     */
    public VirtualView getVirtualView() {
        return virtualView;
    }
}