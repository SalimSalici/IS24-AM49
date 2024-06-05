package it.polimi.ingsw.am49.model.events;

import it.polimi.ingsw.am49.controller.gameupdates.EndGameUpdate;
import it.polimi.ingsw.am49.controller.gameupdates.GameUpdate;
import it.polimi.ingsw.am49.model.enumerations.GameEventType;
import it.polimi.ingsw.am49.model.players.Player;

import java.util.HashMap;
import java.util.Map;

public record EndgameEvent(Map<Player, Integer> playersToAchievedObjectives) implements GameEvent {
    @Override
    public GameEventType getType() {
        return GameEventType.END_GAME;
    }

    @Override
    public GameUpdate toGameUpdate() {
        HashMap<String, Integer[]> updateMap = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : this.playersToAchievedObjectives.entrySet()) {
            Player p = entry.getKey();
            Integer[] pointsAndObjectives = new Integer[2];
            pointsAndObjectives[0] = p.getPoints();
            pointsAndObjectives[1] = entry.getValue();
            updateMap.put(entry.getKey().getUsername(), pointsAndObjectives);
        }
        return new EndGameUpdate(updateMap);
    }
}
