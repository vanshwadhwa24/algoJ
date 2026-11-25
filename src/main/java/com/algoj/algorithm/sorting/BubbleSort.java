package com.algoj.algorithm.sorting;

import com.algoj.algorithm.SortAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

public class BubbleSort extends SortAlgorithm {

    public BubbleSort() {
        super("bubble-sort");
    }

    @Override
    protected List<ExecutionStep> sort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Initial unsorted array loaded.", 1,
                state.copy(), makeCounters(state), null
        ));

        int n = state.getArray().size();
        for (int i = 0; i < n - 1; i++) {
            boolean swappedInPass = false;
            for (int j = 0; j < n - i - 1; j++) {
                state.setComparingIndices(List.of(j, j + 1));
                state.setSwappingIndices(Collections.emptyList());
                state.getArray().get(j).setState("comparing");
                state.getArray().get(j + 1).setState("comparing");
                state.setComparisons(state.getComparisons() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing index %d (%d) and %d (%d).",
                                j, state.getArray().get(j).getValue(),
                                j + 1, state.getArray().get(j + 1).getValue()),
                        4, state.copy(), makeCounters(state), null
                ));

                if (state.getArray().get(j).getValue() > state.getArray().get(j + 1).getValue()) {
                    state.setSwappingIndices(List.of(j, j + 1));
                    state.getArray().get(j).setState("swapping");
                    state.getArray().get(j + 1).setState("swapping");

                    SortElement temp = state.getArray().get(j);
                    state.getArray().set(j, state.getArray().get(j + 1));
                    state.getArray().set(j + 1, temp);

                    state.setSwaps(state.getSwaps() + 1);
                    swappedInPass = true;

                    steps.add(new ExecutionStep(
                            steps.size(), "swap",
                            String.format("Swapped %d and %d because %d > %d.",
                                    state.getArray().get(j).getValue(), state.getArray().get(j + 1).getValue(),
                                    state.getArray().get(j + 1).getValue(), state.getArray().get(j).getValue()),
                            5, state.copy(), makeCounters(state), null
                    ));
                }

                state.getArray().get(j).setState("default");
                state.getArray().get(j + 1).setState("default");
                state.setComparingIndices(Collections.emptyList());
                state.setSwappingIndices(Collections.emptyList());
            }

            int settledIdx = n - i - 1;
            state.getSortedIndices().add(settledIdx);
            state.getArray().get(settledIdx).setState("sorted");

            steps.add(new ExecutionStep(
                    steps.size(), "settle",
                    String.format("Element %d is now settled at final sorted position %d.",
                            state.getArray().get(settledIdx).getValue(), settledIdx),
                    2, state.copy(), makeCounters(state), null
            ));

            if (!swappedInPass) {
                for (int k = 0; k < settledIdx; k++) {
                    if (!state.getSortedIndices().contains(k)) {
                        state.getSortedIndices().add(k);
                        state.getArray().get(k).setState("sorted");
                    }
                }
                steps.add(new ExecutionStep(
                        steps.size(), "early-exit",
                        "No swaps occurred in this pass. Array is fully sorted (Early termination O(n)).",
                        6, state.copy(), makeCounters(state), null
                ));
                break;
            }
        }

        for (int k = 0; k < n; k++) {
            if (!state.getSortedIndices().contains(k)) {
                state.getSortedIndices().add(k);
                state.getArray().get(k).setState("sorted");
            }
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Bubble Sort completed! Total comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }
}
