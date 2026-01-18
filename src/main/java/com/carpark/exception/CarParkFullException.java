package com.carpark.exception;

public class CarParkFullException extends RuntimeException {
    public CarParkFullException(String message) {
        super(message);
    }
}
