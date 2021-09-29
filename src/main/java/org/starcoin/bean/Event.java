package org.starcoin.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Event {
    private static final int ADDRESS_BYTE_LENGTH = 16;
    @JsonProperty("block_hash")
    @JSONField(name = "block_hash")
    String blockHash;
    @JSONField(name = "block_number")
    @JsonProperty("block_number")
    String blockNumber;
    @JSONField(name = "transaction_hash")
    @JsonProperty("transaction_hash")
    String transactionHash;
    @JSONField(name = "transaction_index")
    @JsonProperty("transaction_index")
    int transactionIndex;
    String data;
    @JSONField(name = "type_tag")
    @JsonProperty("type_tag")
    String typeTag;  //TypeTag typeTag;
    @JSONField(name = "event_key")
    @JsonProperty("event_key")
    String eventKey; // "0x0300000000000000ccf1adedf0ba6f9bdb9a6905173a5d72",
    @JSONField(name = "event_seq_number")
    @JsonProperty("event_seq_number")
    String eventSeqNumber;

//    @JSONField(name = "decode_event_data")
//    @JsonProperty("decode_event_data")
//    Map<String, Object> decodeEventData;

    public static String getFromAddressFromEventKey(String eventKey) {
        return "0x" + eventKey.substring(eventKey.length() - ADDRESS_BYTE_LENGTH * 2);
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTypeTag() {
        return typeTag;
    }

    public void setTypeTag(String typeTag) {
        this.typeTag = typeTag;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventSeqNumber() {
        return eventSeqNumber;
    }

    public void setEventSeqNumber(String eventSeqNumber) {
        this.eventSeqNumber = eventSeqNumber;
    }

//    public Map<String, Object> getDecodeEventData() {
//        return decodeEventData;
//    }
//
//    public void setDecodeEventData(Map<String, Object> decodeEventData) {
//        this.decodeEventData = decodeEventData;
//    }

    @Override
    public String toString() {
        return "Event{" +
                "blockHash='" + blockHash + '\'' +
                ", blockNumber='" + blockNumber + '\'' +
                ", transactionHash='" + transactionHash + '\'' +
                ", transactionIndex=" + transactionIndex +
                ", data='" + data + '\'' +
                ", typeTag='" + typeTag + '\'' +
                ", eventKey='" + eventKey + '\'' +
                ", eventSeqNumber='" + eventSeqNumber + '\'' +
                //", decodeEventData=" + decodeEventData +
                '}';
    }
}
