package it.polimi.ingsw.am49.model.cards.placeables;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.io.Serializable;
import java.util.Map;

/**
 * Are a type of {@link PlaceableCard} that can be placed in the board.
 * If the player wants to put down this card facing upwords he needs to
 * make sure that the required resources are present in the play area.
 */
public class GoldCard extends PlaceableCard implements Serializable {



    /**
     * Constructs a new {@link GoldCard} object of the specified resource type,
     * identified by id, with the specified symbols in the corners.
     * Also, the {@link GoldCard} object will have an instance of a child of
     * {@link PlacementPointsStrategy}, and for every iteration of this accomplishment
     * the card will give the specified amount of points.
     *
     * @param id unique identifier of the card
     * @param tr the symbol in the top right corner of the card's front
     * @param tl the symbol in the top left corner of the card's front
     * @param br the symbol in the bottom right corner of the card's front
     * @param bl the symbol in the bottom left corner of the card's front
     * @param resource the resource the card belongs to
     * @param points the amount of points given to the player assuming the condition shown on the card
     *               is accomplished a single time
     */
    public GoldCard(int id, Symbol tr, Symbol tl, Symbol br, Symbol bl,
                    Resource resource, int points, PlacementPointsStrategy pointsStrategy,
                    Map<Symbol, Integer> price, boolean isGoldCard) {
        super(id, tr, tl, br, bl, resource, points, pointsStrategy, price, isGoldCard);
    }

    /**
     * Costructs a copy of another {@link GoldCard}.
     *
     * @param other the {@link GoldCard} that is being copied
     */
    public GoldCard(GoldCard other) {
        super(other.id, other.tr, other.tl, other.br, other.bl, other.resource, other.points, other.pointsStrategy, other.price, other.isGoldCard);
    }

    @Override
    public GoldCard clone() {
        return new GoldCard(this);
    }

}
