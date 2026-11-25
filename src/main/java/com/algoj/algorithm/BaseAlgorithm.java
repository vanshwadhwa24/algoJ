package com.algoj.algorithm;

/** Holds the id shared by every algorithm. */
public abstract class BaseAlgorithm implements Algorithm {
    private final String id;

    protected BaseAlgorithm(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }
}
