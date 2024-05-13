package it.polimi.ingsw.am49.util;

// Observable.java
import java.util.ArrayList;
import java.util.List;

public class Observable {
    private final List<Observer> observers;

    public Observable() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(Observer o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!this.observers.contains(o)) {
            this.observers.add(o);
        }
    }

    public void deleteObserver(Observer o) {
        this.observers.remove(o);
    }

    public void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update();
        }
    }
}
