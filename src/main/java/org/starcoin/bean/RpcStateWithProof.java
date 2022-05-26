package org.starcoin.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * State with proof returned by JSON RPC.
 */
public class RpcStateWithProof {

    /**
     * Account state's proof for global state root.
     */
    @JsonProperty("account_proof")
    private SparseMerkleProof accountProof;

    /**
     * Account state including storage roots.
     */
    @JsonProperty("account_state")
    private String accountState;

    /**
     * State's proof for account storage root.
     */
    @JsonProperty("account_state_proof")
    private SparseMerkleProof stateProof;

    /**
     * State(value) at the specified access path.
     */
    @JsonProperty("state")
    private String state;

    public SparseMerkleProof getAccountProof() {
        return accountProof;
    }

    public void setAccountProof(SparseMerkleProof accountProof) {
        this.accountProof = accountProof;
    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public SparseMerkleProof getStateProof() {
        return stateProof;
    }

    public void setStateProof(SparseMerkleProof stateProof) {
        this.stateProof = stateProof;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "StateWithProof{" +
                "accountProof=" + accountProof +
                ", accountState='" + accountState + '\'' +
                ", stateProof=" + stateProof +
                ", state='" + state + '\'' +
                '}';
    }

    public static class SparseMerkleProof {
        @JsonProperty("siblings")
        private String[] siblings;
        @JsonProperty("leaf")
        private String[] leaf;

        public String[] getLeaf() {
            return leaf;
        }

        public void setLeaf(String[] leaf) {
            this.leaf = leaf;
        }

        public String[] getSiblings() {
            return siblings;
        }

        public void setSiblings(String[] siblings) {
            this.siblings = siblings;
        }

        @Override
        public String toString() {
            return "SparseMerkleProof{" +
                    "leaf=" + Arrays.toString(leaf) +
                    ", siblings=" + Arrays.toString(siblings) +
                    '}';
        }
    }
}
