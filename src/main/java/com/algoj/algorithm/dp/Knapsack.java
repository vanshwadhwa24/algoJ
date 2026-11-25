package com.algoj.algorithm.dp;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.BaseAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import java.util.*;

public class Knapsack extends BaseAlgorithm {

    public Knapsack() {
        super("knapsack");
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return knapsack(null, in.n() != null ? in.n() : 5);
    }

    private List<ExecutionStep> knapsack(List<KnapsackItem> itemsInput, int capacityInput) {
        List<ExecutionStep> steps = new ArrayList<>();
        List<KnapsackItem> items = (itemsInput != null && !itemsInput.isEmpty()) ? itemsInput : List.of(
                new KnapsackItem("1", "Book", 1, 3),
                new KnapsackItem("2", "Camera", 2, 4),
                new KnapsackItem("3", "Laptop", 3, 5),
                new KnapsackItem("4", "Tablet", 4, 7)
        );
        int W = capacityInput > 0 ? capacityInput : 5;
        int N = items.size();

        KnapsackState state = new KnapsackState();
        state.setItems(items);
        state.setCapacity(W);

        List<List<Integer>> table = new ArrayList<>();
        for (int i = 0; i <= N; i++) {
            table.add(new ArrayList<>(Collections.nCopies(W + 1, 0)));
        }
        state.setTable(table);
        state.setCurrentRow(0);
        state.setCurrentCol(0);
        state.setOptimalValue(0);
        state.setMessage(String.format("Initialized 0/1 Knapsack DP table of size (%d x %d).", N + 1, W + 1));

        steps.add(new ExecutionStep(
                steps.size(), "init",
                "0/1 Knapsack initialized. Base cases: dp[0][w] = 0 (no items) and dp[i][0] = 0 (capacity 0).",
                2, state.copy(), Map.of("optimalValue", 0), null
        ));

        for (int i = 1; i <= N; i++) {
            KnapsackItem item = items.get(i - 1);
            int wt = item.getWeight();
            int val = item.getValue();

            for (int w = 1; w <= W; w++) {
                state.setCurrentRow(i);
                state.setCurrentCol(w);

                if (wt <= w) {
                    int includeVal = val + table.get(i - 1).get(w - wt);
                    int excludeVal = table.get(i - 1).get(w);
                    int chosen = Math.max(includeVal, excludeVal);
                    table.get(i).set(w, chosen);

                    state.setActiveDependencies(List.of(
                            List.of(i - 1, w),
                            List.of(i - 1, w - wt)
                    ));

                    steps.add(new ExecutionStep(
                            steps.size(), "compute-cell",
                            String.format("Item '%s' (wt:%d, val:%d) fits into capacity %d. max(exclude:%d, include:%d) = %d.",
                                    item.getName(), wt, val, w, excludeVal, includeVal, chosen),
                            5, state.copy(), Map.of("optimalValue", chosen), null
                    ));
                } else {
                    int excludeVal = table.get(i - 1).get(w);
                    table.get(i).set(w, excludeVal);
                    state.setActiveDependencies(List.of(List.of(i - 1, w)));

                    steps.add(new ExecutionStep(
                            steps.size(), "too-heavy",
                            String.format("Item '%s' weight (%d) exceeds capacity %d. Carried over value: %d.",
                                    item.getName(), wt, w, excludeVal),
                            7, state.copy(), Map.of("optimalValue", excludeVal), null
                    ));
                }
            }
        }

        // Backtrack to find selected items
        int optVal = table.get(N).get(W);
        state.setOptimalValue(optVal);

        List<String> selected = new ArrayList<>();
        int currW = W;
        for (int i = N; i > 0 && optVal > 0; i--) {
            if (table.get(i).get(currW) != table.get(i - 1).get(currW)) {
                KnapsackItem it = items.get(i - 1);
                selected.add(it.getName());
                currW -= it.getWeight();
                optVal -= it.getValue();
            }
        }
        Collections.reverse(selected);
        state.setSelectedItems(selected);
        state.setActiveDependencies(Collections.emptyList());

        state.setMessage(String.format("0/1 Knapsack solved! Maximum profit: %d. Selected items: %s.",
                state.getOptimalValue(), selected));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 9, state.copy(), Map.of("optimalValue", state.getOptimalValue()), null
        ));

        return steps;
    }
}
