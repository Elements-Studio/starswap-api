package dev.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event<TData> {

    /*
      {
        "version": "19923694",
        "key": "0x04000000000000002b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14",
        "sequence_number": "0",
        "type": "0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageChangeEvent",
        "data": {
          "from_message": "hello, blockchain",
          "to_message": "hello, blockchain, again"
        }
      }
     */
    @JsonProperty("version")
    private String version;

    @JsonProperty("key")
    private String key;

    @JsonProperty("sequence_number")
    private String sequenceNumber;

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private TData data;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TData getData() {
        return data;
    }

    public void setData(TData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "version='" + version + '\'' +
                ", key='" + key + '\'' +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
