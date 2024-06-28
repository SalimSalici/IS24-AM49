package it.polimi.ingsw.am49.client.virtualmodel;

import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.enumerations.Resource;
import it.polimi.ingsw.am49.common.enumerations.Symbol;
import it.polimi.ingsw.am49.common.util.Observable;
import it.polimi.ingsw.am49.common.util.Pair;

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

    /**
     * The username of the player.
     */
    private final String username;

    /**
     * The points scored by the player.
     */
    private int points;

    /**
     * The number of completed objectives by the player.
     */
    private int completedObjectives;

    /**
     * The virtual board associated with the player.
     */
    private final VirtualBoard board;

    /**
     * The list of card IDs in the player's hand.
     */
    private List<Integer> hand;

    /**
     * The list of hidden resources in the player's hand.
     */
    private List<Pair<Resource, Boolean>> hiddenHand;

    /**
     * The map of active symbols and their counts for the player.
     */
    private Map<Symbol, Integer> activeSymbols;

    /**
     * The color representing the player in the game.
     */
    private final Color color;

    /**
     * The ID of the personal objective card.
     */
    private int personalObjectiveId;

    /**
     * The list of chat messages for the player.
     */
    private final List<VirtualChatMessage> messages;

    /**
     * The flag indicating if the player is currently playing.
     */
    private boolean playing = true;

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
     * Sets the playing status of the player.
     *
     * @param playing the new playing status.
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * @return the playing status of the player.
     */
    public boolean getPlaying(){
        return playing;
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
     * @return the number of completed objectives.
     */
    public int getCompletedObjectives() {
        return this.completedObjectives;
    }

    /**
     * Sets the number of objectives completed by the player.
     *
     * @param completedObjectives the new number of completed objectives.
     */
    public void setCompletedObjectives(int completedObjectives) {
        this.completedObjectives = completedObjectives;
    }

    /**
     * @return a map of active symbols and their counts.
     */
    public Map<Symbol, Integer> getActiveSymbols() {
        return activeSymbols;
    }

    /**
     * Sets the active symbols and their counts for the player.
     *
     * @param activeSymbols the new map of active symbols and their counts.
     */
    public void setActiveSymbols(Map<Symbol, Integer> activeSymbols) {
        this.activeSymbols = activeSymbols;
    }

    /**
     * @return the color of the player.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the JavaFX color of the player.
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
     * @return the hand of the player.
     */
    public List<Integer> getHand() {
        return hand;
    }

    /**
     * @return the ID of the personal objective card.
     */
    public int getPersonalObjectiveId() {
        return personalObjectiveId;
    }

    /**
     * Sets the hand of the player.
     *
     * @param hand the new hand of the player.
     */
    public void setHand(List<Integer> hand) {
        this.hand = hand;
    }

    /**
     * @return the hidden hand of the player.
     */
    public List<Pair<Resource, Boolean>> getHiddenHand() {
        return hiddenHand;
    }

    /**
     * Sets the hidden hand of the player.
     *
     * @param hiddenHand the new hidden hand of the player.
     */
    public void setHiddenHand(List<Pair<Resource, Boolean>> hiddenHand) {
        this.hiddenHand = hiddenHand;
    }

    /**
     * Sets the personal objective of the player.
     *
     * @param personalObjectiveId the new ID of the personal objective card.
     */
    public void setPersonalObjectiveId(int personalObjectiveId) {
        this.personalObjectiveId = personalObjectiveId;
    }

    /**
     * @return the starter card, or null if no starter tile is found.
     */
    public VirtualCard getStarterCard() {
        VirtualTile starterTile = this.board.getStarterTile();
        if (starterTile != null)
            return starterTile.getCard();
        return null;
    }

    /**
     * Adds a new message to the player's chat and notifies observers.
     *
     * @param text the text of the message.
     * @param sender the sender of the message.
     * @param recipient the recipient of the message.
     * @param time the time the message was sent.
     */
    public void setMessage(String text, String sender, String recipient, LocalTime time) {
        messages.add(new VirtualChatMessage(text, sender, recipient, time));
        this.notifyObservers();
    }

    /**
     * @return the list of chat messages for the player.
     */
    public List<VirtualChatMessage> getMessages() {
        return messages;
    }

    /**
     * @return the global chat messages for the player.
     */
    public List<String> getGlobalChat() {
        return messages.stream()
                .filter(m -> !m.isPrivate())
                .map(m -> m.getTimeAsString() + " " + m.getSender() + ": " + m.getText())
                .toList();
    }

    /**
     * Retrieves the private chat messages between the player and a recipient.
     *
     * @param recipient the recipient of the private messages.
     * @return the list of private chat messages.
     */
    public List<String> getPrivateChat(VirtualPlayer recipient) {
        return messages.stream()
                .filter(m -> m.getRecipient().equals(recipient.username) || (m.getSender().equals(recipient.username) && m.getRecipient().equals(username)))
                .map(m -> m.getSender().equals(username) ? m.getTimeAsString() + " " + m.getText() : m.getTimeAsString() + " " + m.getSender() + ": " + m.getText())
                .toList();
    }
}
