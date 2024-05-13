package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.util.Observable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VirtualPlayer extends Observable {
    private final String username;
    private int points;
    private final VirtualBoard board;
    private List<Integer> hand;
    private List<Resource> hiddenHand;
    private Map<Symbol, Integer> activeSymbols;
    private final Color color;

    public VirtualPlayer(String username, Color color) {
        this.username = username;
        this.hand = new LinkedList<>();
        this.hiddenHand = new LinkedList<>();
        this.color = color;
        this.board = new VirtualBoard();
    }

    public String getUsername() {
        return username;
    }

    public VirtualBoard getBoard() {
        return board;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Map<Symbol, Integer> getActiveSymbols() {
        return activeSymbols;
    }

    public void setActiveSymbols(Map<Symbol, Integer> activeSymbols) {
        this.activeSymbols = activeSymbols;
    }

    public Color getColor() {
        return color;
    }

    public List<Integer> getHand() {
        return hand;
    }

    public void setHand(List<Integer> hand) {
        this.hand = hand;
    }

    public List<Resource> getHiddenHand() {
        return hiddenHand;
    }

    public void setHiddenHand(List<Resource> hiddenHand) {
        this.hiddenHand = hiddenHand;
    }
}
