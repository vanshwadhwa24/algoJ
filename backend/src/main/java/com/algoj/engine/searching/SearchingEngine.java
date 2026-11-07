package com.algoj.engine.searching;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.SearchModel.SearchElement;
import com.algoj.model.SearchModel.SearchState;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchingEngine {

    private List<SearchElement> createSearchElements(List<Integer> values) {
        List<SearchElement> list = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            list.add(new SearchElement("search-" + i + "-" + values.get(i), values.get(i), "default"));
        }
        return list;
    }

    private Map<String, Integer> makeCounters(SearchState s) {
        Map<String, Integer> c = new HashMap<>();
        c.put("comparisons", s.getComparisons());
        return c;
    }

    // ==========================================
    // 1. LINEAR SEARCH
    // ==========================================
    public List<ExecutionStep> linearSearch(List<Integer> initialValues, int target) {
        List<ExecutionStep> steps = new ArrayList<>();
        SearchState state = new SearchState();
        state.setArray(createSearchElements(initialValues));
        state.setTarget(target);
        state.setComparisons(0);
        state.setFoundIndex(null);
        state.setSorted(false);

        steps.add(new ExecutionStep(
                steps.size(), "init",
                String.format("Linear search initialized. Scanning sequential elements for target %d.", target),
                1, state.copy(), makeCounters(state), null
        ));

        for (int i = 0; i < state.getArray().size(); i++) {
            state.setCurrentIndex(i);
            state.getArray().get(i).setState("active");
            state.setComparisons(state.getComparisons() + 1);

            int val = state.getArray().get(i).getValue();

            steps.add(new ExecutionStep(
                    steps.size(), "compare",
                    String.format("Comparing index %d (%d) with target %d.", i, val, target),
                    3, state.copy(), makeCounters(state), null
            ));

            if (val == target) {
                state.setFoundIndex(i);
                state.getArray().get(i).setState("found");

                steps.add(new ExecutionStep(
                        steps.size(), "found",
                        String.format("TARGET FOUND! Target %d located at index %d after %d comparisons.",
                                target, i, state.getComparisons()),
                        4, state.copy(), makeCounters(state), null
                ));
                return steps;
            } else {
                state.getArray().get(i).setState("eliminated");
            }
        }

        steps.add(new ExecutionStep(
                steps.size(), "not-found",
                String.format("TARGET NOT FOUND! Reached end of array. Total comparisons: %d.",
                        state.getComparisons()),
                6, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    // ==========================================
    // 2. BINARY SEARCH
    // ==========================================
    public List<ExecutionStep> binarySearch(List<Integer> initialValues, int target) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<Integer> sortedValues = new ArrayList<>(initialValues);
        Collections.sort(sortedValues);

        SearchState state = new SearchState();
        state.setArray(createSearchElements(sortedValues));
        state.setTarget(target);
        state.setComparisons(0);
        state.setFoundIndex(null);
        state.setSorted(true);

        int low = 0;
        int high = state.getArray().size() - 1;
        state.setLowIndex(low);
        state.setHighIndex(high);

        steps.add(new ExecutionStep(
                steps.size(), "init",
                String.format("Binary search initialized on sorted array for target %d. Search range: [%d..%d].",
                        target, low, high),
                1, state.copy(), makeCounters(state), null
        ));

        while (low <= high) {
            int mid = low + (high - low) / 2;
            state.setLowIndex(low);
            state.setHighIndex(high);
            state.setMidIndex(mid);
            state.getArray().get(mid).setState("mid");
            state.setComparisons(state.getComparisons() + 1);

            int midVal = state.getArray().get(mid).getValue();

            steps.add(new ExecutionStep(
                    steps.size(), "calculate-mid",
                    String.format("Calculated mid = %d (value = %d) between low=%d and high=%d.",
                            mid, midVal, low, high),
                    3, state.copy(), makeCounters(state), null
            ));

            if (midVal == target) {
                state.setFoundIndex(mid);
                state.getArray().get(mid).setState("found");

                steps.add(new ExecutionStep(
                        steps.size(), "found",
                        String.format("TARGET FOUND! Target %d found at index %d in %d logarithmic steps O(log n).",
                                target, mid, state.getComparisons()),
                        4, state.copy(), makeCounters(state), null
                ));
                return steps;
            } else if (midVal < target) {
                // Eliminate left half
                for (int k = low; k <= mid; k++) {
                    state.getArray().get(k).setState("eliminated");
                }
                steps.add(new ExecutionStep(
                        steps.size(), "eliminate-left",
                        String.format("mid (%d) < target (%d). Eliminating left half [%d..%d]. Moving low to %d.",
                                midVal, target, low, mid, mid + 1),
                        6, state.copy(), makeCounters(state), null
                ));
                low = mid + 1;
            } else {
                // Eliminate right half
                for (int k = mid; k <= high; k++) {
                    state.getArray().get(k).setState("eliminated");
                }
                steps.add(new ExecutionStep(
                        steps.size(), "eliminate-right",
                        String.format("mid (%d) > target (%d). Eliminating right half [%d..%d]. Moving high to %d.",
                                midVal, target, mid, high, mid - 1),
                        8, state.copy(), makeCounters(state), null
                ));
                high = mid - 1;
            }
        }

        steps.add(new ExecutionStep(
                steps.size(), "not-found",
                String.format("TARGET NOT FOUND! low (%d) > high (%d). Target %d does not exist in array.",
                        low, high, target),
                10, state.copy(), makeCounters(state), null
        ));

        return steps;
    }
}
