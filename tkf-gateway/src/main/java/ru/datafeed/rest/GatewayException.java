package ru.datafeed.rest;

public class GatewayException extends RuntimeException {
    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayException(String message) {
        super(message);
    }
}
