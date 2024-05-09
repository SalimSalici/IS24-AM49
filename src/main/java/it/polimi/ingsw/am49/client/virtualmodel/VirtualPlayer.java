package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Symbol;

import java.util.Map;

public class VirtualPlayer {
    private String username;
    private int points;
    private VirtualBoard board;
    private Map<Symbol, Integer> activeSymbols;
    private Color color;

    public VirtualPlayer(String username, Color color) {
        this.username = username;
        this.color = color;

    }

    public String getUsername() {
        return username;
    }
}
