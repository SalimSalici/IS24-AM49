package it.polimi.ingsw.am49.view.gui;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuiTextureManager {
    private final Map<Integer, GuiTexture> textures;
    private final Map<Resource, Image> goldBack;
    private final Map<Resource, Image> resourceBack;
    private final Image objectiveBack;
    private final Image turnIndicator;

    private static GuiTextureManager instance;

    private GuiTextureManager() {
        // loads all backs
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

        // loads all the cards
        this.textures = new HashMap<>();
        for (int i = 1; i <= 102; i++)
            this.textures.put(i, this.loadCard(i));
    }

    private GuiTexture loadCard(int id) {
        try {
            String zeros = "0".repeat(3 - String.valueOf(id).length());
            String subFolder_front = "gold_cards_front";
            String subFolder_back = "gold_card_back";

            Image frontImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/" + subFolder_front + "/" + zeros + id + ".png")));
            Image backImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/am49/images/" + subFolder_back + "/" + zeros + id + ".png")));

            return new GuiTexture(frontImage, backImage);
        } catch (NullPointerException | IllegalArgumentException e) {
            System.err.println("Error loading images for card ID: " + id);
            e.printStackTrace();
            return null;
        }
    }

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

            if (isGold) cardId += 40; // questo offset permette di prendere la prima carta gold per ogni tipo di risorsa

            return Objects.requireNonNull(loadCard(cardId)).getBackImage();
        } catch (RuntimeException e) {
            System.err.println("Error loading back image for resource: " + resource);
            e.printStackTrace();
            return null;
        }
    }

    public static GuiTextureManager getInstance(){
        if(GuiTextureManager.instance == null)
            GuiTextureManager.instance = new GuiTextureManager();
        return GuiTextureManager.instance;
    }

    public Image getCardImage(int id, boolean flipped){
        if(flipped)
            return this.textures.get(id).getBackImage();
        return this.textures.get(id).getFrontImage();
    }
    public Image getCardImageByVirtualCard(VirtualCard virtualCard){
        return getCardImage(virtualCard.id(), virtualCard.flipped());
    }
    public Image getCardBackByResource(Resource resource, boolean isGold){
        if(isGold)
            return this.goldBack.get(resource);
        return this.resourceBack.get(resource);
    }
    public Image getObjectiveBack() {
        return objectiveBack;
    }

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

    public Image getTurnIndicator() {
        return turnIndicator;
    }
}
