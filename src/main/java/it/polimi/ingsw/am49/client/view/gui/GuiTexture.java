package it.polimi.ingsw.am49.client.view.gui;

import javafx.scene.image.Image;

/**
 * Represents the textures used in the GUI for a card, including the front and back images.
 */
public class GuiTexture {
    private final Image front;
    private final Image back;

    /**
     * Constructs a new GuiTexture with specified front and back images.
     *
     * @param frontImage the image to be used for the front of the card
     * @param backImage the image to be used for the back of the card
     */
    public GuiTexture(Image frontImage, Image backImage){
        this.front = frontImage;
        this.back = backImage;
    }

    /**
     * @return the front image
     */
    public Image getFrontImage() {
        return front;
    }

    /**
     * @return the back image
     */
    public Image getBackImage() {
        return back;
    }
}
