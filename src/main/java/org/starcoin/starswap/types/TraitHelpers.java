package org.starcoin.starswap.types;

final class TraitHelpers {
    static void serialize_array16_u8_array(java.util.@com.novi.serde.ArrayLen(length=16) List<@com.novi.serde.Unsigned Byte> value, com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        if (value.size() != 16) {
            throw new java.lang.IllegalArgumentException("Invalid length for fixed-size array: " + value.size() + " instead of "+ 16);
        }
        for (@com.novi.serde.Unsigned Byte item : value) {
            serializer.serialize_u8(item);
        }
    }

    static java.util.@com.novi.serde.ArrayLen(length=16) List<@com.novi.serde.Unsigned Byte> deserialize_array16_u8_array(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        java.util.List<@com.novi.serde.Unsigned Byte> obj = new java.util.ArrayList<@com.novi.serde.Unsigned Byte>(16);
        for (long i = 0; i < 16; i++) {
            obj.add(deserializer.deserialize_u8());
        }
        return obj;
    }

}

