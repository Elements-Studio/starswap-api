package org.starcoin.starswap.types;


import org.starcoin.types.AccountAddress;
import org.starcoin.types.TokenCode;

public final class BoostEvent {
    public final TokenCode x_token_code;
    public final TokenCode y_token_code;
    public final AccountAddress signer;
    public final java.math.@com.novi.serde.Unsigned @com.novi.serde.Int128 BigInteger amount;

    public BoostEvent(TokenCode x_token_code, TokenCode y_token_code, AccountAddress signer, java.math.@com.novi.serde.Unsigned @com.novi.serde.Int128 BigInteger amount) {
        java.util.Objects.requireNonNull(x_token_code, "x_token_code must not be null");
        java.util.Objects.requireNonNull(y_token_code, "y_token_code must not be null");
        java.util.Objects.requireNonNull(signer, "signer must not be null");
        java.util.Objects.requireNonNull(amount, "amount must not be null");
        this.x_token_code = x_token_code;
        this.y_token_code = y_token_code;
        this.signer = signer;
        this.amount = amount;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        x_token_code.serialize(serializer);
        y_token_code.serialize(serializer);
        signer.serialize(serializer);
        serializer.serialize_u128(amount);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static BoostEvent deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.x_token_code = TokenCode.deserialize(deserializer);
        builder.y_token_code = TokenCode.deserialize(deserializer);
        builder.signer = AccountAddress.deserialize(deserializer);
        builder.amount = deserializer.deserialize_u128();
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static BoostEvent bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        BoostEvent value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BoostEvent other = (BoostEvent) obj;
        if (!java.util.Objects.equals(this.x_token_code, other.x_token_code)) { return false; }
        if (!java.util.Objects.equals(this.y_token_code, other.y_token_code)) { return false; }
        if (!java.util.Objects.equals(this.signer, other.signer)) { return false; }
        if (!java.util.Objects.equals(this.amount, other.amount)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.x_token_code != null ? this.x_token_code.hashCode() : 0);
        value = 31 * value + (this.y_token_code != null ? this.y_token_code.hashCode() : 0);
        value = 31 * value + (this.signer != null ? this.signer.hashCode() : 0);
        value = 31 * value + (this.amount != null ? this.amount.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public TokenCode x_token_code;
        public TokenCode y_token_code;
        public AccountAddress signer;
        public java.math.@com.novi.serde.Unsigned @com.novi.serde.Int128 BigInteger amount;

        public BoostEvent build() {
            return new BoostEvent(
                x_token_code,
                y_token_code,
                signer,
                amount
            );
        }
    }
}
