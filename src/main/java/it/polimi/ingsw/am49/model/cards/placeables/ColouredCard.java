package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.model.players.BoardTile;
import it.polimi.ingsw.am49.model.players.PlayerBoard;

import java.util.LinkedList;
import java.util.List;

/**
 * Rapresents the other cards that can be placed on the player board except the starting card.
 * Each one of these cards belongs to one of the four resources.
 * This abstract class provides the common methods for all the types of coloured cards.
 *
 * @see ResourceCard
 * @see GoldCard
 */
public abstract class ColouredCard extends PlaceableCard {
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
     * Object used to call the strategy design pattern.
     */
    protected final PlacementPointsStrategy pointsStrategy;

    /**
     * Constructs a new {@link ColouredCard} object of the specified resource type,
     * identified by id, with the specified symbols in the corners.
     * Also, depending on the type of accomplishment, the {@link ColouredCard} object
     * will have a certain type of pointsStrategy, and for every iteration
     * of this accomplishment the card will give the specified amount of
     * points.
     *
     * @param id unique identifier of the card
     * @param tr the symbol in the top right corner of the card's front
     * @param tl the symbol in the top left corner of the card's front
     * @param br the symbol in the bottom right corner of the card's front
     * @param bl the symbol in the bottom left corner of the card's front
     * @param resource the resource the card belongs to
     * @param points the amount of points given to the player assuming the condition shown on the card
     *               is accomplished a single time
     * @param pointsStrategy object used to call the strategy design pattern
     */
    public ColouredCard(int id, Symbol tr, Symbol tl, Symbol br, Symbol bl,
                        Resource resource, int points, PlacementPointsStrategy pointsStrategy) {
        super(id, tr, tl, br, bl);
        this.resource = resource;
        this.points = points;
        this.pointsStrategy = pointsStrategy;
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
        return pointsStrategy.execute(playerBoard, boardTile) * points;
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
     * @return the amount of points given to the player assuming the condition shown on the card
     * is accomplished a single time
     */
    public int getPoints() {
        return points;
    }

    @Override
    public Symbol getActiveTr() {
        return this.flipped ? Symbol.EMPTY : tr;
    }

    @Override
    public Symbol getActiveTl() {
        return this.flipped ? Symbol.EMPTY : tl;
    }

    @Override
    public Symbol getActiveBr() {
        return this.flipped ? Symbol.EMPTY : br;
    }

    @Override
    public Symbol getActiveBl() {
        return this.flipped ? Symbol.EMPTY : bl;
    }

    @Override
    public List<Resource> getCenterResources() {
        List<Resource> centerResources = new LinkedList<>();
        centerResources.add(this.resource);
        return centerResources;
    }

    @Override
    public List<Resource> getActiveCenterResources() {
        if (this.flipped) return this.getCenterResources();
        return new LinkedList<>();
    }

}
