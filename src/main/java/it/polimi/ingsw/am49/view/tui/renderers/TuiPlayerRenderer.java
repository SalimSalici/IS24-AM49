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

/**
 * The TuiPlayerRenderer class is responsible for rendering the player's information,
 * hand, and objectives on the TUI.
 */
public class TuiPlayerRenderer {

    private final TuiCardRenderer renderer;
    private final VirtualPlayer player;
    private final boolean hidden;
    private final List<Integer> commonObjectives;
    private final TuiTextureManager textureManager;

    /**
     * Constructs a TuiPlayerRenderer with the specified virtual player, visibility, and common objectives.
     *
     * @param player the virtual player to render
     * @param hidden whether the player's hand and personal objective should be hidden
     * @param commonObjectives the list of common objectives
     */
    public TuiPlayerRenderer(VirtualPlayer player, boolean hidden, List<Integer> commonObjectives) {
        this.player = player;
        this.renderer = new TuiCardRenderer(110, 5);
        this.hidden = hidden;
        this.commonObjectives = commonObjectives;
        this.textureManager = TuiTextureManager.getInstance();
    }

    /**
     * Prints the player's information including username, points, and available symbols.
     */
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

    /**
     * Prints the player's hand and objectives to the console.
     */
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

    /**
     * Draws the player's visible hand and personal objective.
     */
    private void drawVisibleHandAndObjective() {
        List<Integer> hand = this.player.getHand();

        for (int i = 0; i < hand.size(); i++) {
            drawCard(hand.get(i), 7 + i * 16);
        }

        Integer personalObjectiveId = this.player.getPersonalObjectiveId();
        drawCard(personalObjectiveId, 62);
    }

    /**
     * Draws the player's hidden hand and personal objective.
     */
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

    /**
     * Draws the common objectives.
     */
    private void drawCommonObjectives() {
        for (int i = 0; i < this.commonObjectives.size(); i++) {
            Integer cardId = this.commonObjectives.get(i);
            drawCard(cardId, 85 + i * 16);
        }
    }

    /**
     * Draws a card at the specified x position.
     *
     * @param cardId the ID of the card to draw
     * @param xPosition the x-coordinate where the card should be drawn
     */
    private void drawCard(Integer cardId, int xPosition) {
        if (cardId != null) {
            ColoredChar[][] texture = TuiTextureManager.getInstance().getTexture(cardId, false);
            this.renderer.draw(texture, xPosition, 2);
        }
    }

    /**
     * Gets the back texture for a placeable resource.
     *
     * @param resource the resource type
     * @param gold whether the resource is gold
     * @return the back texture for the resource
     */
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

    /**
     * Prints the player's hand, objectives, and information to the console.
     */
    public void print() {
        this.printHandAndObjectives();
        System.out.println();
        this.printInfo();
    }
}
