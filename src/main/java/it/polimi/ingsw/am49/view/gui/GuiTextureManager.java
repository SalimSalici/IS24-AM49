package it.polimi.ingsw.am49.view.gui;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages GUI textures for cards, resources, and other game elements.
 */
public class GuiTextureManager {
    private final Map<Integer, GuiTexture> textures;
    private final Map<Resource, Image> goldBack;
    private final Map<Resource, Image> resourceBack;
    private final Image objectiveBack;
    private final Image turnIndicator;

    private static GuiTextureManager instance;

    /**
     * Private constructor for singleton pattern.
     * Initializes all texture maps and loads images.
     */
    private GuiTextureManager() {
        this.goldBack = new HashMap<>();
        for(Resource resource : Resource.values()){
            goldBack.put(resource, loadCardBack(resource, true));
        }

        this.resourceBack = new HashMap<>();
        for(Resource resource : Resource.values()){
            resourceBack.put(resource, loadCardBack(resource, false));
        }

        objectiveBack = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/gold_card_back/087.png")));
        turnIndicator = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/elements/turnIndicator.png")));

        this.textures = new HashMap<>();
        for (int i = 1; i <= 102; i++)
            this.textures.put(i, this.loadCard(i));
    }

    /**
     * Loads a card texture based on its ID.
     * @param id The card ID.
     * @return GuiTexture containing both front and back images.
     */
    private GuiTexture loadCard(int id) {
        try {
            String zeros = "0".repeat(3 - String.valueOf(id).length());
            String subFolder_front = "gold_cards_front_resized";
            String subFolder_back = "gold_card_back_resized";

            Image frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/Resized/" + subFolder_front + "/" + zeros + id + ".png")));
            Image backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/Resized/" + subFolder_back + "/" + zeros + id + ".png")));

            return new GuiTexture(frontImage, backImage);
        } catch (NullPointerException | IllegalArgumentException e) {
            System.err.println("Error loading images for card ID: " + id);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads the back image of a card based on the resource type and whether it is a gold card.
     * @param resource The resource type.
     * @param isGold Whether the card is a gold card.
     * @return Image of the card back.
     */
    private Image loadCardBack(Resource resource, boolean isGold) {
        int cardId;
        try {
            switch (resource) {
                case MUSHROOMS -> cardId = 1;
                case LEAVES -> cardId = 11;
                case WOLVES -> cardId = 21;
                case BUGS -> cardId = 31;
                default -> cardId = 0;
            }

            if (cardId == 0) {
                throw new RuntimeException("The resource passed to the function is not valid");
            }

            if (isGold) cardId += 40;

            return Objects.requireNonNull(loadCard(cardId)).getBackImage();
        } catch (RuntimeException e) {
            System.err.println("Error loading back image for resource: " + resource);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the singleton instance of GuiTextureManager.
     * @return The singleton instance.
     */
    public static GuiTextureManager getInstance(){
        if(GuiTextureManager.instance == null)
            GuiTextureManager.instance = new GuiTextureManager();
        return GuiTextureManager.instance;
    }

    /**
     * Retrieves the image of a card by its ID and orientation.
     * @param id The card ID.
     * @param flipped Whether the card is flipped (showing the back).
     * @return The requested card image.
     */
    public Image getCardImage(int id, boolean flipped){
        if(flipped)
            return this.textures.get(id).getBackImage();
        return this.textures.get(id).getFrontImage();
    }

    /**
     * Retrieves the image of a card based on a VirtualCard object.
     * @param virtualCard The virtual card.
     * @return The image of the card.
     */
    public Image getCardImageByVirtualCard(VirtualCard virtualCard){
        return getCardImage(virtualCard.id(), virtualCard.flipped());
    }

    /**
     * Retrieves the back image of a card based on the resource type and whether it is a gold card.
     * @param resource The resource type.
     * @param isGold Whether the card is a gold card.
     * @return The back image of the card.
     */
    public Image getCardBackByResource(Resource resource, boolean isGold){
        if(isGold)
            return this.goldBack.get(resource);
        return this.resourceBack.get(resource);
    }

    /**
     * Retrieves the image for the objective back.
     * @return The objective back image.
     */
    public Image getObjectiveBack() {
        return objectiveBack;
    }

    /**
     * Retrieves the image associated with a totem color.
     * @param color The totem color.
     * @return The image corresponding to the totem color.
     */
    public Image getImageByTotemColor(Color color){
        String fileColor;
        switch (color){
            case RED -> fileColor = "rouge";
            case BLUE -> fileColor = "bleu";
            case GREEN -> fileColor = "vert";
            case YELLOW -> fileColor = "jaune";
            default -> fileColor = "noir";
        }

        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/" + "CODEX_pion_" + fileColor + ".png")));
    }

    /**
     * Retrieves the image of the turn indicator.
     * @return The turn indicator image.
     */
    public Image getTurnIndicator() {
        return turnIndicator;
    }
}
