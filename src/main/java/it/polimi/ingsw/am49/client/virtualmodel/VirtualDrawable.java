package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.util.Observable;

import java.util.List;

/**
 * Represents a virtual drawable entity in the game that tracks resources and golds.
 */
public class VirtualDrawable extends Observable {
    private int remainingResources;
    private int remainingGolds;
    private Resource deckTopResource;
    private Resource deckTopGold;
    private List<Integer> revealedResourcesIds;
    private List<Integer> revealedGoldsIds;

    /**
     * Constructs a new VirtualDrawable with specified initial values.
     *
     * @param remainingResources the initial count of remaining resources
     * @param remainingGolds the initial count of remaining golds
     * @param deckTopResource the resource at the top of the deck
     * @param deckTopGold the gold resource at the top of the deck
     * @param revealedResourcesIds list of IDs of revealed resources
     * @param revealedGoldsIds list of IDs of revealed golds
     */
    public VirtualDrawable(int remainingResources, int remainingGolds, Resource deckTopResource, Resource deckTopGold,  List<Integer> revealedResourcesIds, List<Integer> revealedGoldsIds){
        this.remainingResources = remainingResources;
        this.remainingGolds = remainingGolds;
        this.deckTopResource = deckTopResource;
        this.deckTopGold = deckTopGold;
        this.revealedResourcesIds = revealedResourcesIds;
        this.revealedGoldsIds = revealedGoldsIds;
    }

    /**
     * Returns the resource at the top of the gold deck.
     *
     * @return the top gold resource
     */
    public Resource getDeckTopGold() {
        return deckTopGold;
    }

    /**
     * Returns the list of IDs for revealed resources.
     *
     * @return list of revealed resource IDs
     */
    public List<Integer> getRevealedResourcesIds() {
        return revealedResourcesIds;
    }

    /**
     * Returns the resource at the top of the resource deck.
     *
     * @return the top resource
     */
    public Resource getDeckTopResource() {
        return deckTopResource;
    }

    /**
     * Returns the list of IDs for revealed golds.
     *
     * @return list of revealed gold IDs
     */
    public List<Integer> getRevealedGoldsIds() {
        return revealedGoldsIds;
    }

    /**
     * Sets the resource at the top of the gold deck.
     *
     * @param deckTopGold the new top gold resource
     */
    public void setDeckTopGold(Resource deckTopGold) {
        this.deckTopGold = deckTopGold;
    }

    /**
     * Sets the resource at the top of the resource deck.
     *
     * @param deckTopResource the new top resource
     */
    public void setDeckTopResource(Resource deckTopResource) {
        this.deckTopResource = deckTopResource;
    }

    /**
     * Sets the list of revealed gold IDs.
     *
     * @param revealedGoldsIds the new list of revealed gold IDs
     */
    public void setRevealedGoldsIds(List<Integer> revealedGoldsIds) {
        this.revealedGoldsIds = revealedGoldsIds;
    }

    /**
     * Sets the list of revealed resource IDs.
     *
     * @param revealedResourcesIds the new list of revealed resource IDs
     */
    public void setRevealedResourcesIds(List<Integer> revealedResourcesIds) {
        this.revealedResourcesIds = revealedResourcesIds;
    }

    /**
     * Returns the count of remaining resources.
     *
     * @return the remaining resources
     */
    public int getRemainingResources() {
        return remainingResources;
    }

    /**
     * Sets the count of remaining resources.
     *
     * @param remainingResources the new count of remaining resources
     */
    public void setRemainingResources(int remainingResources) {
        this.remainingResources = remainingResources;
    }

    /**
     * Returns the count of remaining golds.
     *
     * @return the remaining golds
     */
    public int getRemainingGolds() {
        return remainingGolds;
    }

    /**
     * Sets the count of remaining golds.
     *
     * @param remainingGolds the new count of remaining golds
     */
    public void setRemainingGolds(int remainingGolds) {
        this.remainingGolds = remainingGolds;
    }
}
