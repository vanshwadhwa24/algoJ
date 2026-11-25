package com.algoj.algorithm.sorting;

import com.algoj.algorithm.SortAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

public class HeapSort extends SortAlgorithm {

    public HeapSort() {
        super("heap-sort");
    }

    @Override
    protected List<ExecutionStep> sort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Heap Sort initialized. Building max heap.", 1,
                state.copy(), makeCounters(state), null
        ));

        int n = state.getArray().size();

        // 1. Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(state, n, i, steps);
        }

        steps.add(new ExecutionStep(
                steps.size(), "heap-built", "Max heap structure built! Largest element is at index 0.", 3,
                state.copy(), makeCounters(state), null
        ));

        // 2. Extract elements one by one
        for (int i = n - 1; i > 0; i--) {
            state.setSwappingIndices(List.of(0, i));
            state.getArray().get(0).setState("swapping");
            state.getArray().get(i).setState("swapping");

            SortElement temp = state.getArray().get(0);
            state.getArray().set(0, state.getArray().get(i));
            state.getArray().set(i, temp);
            state.setSwaps(state.getSwaps() + 1);

            state.getArray().get(i).setState("sorted");
            state.getSortedIndices().add(i);

            steps.add(new ExecutionStep(
                    steps.size(), "extract-max",
                    String.format("Swapped max element %d to end at sorted position %d.", temp.getValue(), i),
                    5, state.copy(), makeCounters(state), null
            ));

            state.setSwappingIndices(Collections.emptyList());
            heapify(state, i, 0, steps);
        }

        if (n > 0) {
            state.getArray().get(0).setState("sorted");
            if (!state.getSortedIndices().contains(0)) state.getSortedIndices().add(0);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Heap Sort completed! Comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    private void heapify(SortState state, int n, int i, List<ExecutionStep> steps) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            state.setComparingIndices(List.of(left, largest));
            state.setComparisons(state.getComparisons() + 1);
            if (state.getArray().get(left).getValue() > state.getArray().get(largest).getValue()) {
                largest = left;
            }
        }

        if (right < n) {
            state.setComparingIndices(List.of(right, largest));
            state.setComparisons(state.getComparisons() + 1);
            if (state.getArray().get(right).getValue() > state.getArray().get(largest).getValue()) {
                largest = right;
            }
        }

        if (largest != i) {
            state.setSwappingIndices(List.of(i, largest));
            state.getArray().get(i).setState("swapping");
            state.getArray().get(largest).setState("swapping");

            SortElement temp = state.getArray().get(i);
            state.getArray().set(i, state.getArray().get(largest));
            state.getArray().set(largest, temp);
            state.setSwaps(state.getSwaps() + 1);

            steps.add(new ExecutionStep(
                    steps.size(), "heapify-swap",
                    String.format("Heapify: Swapped parent %d with larger child %d.",
                            temp.getValue(), state.getArray().get(i).getValue()),
                    6, state.copy(), makeCounters(state), null
            ));

            state.getArray().get(i).setState("default");
            state.getArray().get(largest).setState("default");
            state.setComparingIndices(Collections.emptyList());
            state.setSwappingIndices(Collections.emptyList());

            heapify(state, n, largest, steps);
        }
    }
}
