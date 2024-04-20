package it.polimi.ingsw.am49.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO: test the hell out of this class
public class BiMap<K, V> {

    private final Map<K, V> keyToValue = new HashMap<>();
    private final Map<V, K> valueToKey = new HashMap<>();

    public void put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        if (keyToValue.containsKey(key) || valueToKey.containsKey(value))
            throw new IllegalArgumentException("Duplicate key or value");

        keyToValue.put(key, value);

        if (value != null)
            valueToKey.put(value, key);
    }

    public V getValue(K key) {
        return keyToValue.get(key);
    }

    public K getKey(V value) {
        return valueToKey.get(value);
    }

    public boolean containsValue(V value) {
        return this.keyToValue.containsValue(value);
    }

    public boolean containsKey(K key) {
        return this.keyToValue.containsKey(key);
    }

    public Set<K> keySet() {
        return this.keyToValue.keySet();
    }

    public Collection<V> values() {
        return this.keyToValue.values();
    }
}
