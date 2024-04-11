package it.polimi.ingsw.am49.model.decks;

import it.polimi.ingsw.am49.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This Class is used to save a copy of the deck in {@link it.polimi.ingsw.am49.model.decks.GameDeck} as a List
 * to easily implement the resilience against crashes. The main methods of the Stack structure are defined in order to
 * allow the use of this list as if it were a Stack.
 * @param <T> tells the tipe of card that are in the list that is being handled.
 */
public class CardStack<T extends Card>{
    private final ArrayList<T> stack;

    public CardStack() {
        this.stack = new ArrayList<>();
    }

    public void push(T card) {
        stack.add(card);
    }

    public T pop() {
        if (!stack.isEmpty()) {
            return stack.removeLast();
        }
        return null; // o eccezione
    }

    public T peek() {
        if (!stack.isEmpty()) {
            return stack.getLast();
        }
        return null; // o eccezione
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    public void shuffle(){
        Collections.shuffle(stack);
    }
}
