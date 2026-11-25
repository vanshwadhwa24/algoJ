package com.algoj.algorithm.searching;

import com.algoj.algorithm.SearchAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.SearchModel.SearchElement;
import com.algoj.model.SearchModel.SearchState;
import java.util.*;

public class LinearSearch extends SearchAlgorithm {

    public LinearSearch() {
        super("linear-search");
    }

    @Override
    protected List<ExecutionStep> search(List<Integer> initialValues, int target) {
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
}
