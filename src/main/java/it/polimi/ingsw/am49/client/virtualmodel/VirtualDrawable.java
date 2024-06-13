package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.util.Observable;

import java.util.List;

public class VirtualDrawable extends Observable {
    private int remainingResources;
    private int remainingGolds;
    private Resource deckTopResource;
    private Resource deckTopGold;
    private List<Integer> revealedResourcesIds;
    private List<Integer> revealedGoldsIds;

    public VirtualDrawable(int remainingResources, int remainingGolds, Resource deckTopResource, Resource deckTopGold,  List<Integer> revealedResourcesIds, List<Integer> revealedGoldsIds){
        this.remainingResources = remainingResources;
        this.remainingGolds = remainingGolds;
        this.deckTopResource = deckTopResource;
        this.deckTopGold = deckTopGold;
        this.revealedResourcesIds = revealedResourcesIds;
        this.revealedGoldsIds = revealedGoldsIds;
    }
    public Resource getDeckTopGold() {
        return deckTopGold;
    }
    public List<Integer> getRevealedResourcesIds() {
        return revealedResourcesIds;
    }
    public Resource getDeckTopResource() {
        return deckTopResource;
    }
    public List<Integer> getRevealedGoldsIds() {
        return revealedGoldsIds;
    }
    public void setDeckTopGold(Resource deckTopGold) {
        this.deckTopGold = deckTopGold;
    }
    public void setDeckTopResource(Resource deckTopResource) {
        this.deckTopResource = deckTopResource;
    }
    public void setRevealedGoldsIds(List<Integer> revealedGoldsIds) {
        this.revealedGoldsIds = revealedGoldsIds;
    }
    public void setRevealedResourcesIds(List<Integer> revealedResourcesIds) {
        this.revealedResourcesIds = revealedResourcesIds;
    }
    public int getRemainingResources() {
        return remainingResources;
    }
    public void setRemainingResources(int remainingResources) {
        this.remainingResources = remainingResources;
    }
    public int getRemainingGolds() {
        return remainingGolds;
    }
    public void setRemainingGolds(int remainingGolds) {
        this.remainingGolds = remainingGolds;
    }
}
