package com.missionhub.util;

import java.io.Serializable;
import java.util.Map;

public class SimpleEntry<K, V>
        implements Map.Entry<K, V>, Serializable {
    private static final long serialVersionUID = -8499721149061103585L;
    private final K key;
    private V value;

    public SimpleEntry(K theKey, V theValue) {
        key = theKey;
        value = theValue;
    }

    /**
     * Constructs an instance with the key and value of {@code copyFrom}.
     */
    public SimpleEntry(Map.Entry<? extends K, ? extends V> copyFrom) {
        key = copyFrom.getKey();
        value = copyFrom.getValue();
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V object) {
        V result = value;
        value = object;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
            return (key == null ? entry.getKey() == null : key.equals(entry
                    .getKey()))
                    && (value == null ? entry.getValue() == null : value
                    .equals(entry.getValue()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (key == null ? 0 : key.hashCode())
                ^ (value == null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}