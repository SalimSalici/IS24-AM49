package it.polimi.ingsw.am49.model.decks;

import it.polimi.ingsw.am49.model.cards.Card;

/**
 * This class is used to create a stack for every deck, from witch the cards can be drawn.
 * The usage of a stack allows each card to be drawn only onece in a game.
 * @param <T> tells the type of cards that are in the deck that is being handled.
 */
public class GameDeck<T extends Card> {
    private final CardStack<T> deck;

    /**
     * This constructor randomizes the order of the cards in the deck and adds them to the stack.
     * @param cards is a perfect copy of the corresponding immutable deck present in the GameDeck class {@link it.polimi.ingsw.am49.model.decks.GameDeck}.
     */
    public GameDeck(T[] cards) {
        this.deck = new CardStack<>();
        for (T card : cards)
            deck.push(card);
        deck.shuffle();
    }

    /**
     * @return the card drawn from the deck and therefore removed from the stack.
     */
    public T draw() {
        return deck.pop();
    }

    /**
     * @return the number of cards still present in the deck.
     */
    public int size() {
        return this.deck.size();
    }

    public boolean isEmpty(){
        return this.deck.isEmpty();
    }
}
