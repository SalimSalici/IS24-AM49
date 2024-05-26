package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.cards.Card;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.io.Serializable;
import java.util.*;

/**
 * Rapresents the cards that can be placed on the player board. This abstract class
 * provides the common methods for all the types of placeables cards.
 *
 * @see StarterCard
 * @see ResourceCard
 * @see GoldCard
 */
public abstract class PlaceableCard extends Card implements Serializable {
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
     * The resource the card belongs to
     */
    protected final Resource resource;

    /**
     * The amount of points given to the player assuming the condition shown on the card
     * is accomplished a single time.
     * In order to get the points the player must place this card with the face showing the
     * accomplishments facing up.
     */
    protected final int points;

    /**
     * Is rapresenting the required resources that must be on the player board
     * if the player wants to place the card's face showing the price.
     */
    protected final Map<Symbol, Integer> price;

    /**
     * Object used to call the strategy design pattern.
     */
    protected final PlacementPointsStrategy pointsStrategy;

    protected boolean isGoldCard;


    /**
     * Costructs a new PlaceableCard object with the specified symbols in the
     * corners and identified by id.
     *
     * @param id the unique identifier of the card
     * @param tr the symbol in the top right corner of the card's front
     * @param tl the symbol in the top left corner of the card's front
     * @param br the symbol in the bottom right corner of the card's front
     * @param bl the symbol in the bottom left corner of the card's front
     * @param resource the resource the card belongs to
     * @param points the amount of points given to the player assuming the condition shown on the card
     *               is accomplished a single time
     * @param pointsStrategy object used to call the strategy design pattern
     */
    public PlaceableCard(int id, Symbol tr, Symbol tl, Symbol br, Symbol bl, Resource resource,
                         int points, PlacementPointsStrategy pointsStrategy, Map<Symbol, Integer> price, boolean isGoldCard) {
        super(id);
        this.tr = tr;
        this.tl = tl;
        this.br = br;
        this.bl = bl;
        this.resource = resource;
        this.points = points;
        this.pointsStrategy = pointsStrategy;
        this.isGoldCard = isGoldCard;

        Map<Symbol, Integer> tempPrice = new HashMap<>( price );
        for (Symbol sym : Symbol.values())
            tempPrice.putIfAbsent(sym, 0);
        this.price = Collections.unmodifiableMap( tempPrice );
    }

    /**
     * Calculates the total amount of points given to the player, considering the number of times
     * the condition shown on the card is accomplished.
     *
     * @param playerBoard the whole player board, containing all the already placed cards
     * @param boardTile the object of type {@link BoardTile} rapresenting the tile in which the card is placed
     * @return the total points the player gets from placing the card
     */
    public int calculatePoints(PlayerBoard playerBoard, BoardTile boardTile) {
        if (!this.flipped)
            return pointsStrategy.execute(playerBoard, boardTile) * points;
        return 0;
    }

    /**
     *
     * @return the resource the card belongs to
     */
    public Resource getResource() {
        return resource;
    }

    /**
     *
     * @return the required resources that must be on the player board
     *         if the player wants to place the card's face showing the price
     */
    public Map<Symbol, Integer> getPrice() {
        return price;
    }

    /**
     *
     * @return the amount of points given to the player assuming the condition shown on the card
     * is accomplished a single time
     */
    public int getPoints() { return points; }
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
    public Symbol getActiveTr() {
        return this.flipped ? Symbol.EMPTY : tr;
    }

    /**
     * Gets the symbol in the top left corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the top left corner of the card's front if isn't
     *         flipped, the symbol in the top left corner of the card's back otherwise
     */
    public Symbol getActiveTl() {
        return this.flipped ? Symbol.EMPTY : tl;
    }

    /**
     * Gets the symbol in the bottom right corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the bottom right corner of the card's front if isn't
     *         flipped, the symbol in the bottom right corner of the card's back otherwise
     */
    public Symbol getActiveBr() {
        return this.flipped ? Symbol.EMPTY : br;
    }

    /**
     * Gets the symbol in the bottom left corner that is visible looking at the
     * player board.
     *
     * @return the symbol in the bottom left corner of the card's front if it isn't
     *         flipped, the symbol in the bottom left corner of the card's back otherwise
     */
    public Symbol getActiveBl() {
        return this.flipped ? Symbol.EMPTY : bl;
    }

    /**
     * Gets the resources in the center of the card's face that is visible looking at
     * the player board. If there aren't any resources the list returned is empty.
     *
     * @return the list of resources visible in the center of the card once placed
     */
    public List<Resource> getActiveCenterResources() {
        if (this.flipped) return this.getCenterResources();
        return new LinkedList<>();
    }

    /**
     * Gets the resources in the center of the only card's face where there actually are
     * some resources.
     * The front in the case of {@link StarterCard}'s cards.
     * The back in the case of {@link ResourceCard}'s and {@link GoldCard}'s cards.
     *
     * @return the list of mentioned resources
     */
    public List<Resource> getCenterResources() {
        List<Resource> centerResources = new LinkedList<>();
        centerResources.add(this.resource);
        return centerResources;
    }


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

    public boolean isGoldCard() { return isGoldCard; }
}
