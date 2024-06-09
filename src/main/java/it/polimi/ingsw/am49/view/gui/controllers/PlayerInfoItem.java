package it.polimi.ingsw.am49.view.gui.controllers;

import javafx.scene.image.Image;

public class PlayerInfoItem {
    private final String playerName;
    private final Image totemImage;

    public PlayerInfoItem(String text, Image image) {
        this.playerName = text;
        this.totemImage = image;
    }

    public String getText() {
        return playerName;
    }

    public Image getImage() {
        return totemImage;
    }
}
