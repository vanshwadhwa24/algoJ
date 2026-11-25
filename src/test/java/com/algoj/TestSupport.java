package com.algoj;

import com.algoj.algorithm.Algorithms;
import com.algoj.algorithm.AlgorithmInput;
import com.algoj.dto.ExecutionStep;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/** Shared helpers: run an algorithm, read a step state as a plain map, collect oracle mismatches. */
final class TestSupport {
    static final ObjectMapper JSON = new ObjectMapper();

    private TestSupport() {}

    static List<ExecutionStep> run(String id, AlgorithmInput in) {
        return Algorithms.get(id).run(in);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> state(ExecutionStep step) {
        return JSON.convertValue(step.state(), Map.class);
    }

    static Map<String, Object> last(List<ExecutionStep> steps) {
        return state(steps.get(steps.size() - 1));
    }

    static double num(Object o) {
        if (o == null) return Double.POSITIVE_INFINITY;
        if (o instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(o.toString());
        } catch (NumberFormatException e) {
            return Double.POSITIVE_INFINITY;
        }
    }

    /** Collects mismatches so one test run reports every failing case, not just the first. */
    static final class Failures {
        private final Map<String, String> firstExample = new TreeMap<>();
        private final Map<String, Integer> count = new TreeMap<>();

        void add(String key, String detail) {
            count.merge(key, 1, Integer::sum);
            firstExample.putIfAbsent(key, detail);
        }

        boolean isEmpty() {
            return count.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            count.forEach((k, c) -> b.append(k).append(" x").append(c).append(" e.g. ").append(firstExample.get(k)).append('\n'));
            return b.toString();
        }
    }
}
