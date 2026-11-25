package com.algoj.persistence;

/** Unchecked wrapper so UI code does not have to handle SQLException everywhere. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
