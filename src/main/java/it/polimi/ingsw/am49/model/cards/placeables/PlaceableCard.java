package it.polimi.am49.cn_demo.model.cards.placeables;

import com.google.gson.Gson;
import it.polimi.am49.cn_demo.model.cards.Card;
import it.polimi.am49.cn_demo.model.decks.DeckLoader;
import it.polimi.am49.cn_demo.model.enumerations.Resource;
import it.polimi.am49.cn_demo.model.enumerations.Symbol;

import java.util.List;

/**
 * Rapresents the cards that can be placed on the player board. This abstract class
 * provides the common methods for all the types of placeables cards.
 *
 * @see StarterCard
 * @see ColouredCard
 */
public abstract class PlaceableCard extends Card {
    /**
     * The symbol present in the top right corner of the card's front.
     */
    protected final Symbol tr;

    /**
     * The symbol present in the top left corner of the card's front.
     */
    protected final Symbol tl;

    /**
     * The symbol present in the bottom right corner of the card's front.
     */
    protected final Symbol br;

    /**
     * The symbol present in the bottom left corner of the card's front.
     */
    protected final Symbol bl;

    /**
     * Is true if the card is placed flipped on the player board.
     */
    protected boolean flipped;

    /**
     * Costructs a new PlaceableCard object with the specified symbols in the
     * corners and identified by id.
     *
     * @param id the unique identifier of the card
     * @param tr the symbol in the top right corner of the card's front
     * @param tl the symbol in the top left corner of the card's front
     * @param br the symbol in the bottom right corner of the card's front
     * @param bl the symbol in the bottom left corner of the card's front
     */
    public PlaceableCard(int id, Symbol tr, Symbol tl, Symbol br, Symbol bl) {
        super(id);
        this.tr = tr;
        this.tl = tl;
        this.br = br;
        this.bl = bl;
    }

    /**
     *
     * @return the symbol in the top right corner of the card's front
     */
    public Symbol getTr() {
        return tr;
    }

    /**
     *
     * @return the symbol in the top left corner of the card's front
     */
    public Symbol getTl() {
        return tl;
    }

    /**
     *
     * @return the symbol in the bottom right corner of the card's front
     */
    public Symbol getBr() {
        return br;
    }

    /**
     *
     * @return the symbol in the bottom left corner of the card's front
     */
    public Symbol getBl() {
        return bl;
    }

    /**
     * Gets the symbol in the top right corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the top right corner of the card's front if isn't
     *         flipped, the symbol in the top right corner of the card's back otherwise
     */
    public abstract Symbol getActiveTr();

    /**
     * Gets the symbol in the top left corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the top left corner of the card's front if isn't
     *         flipped, the symbol in the top left corner of the card's back otherwise
     */
    public abstract Symbol getActiveTl();

    /**
     * Gets the symbol in the bottom right corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the bottom right corner of the card's front if isn't
     *         flipped, the symbol in the bottom right corner of the card's back otherwise
     */
    public abstract Symbol getActiveBr();

    /**
     * Gets the symbol in the bottom left corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the bottom left corner of the card's front if it isn't
     *         flipped, the symbol in the bottom left corner of the card's back otherwise
     */
    public abstract Symbol getActiveBl();

    /**
     * Gets the resources in the center of the card's face that is visible looking at
     * the player board. If there aren't any resources the list returned is empty.
     *
     * @return the list of resources visible in the center of the card once placed
     */
    public abstract List<Resource> getActiveCenterResources();

    /**
     * Gets the resources in the center of the only card's face where there actually are
     * some resources.
     * The front in the case of {@link StarterCard}'s cards.
     * The back in the case of {@link ColouredCard}'s cards.
     *
     * @return the list of mentioned resources
     */
    public abstract List<Resource> getCenterResources();

    /**
     *
     * @return true if the card on the playaer board is placed flipped, false otherwise
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     *
     * @param flipped sets the orientation of the card
     */
    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}
