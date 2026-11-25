package com.algoj.algorithm.sorting;

import com.algoj.algorithm.SortAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

public class SelectionSort extends SortAlgorithm {

    public SelectionSort() {
        super("selection-sort");
    }

    @Override
    protected List<ExecutionStep> sort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Selection Sort initialized.", 1,
                state.copy(), makeCounters(state), null
        ));

        int n = state.getArray().size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            state.getArray().get(minIdx).setState("pivot");

            steps.add(new ExecutionStep(
                    steps.size(), "select-start",
                    String.format("Starting scan for minimum element in unsorted range [%d..%d]. Initial candidate: %d.",
                            i, n - 1, state.getArray().get(minIdx).getValue()),
                    3, state.copy(), makeCounters(state), null
            ));

            for (int j = i + 1; j < n; j++) {
                state.setComparingIndices(List.of(minIdx, j));
                state.getArray().get(j).setState("comparing");
                state.setComparisons(state.getComparisons() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing current min %d (idx %d) with %d (idx %d).",
                                state.getArray().get(minIdx).getValue(), minIdx,
                                state.getArray().get(j).getValue(), j),
                        5, state.copy(), makeCounters(state), null
                ));

                if (state.getArray().get(j).getValue() < state.getArray().get(minIdx).getValue()) {
                    if (minIdx != i) {
                        state.getArray().get(minIdx).setState("default");
                    }
                    minIdx = j;
                    state.getArray().get(minIdx).setState("pivot");

                    steps.add(new ExecutionStep(
                            steps.size(), "new-min",
                            String.format("Found new smaller element %d at index %d.",
                                    state.getArray().get(minIdx).getValue(), minIdx),
                            5, state.copy(), makeCounters(state), null
                    ));
                } else {
                    state.getArray().get(j).setState("default");
                }
                state.setComparingIndices(Collections.emptyList());
            }

            if (minIdx != i) {
                state.setSwappingIndices(List.of(i, minIdx));
                state.getArray().get(i).setState("swapping");
                state.getArray().get(minIdx).setState("swapping");

                SortElement temp = state.getArray().get(i);
                state.getArray().set(i, state.getArray().get(minIdx));
                state.getArray().set(minIdx, temp);

                state.setSwaps(state.getSwaps() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "swap",
                        String.format("Swapped minimum %d into sorted boundary index %d.",
                                state.getArray().get(i).getValue(), i),
                        7, state.copy(), makeCounters(state), null
                ));
            }

            state.getArray().get(minIdx).setState("default");
            state.getArray().get(i).setState("sorted");
            state.getSortedIndices().add(i);
            state.setSwappingIndices(Collections.emptyList());
        }

        if (n > 0) {
            state.getArray().get(n - 1).setState("sorted");
            if (!state.getSortedIndices().contains(n - 1)) state.getSortedIndices().add(n - 1);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Selection Sort completed! Total comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }
}
