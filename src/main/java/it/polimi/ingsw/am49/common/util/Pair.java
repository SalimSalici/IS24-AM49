package it.polimi.ingsw.am49.common.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * A generic container to hold a pair of objects.
 * This class supports types T and E, allowing for different types to be paired together.
 * It implements Serializable, enabling it to be used in serialization operations where needed.
 *
 * @param <T> the type of the first element in the pair
 * @param <E> the type of the second element in the pair
 */
public class Pair<T, E> implements Serializable {
    /**
     * The first element of the pair.
     */
    public T first;

    /**
     * The second element of the pair.
     */
    public E second;

    /**
     * Constructs an empty Pair with both elements initialized to null.
     */
    public Pair() {}

    /**
     * Constructs a Pair with specified values for its elements.
     *
     * @param first the first element of the pair
     * @param second the second element of the pair
     */
    public Pair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @param other the other pair
     * @return true if the two pairs are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    /**
     * @return hash code of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
