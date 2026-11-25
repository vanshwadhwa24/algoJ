package com.algoj.algorithm.sorting;

import com.algoj.algorithm.SortAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import java.util.*;

public class InsertionSort extends SortAlgorithm {

    public InsertionSort() {
        super("insertion-sort");
    }

    @Override
    protected List<ExecutionStep> sort(List<Integer> initialValues) {
        List<ExecutionStep> steps = new ArrayList<>();
        SortState state = new SortState();
        state.setArray(createSortElements(initialValues));

        int n = state.getArray().size();
        if (n > 0) {
            state.getArray().get(0).setState("sorted");
            state.getSortedIndices().add(0);
        }

        steps.add(new ExecutionStep(
                steps.size(), "init", "Insertion Sort initialized. Index 0 considered trivially sorted.", 1,
                state.copy(), makeCounters(state), null
        ));

        for (int i = 1; i < n; i++) {
            SortElement key = state.getArray().get(i).copy();
            state.getArray().get(i).setState("pivot");
            state.setPivotIndex(i);

            steps.add(new ExecutionStep(
                    steps.size(), "select-key",
                    String.format("Selected key %d at index %d to insert into sorted prefix [0..%d].",
                            key.getValue(), i, i - 1),
                    3, state.copy(), makeCounters(state), null
            ));

            int j = i - 1;
            while (j >= 0) {
                state.setComparingIndices(List.of(j, j + 1));
                state.getArray().get(j).setState("comparing");
                state.setComparisons(state.getComparisons() + 1);

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing key %d with sorted prefix element %d at index %d.",
                                key.getValue(), state.getArray().get(j).getValue(), j),
                        4, state.copy(), makeCounters(state), null
                ));

                if (state.getArray().get(j).getValue() > key.getValue()) {
                    state.getArray().set(j + 1, state.getArray().get(j).copy());
                    state.getArray().get(j + 1).setState("swapping");
                    state.setWrites(state.getWrites() + 1);

                    steps.add(new ExecutionStep(
                            steps.size(), "shift",
                            String.format("Shifted %d one position right to index %d.",
                                    state.getArray().get(j + 1).getValue(), j + 1),
                            5, state.copy(), makeCounters(state), null
                    ));

                    state.getArray().get(j + 1).setState("sorted");
                    state.getArray().get(j).setState("default");
                    j--;
                } else {
                    state.getArray().get(j).setState("sorted");
                    break;
                }
            }

            state.getArray().set(j + 1, key);
            state.getArray().get(j + 1).setState("sorted");
            state.setWrites(state.getWrites() + 1);

            for (int k = 0; k <= i; k++) {
                if (!state.getSortedIndices().contains(k)) state.getSortedIndices().add(k);
                state.getArray().get(k).setState("sorted");
            }
            state.setComparingIndices(Collections.emptyList());
            state.setPivotIndex(null);

            steps.add(new ExecutionStep(
                    steps.size(), "inserted",
                    String.format("Inserted key %d into position %d. Prefix [0..%d] is now sorted.",
                            key.getValue(), j + 1, i),
                    6, state.copy(), makeCounters(state), null
            ));
        }

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Insertion Sort completed! Comparisons: %d, Writes/Shifts: %d.",
                        state.getComparisons(), state.getWrites()),
                1, state.copy(), makeCounters(state), null
        ));

        return steps;
    }
}
