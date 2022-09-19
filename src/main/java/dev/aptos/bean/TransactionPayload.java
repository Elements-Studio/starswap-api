package dev.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TransactionPayload {
    @JsonProperty("type")//:"entry_function_payload",
    private String type;

    @JsonProperty("function")//:"0x1::aptos_coin::transfer",
    private String function;

    @JsonProperty("type_arguments")//:[
    private List<String> typeArguments;

    @JsonProperty("arguments")//:[
    private List<Object> arguments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<String> getTypeArguments() {
        return typeArguments;
    }

    public void setTypeArguments(List<String> typeArguments) {
        this.typeArguments = typeArguments;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "TransactionPayload{" +
                "type='" + type + '\'' +
                ", function='" + function + '\'' +
                ", typeArguments=" + typeArguments +
                ", arguments=" + arguments +
                '}';
    }
}
