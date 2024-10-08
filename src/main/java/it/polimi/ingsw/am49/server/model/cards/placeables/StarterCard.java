package it.polimi.ingsw.am49.server.model.cards.placeables;

import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.enumerations.Symbol;

import java.io.Serializable;
import java.util.*;

/**
 * Each player has only one of these cards and is the first card placed on the player board.
 */
public class StarterCard extends PlaceableCard implements Serializable {

    /**
     * The list of resources present in the center of the card's front.
     */
    private final List<Resource> centerResources;

    /**
     * The symbol present in the top right corner of the card's back.
     */
    private final Symbol trb;

    /**
     * The symbol present in the top left corner of the card's back.
     */
    private final Symbol tlb;

    /**
     * The symbol present in the bottom right corner of the card's back.
     */
    private final Symbol brb;

    /**
     * The symbol present in the bottom left corner of the card's back.
     */
    private final Symbol blb;

    /**
     * Constructs a new StarterCard, with the specified parameters.
     *
     * @param id the unique identifier of the card
     * @param tr the symbol in the top right corner of the card's front
     * @param tl the symbol in the top left corner of the card's front
     * @param br the symbol in the bottom right corner of the card's front
     * @param bl the symbol in the bottom left corner of the card's front
     * @param centerResources the list of resources in the center of the card's front
     * @param trb the symbol in the top right corner of the card's back
     * @param tlb the symbol in the top left corner of the card's back
     * @param brb the symbol in the bottom right corner of the card's back
     * @param blb the symbol in the bottom left corner of the card's back
     */
    public StarterCard(
            int id, Symbol tr, Symbol tl, Symbol br, Symbol bl, Resource resource, int points,
            List<Resource> centerResources, Symbol trb, Symbol tlb, Symbol brb, Symbol blb, boolean isGoldCard
    ) {
        super(id, tr, tl, br, bl, resource, points, new BasicPointsStrategy(), new HashMap<>(), isGoldCard);
        this.centerResources = new LinkedList<>(centerResources);
        this.trb = trb;
        this.tlb = tlb;
        this.brb = brb;
        this.blb = blb;
    }

    /**
     * Constructs a copy of another {@link StarterCard}.
     *
     * @param other the {@link StarterCard} that is being copied
     */
    public StarterCard(StarterCard other) {
        super(
            other.id,
            other.tr,
            other.tl,
            other.br,
            other.bl,
            other.resource,
            other.points,
            new BasicPointsStrategy(),
            new HashMap<>(),
            other.isGoldCard
        );
        this.centerResources = new LinkedList<>(other.centerResources);
        this.trb = other.trb;
        this.tlb = other.tlb;
        this.brb = other.brb;
        this.blb = other.blb;
    }

    @Override
    public Symbol getActiveTr() {
        return this.flipped ? this.trb : this.tr;
    }

    @Override
    public Symbol getActiveTl() {
        return this.flipped ? this.tlb : this.tl;
    }

    @Override
    public Symbol getActiveBr() {
        return this.flipped ? this.brb : this.br;
    }

    @Override
    public Symbol getActiveBl() {
        return this.flipped ? this.blb : this.bl;
    }

    @Override
    public List<Resource> getActiveCenterResources() {
        if (this.flipped) return new LinkedList<>();
        return new LinkedList<>(this.centerResources);
    }

    @Override
    public List<Resource> getCenterResources() {
        return Collections.unmodifiableList(centerResources);
    }

    @Override
    public StarterCard clone() {
        return new StarterCard(this);
    }

}
