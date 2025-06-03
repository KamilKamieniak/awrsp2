package com.example.temperaturemanagement.exception;

public class InvalidTemperatureDataException extends RuntimeException {
    public InvalidTemperatureDataException(String message) {
        super(message);
    }

    public InvalidTemperatureDataException(String message, Throwable cause) {
        super(message, cause);
    }
}