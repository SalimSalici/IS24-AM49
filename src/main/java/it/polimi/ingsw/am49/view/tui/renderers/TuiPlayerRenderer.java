package it.polimi.ingsw.am49.view.tui.renderers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.util.Pair;
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

        if (!this.hidden) {
            this.drawVisibleHandAndObjective();
        } else {
            this.drawHiddenHandAndObjective();
        }

        this.drawCommonObjectives();
        this.renderer.print();
    }

    private void drawVisibleHandAndObjective() {
        List<Integer> hand = this.player.getHand();

        for (int i = 0; i < hand.size(); i++) {
            drawCard(hand.get(i), 7 + i * 16);
        }

        Integer personalObjectiveId = this.player.getPersonalObjectiveId();
        drawCard(personalObjectiveId, 62);
    }

    private void drawHiddenHandAndObjective() {
        List<Pair<Resource, Boolean>> hiddenHand = this.player.getHiddenHand();

        for (int i = 0; i < hiddenHand.size(); i++) {
            Pair<Resource, Boolean> pair = hiddenHand.get(i);
            ColoredChar[][] texture = getBackPlaceableTexture(pair.first, pair.second);
            if (texture != null) {
                this.renderer.draw(texture, 7 + i * 16, 2);
            }
        }

        ColoredChar[][] obTexture = textureManager.getBackTexture(BackTexture.OB);
        this.renderer.draw(obTexture, 62, 2);
    }

    private void drawCommonObjectives() {
        for (int i = 0; i < this.commonObjectives.size(); i++) {
            Integer cardId = this.commonObjectives.get(i);
            drawCard(cardId, 85 + i * 16);
        }
    }

    private void drawCard(Integer cardId, int xPosition) {
        if (cardId != null) {
            ColoredChar[][] texture = TuiTextureManager.getInstance().getTexture(cardId, false);
            this.renderer.draw(texture, xPosition, 2);
        }
    }

    private ColoredChar[][] getBackPlaceableTexture(Resource resource, boolean gold) {
        if (!gold)
            return switch (resource) {
                case WOLVES -> textureManager.getBackTexture(BackTexture.RB);
                case LEAVES -> textureManager.getBackTexture(BackTexture.RG);
                case MUSHROOMS -> textureManager.getBackTexture(BackTexture.RR);
                case BUGS -> textureManager.getBackTexture(BackTexture.RP);
            };
        else
            return switch (resource) {
                case WOLVES -> textureManager.getBackTexture(BackTexture.GB);
                case LEAVES -> textureManager.getBackTexture(BackTexture.GG);
                case MUSHROOMS -> textureManager.getBackTexture(BackTexture.GR);
                case BUGS -> textureManager.getBackTexture(BackTexture.GP);
            };
    }

    public void print() {
        this.printHandAndObjectives();
        System.out.println();
        this.printInfo();
    }
}
