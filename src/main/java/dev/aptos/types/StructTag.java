package dev.aptos.types;


public final class StructTag {
    public final dev.aptos.types.AccountAddress address;
    public final dev.aptos.types.Identifier module;
    public final dev.aptos.types.Identifier name;
    public final java.util.List<dev.aptos.types.TypeTag> type_params;

    public StructTag(dev.aptos.types.AccountAddress address, dev.aptos.types.Identifier module, dev.aptos.types.Identifier name, java.util.List<dev.aptos.types.TypeTag> type_params) {
        java.util.Objects.requireNonNull(address, "address must not be null");
        java.util.Objects.requireNonNull(module, "module must not be null");
        java.util.Objects.requireNonNull(name, "name must not be null");
        java.util.Objects.requireNonNull(type_params, "type_params must not be null");
        this.address = address;
        this.module = module;
        this.name = name;
        this.type_params = type_params;
    }

    public static StructTag deserialize(com.novi.serde.Deserializer deserializer) throws com.novi.serde.DeserializationError {
        deserializer.increase_container_depth();
        Builder builder = new Builder();
        builder.address = dev.aptos.types.AccountAddress.deserialize(deserializer);
        builder.module = dev.aptos.types.Identifier.deserialize(deserializer);
        builder.name = dev.aptos.types.Identifier.deserialize(deserializer);
        builder.type_params = dev.aptos.types.TraitHelpers.deserialize_vector_TypeTag(deserializer);
        deserializer.decrease_container_depth();
        return builder.build();
    }

    public static StructTag bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
            throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        StructTag value = deserialize(deserializer);
        if (deserializer.get_buffer_offset() < input.length) {
            throw new com.novi.serde.DeserializationError("Some input bytes were not read");
        }
        return value;
    }

    public void serialize(com.novi.serde.Serializer serializer) throws com.novi.serde.SerializationError {
        serializer.increase_container_depth();
        address.serialize(serializer);
        module.serialize(serializer);
        name.serialize(serializer);
        dev.aptos.types.TraitHelpers.serialize_vector_TypeTag(type_params, serializer);
        serializer.decrease_container_depth();
    }

    public byte[] bcsSerialize() throws com.novi.serde.SerializationError {
        com.novi.serde.Serializer serializer = new com.novi.bcs.BcsSerializer();
        serialize(serializer);
        return serializer.get_bytes();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StructTag other = (StructTag) obj;
        if (!java.util.Objects.equals(this.address, other.address)) {
            return false;
        }
        if (!java.util.Objects.equals(this.module, other.module)) {
            return false;
        }
        if (!java.util.Objects.equals(this.name, other.name)) {
            return false;
        }
        return java.util.Objects.equals(this.type_params, other.type_params);
    }

    public int hashCode() {
        int value = 7;
        value = 31 * value + (this.address != null ? this.address.hashCode() : 0);
        value = 31 * value + (this.module != null ? this.module.hashCode() : 0);
        value = 31 * value + (this.name != null ? this.name.hashCode() : 0);
        value = 31 * value + (this.type_params != null ? this.type_params.hashCode() : 0);
        return value;
    }

    public static final class Builder {
        public AccountAddress address;
        public dev.aptos.types.Identifier module;
        public Identifier name;
        public java.util.List<TypeTag> type_params;

        public StructTag build() {
            return new StructTag(
                    address,
                    module,
                    name,
                    type_params
            );
        }
    }
}
