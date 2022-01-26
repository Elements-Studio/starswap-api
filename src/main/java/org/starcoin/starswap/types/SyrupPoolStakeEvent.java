package org.starcoin.starswap.types;


import org.starcoin.types.AccountAddress;
import org.starcoin.types.TokenCode;

public final class SyrupPoolStakeEvent {
    public final TokenCode token_code;
    public final AccountAddress signer;
    public final java.math.@com.novi.serde.Unsigned @com.novi.serde.Int128 BigInteger amount;
    public final AccountAddress admin;

    public SyrupPoolStakeEvent(TokenCode token_code, AccountAddress signer, java.math.@com.novi.serde.Unsigned @com.novi.serde.Int128 BigInteger amount, AccountAddress admin) {
        java.util.Objects.requireNonNull(token_code, "token_code must not be null");
        java.util.Objects.requireNonNull(signer, "signer must not be null");
        java.util.Objects.requireNonNull(amount, "amount must not be null");
        java.util.Objects.requireNonNull(admin, "admin must not be null");
        this.token_code = token_code;
        this.signer = signer;
        this.amount = amount;
        this.admin = admin;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        token_code.serialize(serializer);
        signer.serialize(serializer);
        serializer.serialize_u128(amount);
        admin.serialize(serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public static SyrupPoolStakeEvent deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.token_code = TokenCode.deserialize(deserializer);
        builder.signer = AccountAddress.deserialize(deserializer);
        builder.amount = deserializer.deserialize_u128();
        builder.admin = AccountAddress.deserialize(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static SyrupPoolStakeEvent bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
             throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        SyrupPoolStakeEvent value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
             throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SyrupPoolStakeEvent other = (SyrupPoolStakeEvent) obj;
        if (!java.util.Objects.equals(this.token_code, other.token_code)) { return false; }
        if (!java.util.Objects.equals(this.signer, other.signer)) { return false; }
        if (!java.util.Objects.equals(this.amount, other.amount)) { return false; }
        if (!java.util.Objects.equals(this.admin, other.admin)) { return false; }
        return true;
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.token_code != null ? this.token_code.hashCode() : 0);
        value = 31 * value + (this.signer != null ? this.signer.hashCode() : 0);
        value = 31 * value + (this.amount != null ? this.amount.hashCode() : 0);
        value = 31 * value + (this.admin != null ? this.admin.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public TokenCode token_code;
        public AccountAddress signer;
        public java.math.@com.novi.serde.Unsigned @com.novi.serde.Int128 BigInteger amount;
        public AccountAddress admin;

        public SyrupPoolStakeEvent build() {
            return new SyrupPoolStakeEvent(
                token_code,
                signer,
                amount,
                admin
            );
        }
    }
}
