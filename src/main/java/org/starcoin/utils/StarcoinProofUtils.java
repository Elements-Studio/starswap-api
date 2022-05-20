package org.starcoin.utils;

import com.novi.serde.DeserializationError;
import com.novi.serde.SerializationError;
import org.starcoin.bean.StateProof;
import org.starcoin.smt.Bytes;
import org.starcoin.smt.Pair;
import org.starcoin.smt.SparseMerkleProof;
import org.starcoin.smt.StarcoinTreeHasher;
import org.starcoin.types.AccountAddress;
import org.starcoin.types.AccountState;
import org.starcoin.types.StructTag;

public class StarcoinProofUtils {
    public static final int ACCOUNT_STORAGE_INDEX_RESOURCE = 1;

    private StarcoinProofUtils() {
    }

    /**
     * Verify account resource state proof.
     *
     * @param stateProof        State proof.
     * @param stateRoot         Global state root.
     * @param accountAddress    Account address.
     * @param resourceStructTag Resource struct tag.
     * @param state             Resource state.
     * @return True if verified Ok, or else false.
     */
    public static boolean verifyResourceStateProof(StateProof stateProof, byte[] stateRoot,
                                                   AccountAddress accountAddress, StructTag resourceStructTag, byte[] state)
            throws SerializationError, DeserializationError {

        AccountState accountState = AccountState.bcsDeserialize(stateProof.getAccountState());
        if (accountState.getStorageRoots().length < ACCOUNT_STORAGE_INDEX_RESOURCE + 1) {
            throw new IndexOutOfBoundsException("ACCOUNT_STORAGE_INDEX_RESOURCE");
        }
        StarcoinTreeHasher th = new StarcoinTreeHasher();
        //
        // First, verify state for storage root.
        //
        byte[] storageRoot = accountState.getStorageRoots()[ACCOUNT_STORAGE_INDEX_RESOURCE];
        boolean ok = SparseMerkleProof.verifyProof(
                Bytes.toBytesArray(stateProof.getProof().getSiblings()),
                new Pair<>(new Bytes(stateProof.getProof().getLeaf().getPath()),
                        new Bytes(stateProof.getProof().getLeaf().getValueHash())),
                new Bytes(storageRoot),
                new Bytes(resourceStructTag.bcsSerialize()), // resource struct tag BCS serialized as key
                state == null ? null : new Bytes(state),
                th);
        if (!ok) {
            return false;
        }
        //
        // Then, verify account state for global state root.
        //
        ok = SparseMerkleProof.verifyProof(
                Bytes.toBytesArray(stateProof.getAccountProof().getSiblings()),
                new Pair<>(new Bytes(stateProof.getAccountProof().getLeaf().getPath()),
                        new Bytes(stateProof.getAccountProof().getLeaf().getValueHash())),
                new Bytes(stateRoot),
                new Bytes(accountAddress.toBytes()), // account address as key
                new Bytes(stateProof.getAccountState()),
                th);
        return ok;
    }
}
