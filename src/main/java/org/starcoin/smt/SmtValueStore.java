package org.starcoin.smt;

import java.util.Map;

public interface SmtValueStore extends Map<Bytes, Bytes> {

    static SmtValueStore asSmtValueStore(Map<Bytes, Bytes> mapStore) {
        if (mapStore instanceof SmtValueStore) {
            return (SmtValueStore) mapStore;
        } else {
            return new MapSmtValueStoreWrapper(mapStore);
        }
    }

    boolean isImmutable();

    Bytes getForValueHash(Bytes key, Bytes valueHash);

    void putForValueHash(Bytes key, Bytes valueHash, Bytes value);
}
