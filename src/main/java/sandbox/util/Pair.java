package sandbox.util;

public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Pair)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Pair<K, V> p = (Pair<K, V>) o;

        return key.equals(p.key) && value.equals(p.value);
    }
}
