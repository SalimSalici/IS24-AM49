package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.io.Serializable;
import java.util.Map;

/**
 * Are a type of {@link PlaceableCard} that can be placed in the board.
 */
public class ResourceCard extends PlaceableCard implements Serializable {

    /**
     * Constructs a new {@link ResourceCard} object of the specified resource type,
     * identified by id, with the specified symbols in the corners.
     * Also, the {@link ResourceCard} object will have the {@link BasicPointsStrategy},
     * and for every iteration of this accomplishment the card will give the
     * specified amount of points.
     *
     * @param id unique identifier of the card
     * @param tr the symbol in the top right corner of the card's front
     * @param tl the symbol in the top left corner of the card's front
     * @param br the symbol in the bottom right corner of the card's front
     * @param bl the symbol in the bottom left corner of the card's front
     * @param resource an object of type {@link Resource}
     * @param points the amount of points given to the player assuming the condition shown on the card
     *               is accomplished a single time
     * @param pointsStrategy indicates the strategy to use for calculating points
     * @param price A map with the cost to play the card
     */
    public ResourceCard(int id, Symbol tr, Symbol tl, Symbol br, Symbol bl, Resource resource, int points, PlacementPointsStrategy  pointsStrategy, Map<Symbol, Integer> price) {
        super(id, tr, tl, br, bl, resource, points, new BasicPointsStrategy(), price);
    }

    /**
     * Costructs a copy of another {@link PlaceableCard}.
     *
     * @param other the {@link PlaceableCard} that is being copied
     */
    public ResourceCard(ResourceCard other) {
        super(other.id, other.tr, other.tl, other.br, other.bl, other.resource, other.points, new BasicPointsStrategy(), other.price);
    }

    @Override
    public ResourceCard clone() {
        return new ResourceCard(this);
    }
}
