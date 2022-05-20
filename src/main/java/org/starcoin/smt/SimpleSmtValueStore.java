package org.starcoin.smt;

import java.util.HashMap;

public class SimpleSmtValueStore extends HashMap<Bytes, Bytes> implements SmtValueStore {
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
}
