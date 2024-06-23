package it.polimi.ingsw.am49.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an observable object, or "data" in the model-view paradigm.
 * It can be subclassed to represent an object that the application wants to have observed.
 */
public class Observable {
    private final List<Observer> observers;

    /**
     * Constructs an Observable with zero Observers.
     */
    public Observable() {
        this.observers = new ArrayList<>();
    }

    /**
     * Adds an observer to the set of observers for this object, provided that it is not the same
     * as some observer already in the set. The order in which notifications will be delivered to
     * multiple observers is not specified. See the class comment.
     *
     * @param o the observer to be added
     * @throws NullPointerException if the parameter o is null.
     */
    public synchronized void addObserver(Observer o) {
        if (o == null) {
            throw new NullPointerException("Observer cannot be null");
        }
        if (!this.observers.contains(o)) {
            this.observers.add(o);
        }
    }

    /**
     * Deletes an observer from the set of observers of this object.
     *
     * @param o the observer to be deleted
     */
    public synchronized void deleteObserver(Observer o) {
        this.observers.remove(o);
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void clearObservers() {
        this.observers.clear();
    }

    /**
     * Notifies all observers about the event.
     * To prevent ConcurrentModificationException, a copy of the observer list is used
     * for notification purposes.
     */
    public void notifyObservers() {
        List<Observer> observersCopy;
        synchronized (this) {
            observersCopy = new ArrayList<>(this.observers);
        }

        for (Observer observer : observersCopy) {
            synchronized (this) {
                if (this.observers.contains(observer)) {
                    observer.update();
                }
            }
        }
    }
}
