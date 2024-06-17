package it.polimi.ingsw.am49.util;

import java.util.*;

/**
 * The BiMap class represents a bidirectional map that maintains a one-to-one relationship between keys and values.
 * It allows for efficient lookups from keys to values and values to keys.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class BiMap<K, V> {

    private final Map<K, V> keyToValue = new HashMap<>();
    private final Map<V, K> valueToKey = new HashMap<>();

    /**
     * Associates the specified key with the specified value in this map.
     * If the map previously contained a mapping for the key or value, an exception is thrown.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @throws IllegalArgumentException if the key is null or if the map already contains the specified key or value
     */
    public void put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        if (keyToValue.containsKey(key) || valueToKey.containsKey(value))
            throw new IllegalArgumentException("Duplicate key or value");

        keyToValue.put(key, value);

        if (value != null)
            valueToKey.put(value, key);
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous key-value pair associated with the key, or null if there was no mapping for the key
     */
    public Map.Entry<K, V> removeKey(K key) {
        K k = this.valueToKey.remove(this.keyToValue.get(key));
        V v = this.keyToValue.remove(key);
        return new AbstractMap.SimpleEntry<>(k, v);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    public V getValue(K key) {
        return keyToValue.get(key);
    }

    /**
     * Returns the key to which the specified value is mapped, or null if this map contains no mapping for the value.
     *
     * @param value the value whose associated key is to be returned
     * @return the key to which the specified value is mapped, or null if this map contains no mapping for the value
     */
    public K getKey(V value) {
        return valueToKey.get(value);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param value the value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    public boolean containsValue(V value) {
        return this.keyToValue.containsValue(value);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    public boolean containsKey(K key) {
        return this.keyToValue.containsKey(key);
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet() {
        return this.keyToValue.keySet();
    }

    /**
     * Returns a Collection view of the values contained in this map.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
        return this.keyToValue.values();
    }
}
