package it.polimi.ingsw.am49.model.cards;

import com.google.gson.Gson;
import it.polimi.ingsw.am49.model.decks.DeckLoader;

import java.io.Serializable;

/**
 * Represents a base card with a unique identifier. This abstract class provides the
 * common structure for all cards in the system. Subclasses should define specific
 * behaviors and characteristics relevant to different types of cards.
 *
 * @see it.polimi.ingsw.am49.model.cards.placeables.PlaceableCard
 * @see it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard
 */
public abstract class Card implements Serializable {

    /**
     * The unique identifier for this card. It is protected so that subclasses can access it
     * directly, but it is final to ensure that the identifier cannot be changed once set.
     */
    protected final int id;

    /**
     * Constructs a new Card with the specified unique identifier.
     *
     * @param id the unique identifier for this card
     */
    public Card(int id) {
        this.id = id;
    }

    /**
     *
     * @return the unique id of the card
     */
    public int getId() {
        return this.id;
    }

    /**
     * Creates a new object that is a copy of the card.
     *
     * @return the newly created copy
     */
    public abstract Card clone();

    /**
     *
     * @return a string rapresenting the card
     */
    public String toString() {
        Gson gson = DeckLoader.getInstance().getGson();
        return gson.toJson(this);
    }
}
