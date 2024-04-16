package it.polimi.ingsw.am49.messages.mtc;

import java.util.List;

public class DrawAreaUpdateMTC extends MessageToClient{
    private final int remainingResources;
    private final int remainingGolds;
    private final List<Integer> revealedResources;
    private final List<Integer> revealedGolds;

    public DrawAreaUpdateMTC(int remainingResources, int remainingGolds, List<Integer> revealedResources, List<Integer> revealedGolds){
        super(MessageToClientType.CARD_DRAWN, "draw area updated");
        this.remainingResources = remainingResources;
        this.remainingGolds = remainingGolds;
        this.revealedResources = revealedResources;
        this.revealedGolds = revealedGolds;
    }

    public int getRemainingResources() {
        return remainingResources;
    }

    public int getRemainingGolds() {
        return remainingGolds;
    }

    public List<Integer> getRevealedResources() {
        return revealedResources;
    }

    public List<Integer> getRevealedGolds() {
        return revealedGolds;
    }
}
