package org.starcoin.smt;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapSmtValueStoreWrapper implements SmtValueStore {
    private final Map<Bytes, Bytes> map;

    public MapSmtValueStoreWrapper(Map<Bytes, Bytes> map) {
        if (map == null)
            throw new IllegalArgumentException();
        this.map = map;
    }

    @Override
    public boolean isImmutable() {
        return false;
    }

    @Override
    public Bytes getForValueHash(Bytes key, Bytes valueHash) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putForValueHash(Bytes key, Bytes valueHash, Bytes value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Bytes get(Object key) {
        return this.map.get(key);
    }

    @Override
    public Bytes put(Bytes key, Bytes value) {
        return this.map.put(key, value);
    }

    @Override
    public Bytes remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends Bytes, ? extends Bytes> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<Bytes> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<Bytes> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<Bytes, Bytes>> entrySet() {
        return this.map.entrySet();
    }
}
