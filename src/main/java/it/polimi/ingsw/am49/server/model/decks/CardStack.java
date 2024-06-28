package it.polimi.ingsw.am49.server.model.decks;

import it.polimi.ingsw.am49.server.model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is used to save a copy of the deck in {@link GameDeck} as a List
 * to easily implement the resilience against crashes. The main methods of the stack structure are defined in order to
 * allow the use of this list as if it were a stack.
 * @param <T> the type of card that is in the list that is being handled.
 */
public class CardStack<T extends Card> implements Serializable {
    private final ArrayList<T> stack;

    /**
     * Constructs an empty CardStack.
     */
    public CardStack() {
        this.stack = new ArrayList<>();
    }

    /**
     * Pushes a card onto the stack.
     *
     * @param card the card to be added to the stack
     */
    public void push(T card) {
        stack.add(card);
    }

    /**
     * Pops a card from the top of the stack.
     *
     * @return the card at the top of the stack, or null if the stack is empty
     */
    public T pop() {
        if (!stack.isEmpty()) {
            return stack.removeLast();
        }
        return null;
    }

    /**
     * Peeks at the card at the top of the stack without removing it.
     *
     * @return the card at the top of the stack, or null if the stack is empty
     */
    public T peek() {
        if (!stack.isEmpty()) {
            return stack.getLast();
        }
        return null;
    }

    /**
     * Checks if the stack is empty.
     *
     * @return true if the stack is empty, false otherwise
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Gets the size of the stack.
     *
     * @return the number of cards in the stack
     */
    public int size() {
        return stack.size();
    }

    /**
     * Shuffles the cards in the stack.
     */
    public void shuffle() {
        Collections.shuffle(stack);
    }
}
