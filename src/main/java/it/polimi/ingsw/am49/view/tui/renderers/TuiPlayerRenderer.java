package it.polimi.ingsw.am49.view.tui.renderers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.config.StaticConfig;
import it.polimi.ingsw.am49.model.enumerations.Color;
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
    private final boolean hiddenHand;
    private final boolean hiddenPersonalObjective;
    private final List<Integer> commonObjectives;
    private final TuiTextureManager textureManager;

    /**
     * Constructs a TuiPlayerRenderer with the specified virtual player, visibility, and common objectives.
     *
     * @param player the virtual player to render
     * @param hiddenHand whether the player's hand should be hidden
     * @param hiddenPersonalObjective whether the player's personal objective should be hidden
     * @param commonObjectives the list of common objectives
     */
    public TuiPlayerRenderer(VirtualPlayer player, boolean hiddenHand, boolean hiddenPersonalObjective, List<Integer> commonObjectives) {
        this.player = player;
        this.renderer = new TuiCardRenderer(110, 5);
        this.hiddenHand = hiddenHand;
        this.hiddenPersonalObjective = hiddenPersonalObjective;
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
                "Player: " + getColoredUsername(this.player)
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

        this.drawHand();
        this.drawPersonalObjective();

        this.drawCommonObjectives();
        this.renderer.print();
    }

    private void drawHand() {
        if (!this.hiddenHand) {
            List<Integer> hand = this.player.getHand();
            for (int i = 0; i < hand.size(); i++) {
                drawCard(hand.get(i), 7 + i * 16);
            }
        } else {
            List<Pair<Resource, Boolean>> hiddenHand = this.player.getHiddenHand();
            for (int i = 0; i < hiddenHand.size(); i++) {
                Pair<Resource, Boolean> pair = hiddenHand.get(i);
                ColoredChar[][] texture = this.getBackPlaceableTexture(pair.first, pair.second);
                if (texture != null) {
                    this.renderer.draw(texture, 7 + i * 16, 2);
                }
            }
        }
    }

    private void drawPersonalObjective() {
        if (!this.hiddenPersonalObjective) {
            Integer personalObjectiveId = this.player.getPersonalObjectiveId();
            drawCard(personalObjectiveId, 62);
        } else {
            ColoredChar[][] obTexture = this.textureManager.getBackTexture(BackTexture.OB);
            this.renderer.draw(obTexture, 62, 2);
        }
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

    protected String getColoredUsername(VirtualPlayer player) {
        if (player == null) return null;
        String offline = player.getPlaying() ? "" : " (offline)";
        return this.getColoredUsername(player.getUsername(), player.getColor()) + offline;
    }

    protected String getColoredUsername(String username, Color color) {
        if (color == null) return username;
        if (StaticConfig.tuiColors)
            return AnsiColor.fromColor(color) + username + AnsiColor.ANSI_RESET;
        else
            return username + "[" + color.name() + "]";
    }
}
