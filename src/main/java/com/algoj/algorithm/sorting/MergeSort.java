package com.algoj.algorithm.sorting;

import com.algoj.algorithm.SortAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

public class MergeSort extends SortAlgorithm {

    public MergeSort() {
        super("merge-sort");
    }

    @Override
    protected List<ExecutionStep> sort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        steps.add(new ExecutionStep(
                steps.size(), "init", "Merge Sort initialized. Starting divide-and-conquer.", 1,
                state.copy(), makeCounters(state), null
        ));

        runMergeSort(state, 0, state.getArray().size() - 1, steps);

        for (int i = 0; i < state.getArray().size(); i++) {
            state.getArray().get(i).setState("sorted");
            if (!state.getSortedIndices().contains(i)) state.getSortedIndices().add(i);
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Merge Sort completed! Comparisons: %d, Writes: %d.",
                        state.getComparisons(), state.getWrites()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }

    private void runMergeSort(SortState state, int left, int right, List<ExecutionStep> steps) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;

        steps.add(new ExecutionStep(
                steps.size(), "split",
                String.format("Divided subarray [%d..%d] into [%d..%d] and [%d..%d].",
                        left, right, left, mid, mid + 1, right),
                3, state.copy(), makeCounters(state), null
        ));

        runMergeSort(state, left, mid, steps);
        runMergeSort(state, mid + 1, right, steps);
        runMerge(state, left, mid, right, steps);
    }

    private void runMerge(SortState state, int left, int mid, int right, List<ExecutionStep> steps) {
        List<SortElement> aux = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            SortElement el = state.getArray().get(i).copy();
            el.setState("auxiliary");
            aux.add(el);
        }
        state.setAuxiliaryArray(aux);

        steps.add(new ExecutionStep(
                steps.size(), "merge-start",
                String.format("Merging sorted runs [%d..%d] and [%d..%d] using auxiliary buffer.",
                        left, mid, mid + 1, right),
                5, state.copy(), makeCounters(state), null
        ));

        int i = left;
        int j = mid + 1;
        List<SortElement> merged = new ArrayList<>();

        while (i <= mid && j <= right) {
            state.setComparingIndices(List.of(i, j));
            state.setComparisons(state.getComparisons() + 1);

            int valI = state.getArray().get(i).getValue();
            int valJ = state.getArray().get(j).getValue();

            steps.add(new ExecutionStep(
                    steps.size(), "merge-compare",
                    String.format("Comparing left element %d (idx %d) with right element %d (idx %d).",
                            valI, i, valJ, j),
                    6, state.copy(), makeCounters(state), null
            ));

            if (valI <= valJ) {
                merged.add(state.getArray().get(i).copy());
                i++;
            } else {
                merged.add(state.getArray().get(j).copy());
                j++;
            }
        }

        while (i <= mid) {
            merged.add(state.getArray().get(i).copy());
            i++;
        }
        while (j <= right) {
            merged.add(state.getArray().get(j).copy());
            j++;
        }

        for (int k = 0; k < merged.size(); k++) {
            int writeIdx = left + k;
            SortElement item = merged.get(k);
            item.setState("sorted");
            state.getArray().set(writeIdx, item);
            state.setWrites(state.getWrites() + 1);

            steps.add(new ExecutionStep(
                    steps.size(), "merge-write",
                    String.format("Wrote sorted element %d back to main array at index %d.",
                            item.getValue(), writeIdx),
                    7, state.copy(), makeCounters(state), null
            ));
        }

        state.setAuxiliaryArray(null);
        state.setComparingIndices(Collections.emptyList());
    }
}
