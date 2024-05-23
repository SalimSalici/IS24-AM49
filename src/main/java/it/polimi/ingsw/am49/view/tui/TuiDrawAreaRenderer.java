package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualDrawable;
import it.polimi.ingsw.am49.view.tui.textures.BackTexture;
import it.polimi.ingsw.am49.view.tui.textures.ColoredChar;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.util.List;

public class TuiDrawAreaRenderer {

    private final TuiCardRenderer renderer;
    private final VirtualDrawable drawArea;
    private final TuiTextureManager textureManager;

    public TuiDrawAreaRenderer(VirtualDrawable drawArea) {
        this.renderer = new TuiCardRenderer(53, 10);
        this.drawArea = drawArea;
        textureManager = TuiTextureManager.getInstance();
    }

    public void print() {
        ColoredChar[][] resourceBackTexture = switch (drawArea.getDeckTopResource()) {
            case WOLVES -> this.textureManager.getBackTexture(BackTexture.RB);
            case LEAVES -> this.textureManager.getBackTexture(BackTexture.RG);
            case MUSHROOMS -> this.textureManager.getBackTexture(BackTexture.RR);
            case BUGS -> this.textureManager.getBackTexture(BackTexture.RP);
        };

        ColoredChar[][] goldBackTexture = switch (drawArea.getDeckTopResource()) {
            case WOLVES -> this.textureManager.getBackTexture(BackTexture.GB);
            case LEAVES -> this.textureManager.getBackTexture(BackTexture.GG);
            case MUSHROOMS -> this.textureManager.getBackTexture(BackTexture.GR);
            case BUGS -> this.textureManager.getBackTexture(BackTexture.GP);
        };

        ColoredChar[][] firstResourceRevealed = this.textureManager.getTexture(drawArea.getRevealedResourcesIds().getFirst(), false);
        ColoredChar[][] secondResourceRevealed = this.textureManager.getTexture(drawArea.getRevealedResourcesIds().get(1), false);
        ColoredChar[][] firstGoldRevealed = this.textureManager.getTexture(drawArea.getRevealedGoldsIds().getFirst(), false);
        ColoredChar[][] secondGoldRevealed = this.textureManager.getTexture(drawArea.getRevealedGoldsIds().get(1), false);

        System.out.println("(1) Res. deck         (2) Revealed    (3) Revealed");
        this.renderer.draw(resourceBackTexture, 7, 2);
        this.renderer.draw(goldBackTexture, 7, 7);
        this.renderer.draw(firstResourceRevealed, 29, 2);
        this.renderer.draw(secondResourceRevealed, 45, 2);
        this.renderer.draw(firstGoldRevealed, 29, 7);
        this.renderer.draw(secondGoldRevealed, 45, 7);
        this.renderer.print();
        System.out.println("(4) Gold deck         (5) Revealed    (6) Revealed");
    }
}
