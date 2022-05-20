package org.starcoin.types;

import com.novi.serde.Bytes;
import org.starcoin.smt.HexUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class AccountState {
    private final byte[][] storageRoots;

    public AccountState(byte[][] storageRoots) {
        this.storageRoots = storageRoots;
    }

    public static AccountState bcsDeserialize(byte[] input) throws com.novi.serde.DeserializationError {
        if (input == null) {
            throw new com.novi.serde.DeserializationError("Cannot deserialize null array");
        }
        com.novi.serde.Deserializer deserializer = new com.novi.bcs.BcsDeserializer(input);
        long len = deserializer.deserialize_len();
        byte[][] roots = new byte[(int) len][];
        for (int i = 0; i < len; i++) {
            Optional<Bytes> bytesOptional = TraitHelpers.deserialize_option_bytes(deserializer);
            roots[i] = bytesOptional.isPresent() ? bytesOptional.get().content() : null;
        }
        return new AccountState(roots);
    }

    public byte[][] getStorageRoots() {
        return storageRoots;
    }

    @Override
    public String toString() {
        List<String> hs = new ArrayList<>();
        if (storageRoots != null) {
            for (byte[] bytes : storageRoots) {
                hs.add(bytes == null ? null : HexUtils.byteArrayToHex(bytes));
            }
        }
        return "AccountState{" +
                "storageRoots=" + Arrays.toString(hs.toArray(new String[0])) +
                '}';
    }
}
