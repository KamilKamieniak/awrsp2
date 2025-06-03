package com.example.temperaturemanagement.exception;

public class TemperatureNotFoundException extends RuntimeException {
    public TemperatureNotFoundException(String message) {
        super(message);
    }

    public TemperatureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}