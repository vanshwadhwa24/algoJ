package com.algoj.structure;

/** A user action on a data structure. Which fields matter depends on the structure and the operation type. */
public record Operation(String type, Integer value, Integer index, String key, String text) {

    public static Operation of(String type) {
        return new Operation(type, null, null, null, null);
    }

    public static Operation withValue(String type, Integer value) {
        return new Operation(type, value, null, null, null);
    }
}
