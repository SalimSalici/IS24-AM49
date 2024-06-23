package it.polimi.ingsw.am49.client.view.gui.controllers;

import javafx.scene.image.ImageView;

/**
 * Represents a card with an id, flip state, and associated images for front and back.
 * It provides functionality to flip the card and manage its interactive state in the GUI.
 */
public class MyCard {
    private final int id;
    private boolean isFlipped;
    private final ImageView frontImage;
    private final ImageView backImage;
    private final OverviewController controller;

    /**
     * Constructs a new MyCard with specified id, flip state, and controller.
     *
     * @param id the unique identifier of the card
     * @param isFlipped the initial flip state of the card (true if flipped, false otherwise)
     * @param controller the controller managing the card's interactions
     */
    public MyCard(int id, boolean isFlipped, OverviewController controller) {
        this.id = id;
        this.isFlipped = isFlipped;
        this.controller = controller;
        this.frontImage = createInteractiveHandCard(false);
        this.backImage = createInteractiveHandCard(true);
    }

    /**
     * Constructs a new MyCard with specified id and controller.
     * The card is initially not flipped.
     *
     * @param id the unique identifier of the card
     * @param controller the controller managing the card's interactions
     */
    public MyCard(int id, OverviewController controller) {
        this.id = id;
        this.isFlipped = false;
        this.controller = controller;
        this.frontImage = createInteractiveHandCard(false);
        this.backImage = createInteractiveHandCard(true);
    }

    /**
     * Creates an interactive card image view.
     *
     * @param flipped true if the card is flipped (back image), false otherwise (front image)
     * @return the created interactive ImageView
     */
    private ImageView createInteractiveHandCard(boolean flipped) {
        ImageView interactiveCard = new ImageView(this.controller.guiTextureManager.getCardImage(id, flipped));
        interactiveCard.setFitWidth(132);
        interactiveCard.setFitHeight(87);
        interactiveCard.getStyleClass().add("clickableImage");
        interactiveCard.setOnMouseClicked(mouseEvent -> {
            if (this.controller.getSelectedCard() == null || !this.equals(this.controller.getSelectedCard())) {
                this.controller.selectCard(this);
            } else {
                this.controller.unselectCard();
            }
        });

        return interactiveCard;
    }

    /**
     * @return the ImageView of the card (front or back image)
     */
    public ImageView getImageView() {
        if (isFlipped)
            return backImage;
        else
            return frontImage;
    }

    /**
     * @return true if the card is flipped, false otherwise
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Flips the card, changing its current state to the opposite side.
     */
    public void flip() {
        this.isFlipped = !this.isFlipped;
    }

    /**
     * @return the id of the card
     */
    public int getId() {
        return id;
    }
}
