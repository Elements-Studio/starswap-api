package dev.aptos.utils;

import dev.aptos.bean.AptosError;

public class NodeApiException extends RuntimeException {
    private Integer httpStatusCode;
    private AptosError aptosError;

    public NodeApiException() {
    }

    public NodeApiException(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public NodeApiException(Integer httpStatusCode, AptosError aptosError) {
        this.httpStatusCode = httpStatusCode;
        this.aptosError = aptosError;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public AptosError getAptosError() {
        return aptosError;
    }

    public void setAptosError(AptosError aptosError) {
        this.aptosError = aptosError;
    }

    @Override
    public String toString() {
        return "NodeApiException{" +
                "httpStatusCode=" + httpStatusCode +
                ", aptosError=" + aptosError +
                '}';
    }
}
