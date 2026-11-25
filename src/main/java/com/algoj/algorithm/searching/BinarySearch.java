package com.algoj.algorithm.searching;

import com.algoj.algorithm.SearchAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SearchModel.SearchElement;
import com.algoj.model.SearchModel.SearchState;
import java.util.*;

public class BinarySearch extends SearchAlgorithm {

    public BinarySearch() {
        super("binary-search");
    }

    @Override
    protected List<ExecutionStep> search(List<Integer> initialValues, int target) {
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
