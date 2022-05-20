package org.starcoin.smt;

import org.bouncycastle.jcajce.provider.digest.SHA3;

public class Sha3Digest256Hasher implements Hasher {
    private final SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();

    @Override
    public int size() {
        return 256 / 8;
    }

    @Override
    public Bytes hash(Bytes value) {
        digestSHA3.update(value.getValue());
        byte[] d = digestSHA3.digest();
        digestSHA3.reset();
        return new Bytes(d);
    }
}
