package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;
import it.polimi.ingsw.am49.view.tui.textures.BackTexture;
import it.polimi.ingsw.am49.view.tui.textures.ColoredChar;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.util.List;

public class TuiPlayerRenderer {

    private final TuiCardRenderer renderer;
    private final VirtualPlayer player;
    private final boolean hidden;
    private final List<Integer> commonObjectives;
    private final TuiTextureManager textureManager;

    public TuiPlayerRenderer(VirtualPlayer player, boolean hidden, List<Integer> commonObjectives) {
        this.player = player;
        this.renderer = new TuiCardRenderer(110, 5);
        this.hidden = hidden;
        this.commonObjectives = commonObjectives;
        this.textureManager = TuiTextureManager.getInstance();
    }

    public void printInfo() {
        String availableSymbols =
                  "W(" + this.player.getActiveSymbols().get(Symbol.WOLVES) + ") | "
                + "L(" + this.player.getActiveSymbols().get(Symbol.LEAVES) + ") | "
                + "M(" + this.player.getActiveSymbols().get(Symbol.MUSHROOMS) + ") | "
                + "B(" + this.player.getActiveSymbols().get(Symbol.BUGS) + ") | "
                + "S(" + this.player.getActiveSymbols().get(Symbol.MANUSCRIPT) + ") | "
                + "I(" + this.player.getActiveSymbols().get(Symbol.INKWELL) + ") | "
                + "Q(" + this.player.getActiveSymbols().get(Symbol.QUILL) + ")";

        System.out.println(
                "Player: " + AnsiColor.fromColor(this.player.getColor()) + this.player.getUsername() + AnsiColor.ANSI_RESET
                + "     Points: " + this.player.getPoints()
                + "     Available symbols: " + availableSymbols
        );
    }

    public void printHandAndObjectives() {
        System.out.println("Hand" + " ".repeat(51) + "Personal obj." + " ".repeat(10) + "Common objectives");

        this.renderer.clear();

        Integer cardId;
        ColoredChar[][] texture;
        if (!this.hidden) {
            Integer[] hand = new Integer[3];
            for (int i = 0; i < this.player.getHand().size(); i++) {
                hand[i] = this.player.getHand().get(i);
            }

            cardId = hand[0];
            if (cardId != null) {
                texture = TuiTextureManager.getInstance().getTexture(cardId, false);
                this.renderer.draw(texture, 7, 2);
            }

            cardId = hand[1];
            if (cardId != null) {
                texture = TuiTextureManager.getInstance().getTexture(cardId, false);
                this.renderer.draw(texture, 23, 2);
            }

            cardId = hand[2];
            if (cardId != null) {
                texture = TuiTextureManager.getInstance().getTexture(cardId, false);
                this.renderer.draw(texture, 39, 2);
            }

            cardId = this.player.getPersonalObjectiveId();
            texture = TuiTextureManager.getInstance().getTexture(cardId, hidden);
            this.renderer.draw(texture, 62, 2);
        } else {
            Resource[] hiddenHand = new Resource[3];
            for (int i = 0; i < this.player.getHiddenHandAsResources().size(); i++) {
                hiddenHand[i] = this.player.getHiddenHandAsResources().get(i);
            }

            Resource cardResource = hiddenHand[0];
            if (cardResource != null)
                this.renderer.draw(this.getBackTextureFromResource(cardResource), 7, 2);

            cardResource = hiddenHand[1];
            if (cardResource != null)
                this.renderer.draw(this.getBackTextureFromResource(cardResource), 23, 2);

            cardResource = hiddenHand[2];
            if (cardResource != null)
                this.renderer.draw(this.getBackTextureFromResource(cardResource), 39, 2);

            this.renderer.draw(textureManager.getBackTexture(BackTexture.OB), 62, 2);
        }

        cardId = this.commonObjectives.getFirst();
        texture = TuiTextureManager.getInstance().getTexture(cardId, false);
        this.renderer.draw(texture, 85, 2);

        cardId = this.commonObjectives.get(1);
        texture = TuiTextureManager.getInstance().getTexture(cardId, false);
        this.renderer.draw(texture, 101, 2);

        this.renderer.print();
    }

    private ColoredChar[][] getBackTextureFromResource(Resource resource) {
        return switch (resource) {
            case WOLVES -> textureManager.getBackTexture(BackTexture.RB);
            case LEAVES -> textureManager.getBackTexture(BackTexture.RG);
            case MUSHROOMS -> textureManager.getBackTexture(BackTexture.RR);
            case BUGS -> textureManager.getBackTexture(BackTexture.RP);
        };
    }

    public void print() {
        this.printHandAndObjectives();
        System.out.println();
        this.printInfo();
    }
}
