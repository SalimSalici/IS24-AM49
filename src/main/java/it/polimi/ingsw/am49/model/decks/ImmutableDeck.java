package it.polimi.ingsw.am49.model.decks;

import it.polimi.ingsw.am49.model.cards.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the list rappresentig the immutable deck and handles the copy of cards for the GameLoader {@link it.polimi.ingsw.am49.model.decks.DeckLoader} class.
 * @param <T> tells the type of card that are in the immutable deck that is being handled.
 */
public class ImmutableDeck<T extends Card> {

    private final List<T> cards;

    /**
     * Adds all cards to the immutable deck.
     * @param cards is a list with all the cards in the deck, originating from the respective JSON file and being
     *              processed in the DeckLoader class {@link it.polimi.ingsw.am49.model.decks.DeckLoader}.
     * See the following path for the cards JSON file: {@code src/main/resources/}.
     */
    public ImmutableDeck(List<T> cards) {
        this.cards = new ArrayList<>();
        this.cards.addAll(cards);
    }

    /**
     * Handles the copy of the cards from the immutable deck to a new list that is used in DeckLoader {@link it.polimi.ingsw.am49.model.decks.DeckLoader} to
     * generate the GameDecks arrays needed for the GameDeck class.
     * @see it.polimi.ingsw.am49.model.decks.GameDeck
     * @return list that is a copy of the game deck.
     */
    @SuppressWarnings(value="unchecked")
    public List<T> getCardsCopy() {
        List<T> copy = new ArrayList<>();
        for (T card : this.cards)
            copy.add((T)card.clone());
        return copy;
    }

}
