package it.polimi.ingsw.am49.client.view.tui.renderers;

import it.polimi.ingsw.am49.client.view.tui.textures.BackTexture;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.client.view.tui.textures.ColoredChar;
import it.polimi.ingsw.am49.client.view.tui.textures.TuiTextureManager;

/**
 * The TuiDrawAreaRenderer class is responsible for rendering the draw area on the TUI.
 */
public class TuiDrawAreaRenderer {

    private final TuiCardRenderer renderer;
    private final VirtualDrawable drawArea;
    private final TuiTextureManager textureManager;

    /**
     * Constructs a TuiDrawAreaRenderer with the specified virtual draw area.
     *
     * @param drawArea the virtual draw area to render
     */
    public TuiDrawAreaRenderer(VirtualDrawable drawArea) {
        this.renderer = new TuiCardRenderer(53, 10);
        this.drawArea = drawArea;
        textureManager = TuiTextureManager.getInstance();
    }

    /**
     * Prints the draw area to the console.
     */
    public void print() {
        this.renderer.clear();

        ColoredChar[][] resourceBackTexture = switch (drawArea.getDeckTopResource()) {
            case WOLVES -> this.textureManager.getBackTexture(BackTexture.RB);
            case LEAVES -> this.textureManager.getBackTexture(BackTexture.RG);
            case MUSHROOMS -> this.textureManager.getBackTexture(BackTexture.RR);
            case BUGS -> this.textureManager.getBackTexture(BackTexture.RP);
            case null -> null;
        };

        ColoredChar[][] goldBackTexture = switch (drawArea.getDeckTopGold()) {
            case WOLVES -> this.textureManager.getBackTexture(BackTexture.GB);
            case LEAVES -> this.textureManager.getBackTexture(BackTexture.GG);
            case MUSHROOMS -> this.textureManager.getBackTexture(BackTexture.GR);
            case BUGS -> this.textureManager.getBackTexture(BackTexture.GP);
            case null -> null;
        };

        int firstResRev = drawArea.getRevealedResourcesIds().getFirst() == null ? -1 : drawArea.getRevealedResourcesIds().getFirst();
        int secondResRev = drawArea.getRevealedResourcesIds().get(1) == null ? -1 : drawArea.getRevealedResourcesIds().get(1);
        int firstGoldRev = drawArea.getRevealedGoldsIds().getFirst() == null ? -1 : drawArea.getRevealedGoldsIds().getFirst();
        int secondGoldRev = drawArea.getRevealedGoldsIds().get(1) == null ? -1 : drawArea.getRevealedGoldsIds().get(1);
        ColoredChar[][] firstResourceRevealed = this.textureManager.getTexture(firstResRev, false);
        ColoredChar[][] secondResourceRevealed = this.textureManager.getTexture(secondResRev, false);
        ColoredChar[][] firstGoldRevealed = this.textureManager.getTexture(firstGoldRev, false);
        ColoredChar[][] secondGoldRevealed = this.textureManager.getTexture(secondGoldRev, false);

        String remainingResources = drawArea.getRemainingResources() < 10 ?
                "0" + drawArea.getRemainingResources() :
                String.valueOf(drawArea.getRemainingResources());
        String remainingGolds = drawArea.getRemainingGolds() < 10 ?
                "0" + drawArea.getRemainingGolds() :
                String.valueOf(drawArea.getRemainingGolds());

//        System.out.println("(1) Res. deck         (2) Revealed    (3) Revealed");
        System.out.println("(1) Deck " + remainingResources + "           (2) Revealed    (3) Revealed");
        this.renderer.draw(resourceBackTexture, 7, 2);
        this.renderer.draw(goldBackTexture, 7, 7);
        this.renderer.draw(firstResourceRevealed, 29, 2);
        this.renderer.draw(secondResourceRevealed, 45, 2);
        this.renderer.draw(firstGoldRevealed, 29, 7);
        this.renderer.draw(secondGoldRevealed, 45, 7);
        this.renderer.print();
        System.out.println("(4) Deck " + remainingGolds + "           (5) Revealed    (6) Revealed");
//        System.out.println("(4) Gold deck         (5) Revealed    (6) Revealed");
    }
}
