package it.polimi.ingsw.am49.view.gui;

import javafx.scene.image.Image;

public class GuiTexture {
    private final Image front;
    private final Image back;

    public GuiTexture(Image frontImage, Image backImage){
        this.front = frontImage;
        this.back = backImage;
    }

    public Image getFrontImage() {
        return front;
    }
    public Image getBackImage() {
        return back;
    }
}
