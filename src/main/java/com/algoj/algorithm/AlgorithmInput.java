package com.algoj.algorithm;

import com.algoj.model.GraphModel.GraphData;
import java.util.List;

/** Everything an algorithm may need. Unused or null fields fall back to each algorithm's own defaults. */
public record AlgorithmInput(List<Integer> array, Integer target, Integer n, String mode, Integer disks,
                             String text1, String text2, GraphData graph) {

    public static final List<Integer> DEFAULT_ARRAY = List.of(35, 12, 48, 22, 60, 5);

    public static AlgorithmInput empty() {
        return new AlgorithmInput(null, null, null, null, null, null, null, null);
    }

    public static AlgorithmInput ofArray(List<Integer> array, Integer target) {
        return new AlgorithmInput(array, target, null, null, null, null, null, null);
    }

    public List<Integer> arrayOrDefault() {
        return array == null || array.isEmpty() ? DEFAULT_ARRAY : array;
    }
}
