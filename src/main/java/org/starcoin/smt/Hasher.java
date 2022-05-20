package org.starcoin.smt;

public interface Hasher {
    int size();

    Bytes hash(Bytes value);
}
