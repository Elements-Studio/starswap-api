package dev.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Signature {
    @JsonProperty("public_key")//:"0xa76e9dd1a2d9101de47e69e52e0232060b95cd7d80265d61c3fa25e406389b75",
    private String publicKey;
    @JsonProperty("signature")//:"0xb7c5ba28cbd541c0f93be9d68d9576f342cc629697bddb1b1dafd50ba4b9a4c30eaf59773c5691408d192d27f5804055d6a1c18165f769489ef7d85b5a241807",
    private String signature;
    @JsonProperty("type")//:"ed25519_signature"
    private String type;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Signature{" +
                "publicKey='" + publicKey + '\'' +
                ", signature='" + signature + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
