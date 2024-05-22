package it.polimi.ingsw.am49.view.tui;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.view.tui.textures.AnsiColor;
import it.polimi.ingsw.am49.view.tui.textures.ColoredChar;
import it.polimi.ingsw.am49.view.tui.textures.TuiTextureManager;

import java.util.List;

public class TuiPlayerRenderer {

    private final TuiCardRenderer renderer;
    private final VirtualPlayer player;
    private final boolean hidden;
    private final List<Integer> commonObjectives;

    public TuiPlayerRenderer(VirtualPlayer player, boolean hidden, List<Integer> commonObjectives) {
        this.player = player;
        this.renderer = new TuiCardRenderer(110, 5);
        this.hidden = hidden;
        this.commonObjectives = commonObjectives;
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

        int cardId = this.player.getHand().getFirst();
        ColoredChar[][] texture = TuiTextureManager.getInstance().getTexture(cardId, hidden);
        this.renderer.draw(texture, 7, 2);

        cardId = this.player.getHand().get(1);
        texture = TuiTextureManager.getInstance().getTexture(cardId, hidden);
        this.renderer.draw(texture, 23, 2);

        cardId = this.player.getHand().get(2);
        texture = TuiTextureManager.getInstance().getTexture(cardId, hidden);
        this.renderer.draw(texture, 39, 2);

        cardId = this.player.getPersonalObjectiveId();
        texture = TuiTextureManager.getInstance().getTexture(cardId, hidden);
        this.renderer.draw(texture, 62, 2);

        cardId = this.commonObjectives.getFirst();
        texture = TuiTextureManager.getInstance().getTexture(cardId, false);
        this.renderer.draw(texture, 85, 2);

        cardId = this.commonObjectives.get(1);
        texture = TuiTextureManager.getInstance().getTexture(cardId, false);
        this.renderer.draw(texture, 101, 2);

        this.renderer.print();
    }

    public void print() {
        this.printHandAndObjectives();
        System.out.println();
        this.printInfo();
    }
}
