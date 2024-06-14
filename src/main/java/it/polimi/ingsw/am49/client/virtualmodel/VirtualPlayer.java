package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.model.enumerations.Resource;
import it.polimi.ingsw.am49.model.enumerations.Symbol;
import it.polimi.ingsw.am49.util.Observable;
import it.polimi.ingsw.am49.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.util.*;


/**
 * Represents a virtual player in the game.
 *
 * The virtual player holds all necessary information that would be visible and
 * manipulatable by the player during a game session.
 *
 * This class extends {@link Observable}, allowing it to notify observers when changes occur.
 */
public class VirtualPlayer extends Observable {
    private final String username;
    private int points;
    private int completedObjectives;
    private final VirtualBoard board;
    private List<Integer> hand;
    private List<Pair<Resource, Boolean>> hiddenHand;
    private Map<Symbol, Integer> activeSymbols;
    private final Color color;
    private int personalObjectiveId;
    private final List<VirtualChatMessage> messages;

    /**
     * Constructs a VirtualPlayer with a specified username and color.
     * Initializes the hand, hidden hand, and active symbols collection.
     *
     * @param username the username of the player.
     * @param color the color representing the player in the game.
     */
    public VirtualPlayer(String username, Color color) {
        this.username = username;
        this.hand = new LinkedList<>();
        this.hiddenHand = new LinkedList<>();
        this.color = color;
        this.activeSymbols = new HashMap<>();
        for (Symbol s : Symbol.values()) {
            this.activeSymbols.put(s, 0);
        }
        this.board = new VirtualBoard();
        this.messages = new ArrayList<>();
    }

    /**
     * @return the username of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the virtual board.
     */
    public VirtualBoard getBoard() {
        return board;
    }

    /**
     * @return the current point total.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the points of the player.
     *
     * @param points the new point total to set.
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * @return the number of completed objectives
     */
    public int getCompletedObjectives() {
        return this.completedObjectives;
    }

    /**
     * Sets the number of objectives completed by the player.
     *
     * @param completedObjectives the new number of completed objectives
     */
    public void setCompletedObjectives(int completedObjectives) {
        this.completedObjectives = completedObjectives;
    }

    /**
     * @return a map of active symbols and their counts
     */
    public Map<Symbol, Integer> getActiveSymbols() {
        return activeSymbols;
    }


    /**
     * Sets the active symbols and their counts for the player.
     *
     * @param activeSymbols the new map of active symbols and their counts
     */
    public void setActiveSymbols(Map<Symbol, Integer> activeSymbols) {
        this.activeSymbols = activeSymbols;
    }

    /**
     * @return the color of the player
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the JavaFX color of the player
     */
    public javafx.scene.paint.Color getJavaFXColor() {
        return switch (color) {
            case BLUE -> javafx.scene.paint.Color.BLUE;
            case RED -> javafx.scene.paint.Color.RED;
            case GREEN -> javafx.scene.paint.Color.GREEN;
            case YELLOW -> javafx.scene.paint.Color.YELLOW;
        };
    }

    /**
     * @return the hand of the player
     */
    public List<Integer> getHand() {
        return hand;
    }

    /**
     * @return the ID of the personal objective card
     */
    public int getPersonalObjectiveId() {return personalObjectiveId;}

    /**
     * Sets the hand of the player.
     *
     * @param hand the new hand of the player
     */
    public void setHand(List<Integer> hand) {
        this.hand = hand;
    }

    /**
     * @return the hidden hand of the player
     */
    public List<Pair<Resource, Boolean>> getHiddenHand() {
        return hiddenHand;
    }

    /**
     * Sets the hidden hand of the player.
     *
     * @param hiddenHand the new hidden hand of the player
     */
    public void setHiddenHand(List<Pair<Resource, Boolean>> hiddenHand) {
        this.hiddenHand = hiddenHand;
    }

    /**
     * Sets the personal objective of the player.
     *
     * @param personalObjectiveId the new ID of the personal objective card
     */
    public void setPersonalObjectiveId(int personalObjectiveId) {this.personalObjectiveId = personalObjectiveId;}

    /**
     * @return the starter card, or null if no starter tile is found
     */
    public VirtualCard getStarterCard() {
        VirtualTile starterTile = this.board.getStarterTile();
        if (starterTile != null)
            return starterTile.getCard();
        return null;
    }

    public void setMessage(String text, String sender, String recipient, LocalTime time) {
        messages.add( new VirtualChatMessage(text, sender, recipient, time));
    }

    public List<VirtualChatMessage> getMessages() { return messages; }

    public List<String> getGlobalChat(){
        return messages.stream()
                .filter(m -> !m.isPrivate())
                .map(m -> m.getTimeAsString() + m.getSender() +": "+ m.getText())
                .toList();
    }

    public List<String> getPrivateChat(VirtualPlayer recipient){
        return messages.stream()
                .filter(m -> m.getRecipient().equals(recipient.username))
                .map(m -> m.getTimeAsString() + " " + m.getText())
                .toList();
    }

}
