package dev.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LedgerInfo {
    /*
    {
      "chain_id": 27,
      "epoch": "72",
      "ledger_version": "33439307",
      "oldest_ledger_version": "0",
      "ledger_timestamp": "1663215097891147",
      "node_role": "full_node",
      "oldest_block_height": "0",
      "block_height": "5824914"
    }
     */

    @JsonProperty("chain_id")
    private Integer chainId;

    @JsonProperty("epoch")
    private String epoch;

    @JsonProperty("ledger_version")
    private String ledgerVersion;

    @JsonProperty("oldest_ledger_version")
    private String oldestLedgerVersion;

    @JsonProperty("ledger_timestamp")
    private String ledgerTimestamp;

    @JsonProperty("node_role")
    private String nodeRole;

    @JsonProperty("oldest_block_height")
    private String oldestBlockHeight;

    @JsonProperty("block_height")
    private String blockHeight;

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }

    public String getEpoch() {
        return epoch;
    }

    public void setEpoch(String epoch) {
        this.epoch = epoch;
    }

    public String getLedgerVersion() {
        return ledgerVersion;
    }

    public void setLedgerVersion(String ledgerVersion) {
        this.ledgerVersion = ledgerVersion;
    }

    public String getOldestLedgerVersion() {
        return oldestLedgerVersion;
    }

    public void setOldestLedgerVersion(String oldestLedgerVersion) {
        this.oldestLedgerVersion = oldestLedgerVersion;
    }

    public String getLedgerTimestamp() {
        return ledgerTimestamp;
    }

    public void setLedgerTimestamp(String ledgerTimestamp) {
        this.ledgerTimestamp = ledgerTimestamp;
    }

    public String getNodeRole() {
        return nodeRole;
    }

    public void setNodeRole(String nodeRole) {
        this.nodeRole = nodeRole;
    }

    public String getOldestBlockHeight() {
        return oldestBlockHeight;
    }

    public void setOldestBlockHeight(String oldestBlockHeight) {
        this.oldestBlockHeight = oldestBlockHeight;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override
    public String toString() {
        return "LedgerInfo{" +
                "chainId=" + chainId +
                ", epoch='" + epoch + '\'' +
                ", ledgerVersion='" + ledgerVersion + '\'' +
                ", oldestLedgerVersion='" + oldestLedgerVersion + '\'' +
                ", ledgerTimestamp='" + ledgerTimestamp + '\'' +
                ", nodeRole='" + nodeRole + '\'' +
                ", oldestBlockHeight='" + oldestBlockHeight + '\'' +
                ", blockHeight='" + blockHeight + '\'' +
                '}';
    }
}
