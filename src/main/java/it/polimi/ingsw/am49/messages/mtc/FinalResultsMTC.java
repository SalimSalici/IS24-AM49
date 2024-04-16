package it.polimi.ingsw.am49.messages.mtc;

import java.util.HashMap;
import java.util.Map;

public class FinalResultsMTC extends MessageToClient{
    private final Map<String, Integer> playersToPoints;

    public FinalResultsMTC(Map<String, Integer> playersToPoints){
        super(MessageToClientType.FINAL_RESULTS, "FINAL POINTS: ");
        this.playersToPoints = new HashMap<>(playersToPoints);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.playersToPoints.toString();
    }

    public Map<String, Integer> getPlayersToPoints() {
        return playersToPoints;
    }
}
