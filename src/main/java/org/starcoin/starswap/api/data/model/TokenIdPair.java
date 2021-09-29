package org.starcoin.starswap.api.data.model;

public class TokenIdPair extends Pair<String, String> {

    public TokenIdPair(String x, String y) {
        if (x.compareTo(y) > 0) {
            setItem1(y);
            setItem2(x);
        } else {
            setItem1(x);
            setItem2(y);
        }
    }

    public String tokenXId() {
        return getItem1();
    }

    public String tokenYId() {
        return getItem2();
    }

    public String[] toStringArray() {
        return new String[]{tokenXId(), tokenYId()};
    }

}
