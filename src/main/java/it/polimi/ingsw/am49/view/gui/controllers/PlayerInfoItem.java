package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.scene.image.Image;

/**
 * Represents an item containing player information to be displayed in the GUI.
 * This item includes the player's name and their totem image.
 */
public class PlayerInfoItem {
    private final String playerName;
    private final Image totemImage;

    /**
     * Constructs a new PlayerInfoItem with the specified player name and totem image.
     *
     * @param playerName the name of the player
     * @param totemImage the image of the player's totem
     */
    public PlayerInfoItem(String playerName, Image totemImage) {
        this.playerName = playerName;
        this.totemImage = totemImage;
    }

    /**
     * @return the name of the player
     */
    public String getText() {
        return playerName;
    }

    /**
     * @return the image of the player's totem
     */
    public Image getImage() {
        return totemImage;
    }
}
