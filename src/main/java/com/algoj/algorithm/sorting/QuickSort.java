package com.algoj.algorithm.sorting;

import com.algoj.algorithm.SortAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

public class QuickSort extends SortAlgorithm {

    public QuickSort() {
        super("quick-sort");
    }

    @Override
    protected List<ExecutionStep> sort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Quick Sort initialized (Lomuto partition scheme).", 1,
                state.copy(), makeCounters(state), null
        ));

        runQuickSort(state, 0, state.getArray().size() - 1, steps);

        for (int i = 0; i < state.getArray().size(); i++) {
            state.getArray().get(i).setState("sorted");
            if (!state.getSortedIndices().contains(i)) state.getSortedIndices().add(i);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Quick Sort completed! Comparisons: %d, Swaps: %d.",
                        state.getComparisons(), state.getSwaps()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    private void runQuickSort(SortState state, int low, int high, List<ExecutionStep> steps) {
        if (low < high) {
            int pIndex = lomutoPartition(state, low, high, steps);
            runQuickSort(state, low, pIndex - 1, steps);
            runQuickSort(state, pIndex + 1, high, steps);
        } else if (low == high) {
            state.getArray().get(low).setState("sorted");
            if (!state.getSortedIndices().contains(low)) state.getSortedIndices().add(low);
        }
    }

    private int lomutoPartition(SortState state, int low, int high, List<ExecutionStep> steps) {
        int pivotVal = state.getArray().get(high).getValue();
        state.getArray().get(high).setState("pivot");
        state.setPivotIndex(high);

        steps.add(new ExecutionStep(
                steps.size(), "set-pivot",
                String.format("Chosen pivot %d at index %d for partition range [%d..%d].",
                        pivotVal, high, low, high),
                3, state.copy(), makeCounters(state), null
        ));

        int i = low - 1;

        for (int j = low; j < high; j++) {
            state.setComparingIndices(List.of(j, high));
            state.getArray().get(j).setState("comparing");
            state.setComparisons(state.getComparisons() + 1);

            steps.add(new ExecutionStep(
                    steps.size(), "compare",
                    String.format("Comparing index %d (%d) with pivot %d.",
                            j, state.getArray().get(j).getValue(), pivotVal),
                    5, state.copy(), makeCounters(state), null
            ));

            if (state.getArray().get(j).getValue() <= pivotVal) {
                i++;
                if (i != j) {
                    state.setSwappingIndices(List.of(i, j));
                    state.getArray().get(i).setState("swapping");
                    state.getArray().get(j).setState("swapping");

                    SortElement temp = state.getArray().get(i);
                    state.getArray().set(i, state.getArray().get(j));
                    state.getArray().set(j, temp);
                    state.setSwaps(state.getSwaps() + 1);

                    steps.add(new ExecutionStep(
                            steps.size(), "swap",
                            String.format("Swapped %d (idx %d) into smaller-partition at index %d.",
                                    state.getArray().get(i).getValue(), j, i),
                            6, state.copy(), makeCounters(state), null
                    ));
                }
            }

            state.getArray().get(j).setState("default");
            if (i >= low) state.getArray().get(i).setState("partition");
            state.setComparingIndices(Collections.emptyList());
            state.setSwappingIndices(Collections.emptyList());
        }

        // Place pivot into i + 1
        int pivotFinalIdx = i + 1;
        state.setSwappingIndices(List.of(pivotFinalIdx, high));
        state.getArray().get(pivotFinalIdx).setState("swapping");
        state.getArray().get(high).setState("swapping");

        SortElement temp = state.getArray().get(pivotFinalIdx);
        state.getArray().set(pivotFinalIdx, state.getArray().get(high));
        state.getArray().set(high, temp);
        state.setSwaps(state.getSwaps() + 1);

        state.getArray().get(pivotFinalIdx).setState("sorted");
        state.getSortedIndices().add(pivotFinalIdx);
        state.setPivotIndex(null);
        state.setSwappingIndices(Collections.emptyList());

        for (int k = low; k <= high; k++) {
            if (k != pivotFinalIdx && !state.getSortedIndices().contains(k)) {
                state.getArray().get(k).setState("default");
            }
        }

        steps.add(new ExecutionStep(
                steps.size(), "pivot-placed",
                String.format("Pivot %d placed into final sorted position %d.", pivotVal, pivotFinalIdx),
                8, state.copy(), makeCounters(state), null
        ));

        return pivotFinalIdx;
    }
}
