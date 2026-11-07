package com.algoj.engine.dp;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DPAlgorithmEngine {

    // ==========================================
    // 1. 0/1 KNAPSACK
    // ==========================================
    public List<ExecutionStep> knapsack(List<KnapsackItem> itemsInput, int capacityInput) {
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

    // ==========================================
    // 2. LCS (Longest Common Subsequence)
    // ==========================================
    public List<ExecutionStep> lcs(String s1Input, String s2Input) {
        List<ExecutionStep> steps = new ArrayList<>();
        String str1 = (s1Input != null && !s1Input.isEmpty()) ? s1Input.toUpperCase() : "STONE";
        String str2 = (s2Input != null && !s2Input.isEmpty()) ? s2Input.toUpperCase() : "LONGEST";
        int m = str1.length();
        int n = str2.length();

        LCSState state = new LCSState();
        state.setS1(str1);
        state.setS2(str2);

        List<List<Integer>> table = new ArrayList<>();
        for (int i = 0; i <= m; i++) {
            table.add(new ArrayList<>(Collections.nCopies(n + 1, 0)));
        }
        state.setTable(table);
        state.setCurrentRow(0);
        state.setCurrentCol(0);
        state.setLcsResult("");
        state.setMessage(String.format("Initialized LCS DP table (%d x %d) for '%s' and '%s'.", m + 1, n + 1, str1, str2));

        steps.add(new ExecutionStep(
                steps.size(), "init",
                String.format("LCS initialized for '%s' and '%s'. Row 0 and Col 0 set to 0.", str1, str2),
                2, state.copy(), Map.of("lcsLength", 0), null
        ));

        for (int i = 1; i <= m; i++) {
            char c1 = str1.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                char c2 = str2.charAt(j - 1);
                state.setCurrentRow(i);
                state.setCurrentCol(j);

                if (c1 == c2) {
                    int val = 1 + table.get(i - 1).get(j - 1);
                    table.get(i).set(j, val);
                    state.setActiveDependencies(List.of(List.of(i - 1, j - 1)));

                    steps.add(new ExecutionStep(
                            steps.size(), "match",
                            String.format("MATCH '%s' == '%s'! dp[%d][%d] = 1 + dp[%d][%d] (%d) = %d.",
                                    c1, c2, i, j, i - 1, j - 1, table.get(i - 1).get(j - 1), val),
                            4, state.copy(), Map.of("lcsLength", val), null
                    ));
                } else {
                    int top = table.get(i - 1).get(j);
                    int left = table.get(i).get(j - 1);
                    int val = Math.max(top, left);
                    table.get(i).set(j, val);
                    state.setActiveDependencies(List.of(List.of(i - 1, j), List.of(i, j - 1)));

                    steps.add(new ExecutionStep(
                            steps.size(), "mismatch",
                            String.format("MISMATCH '%s' != '%s'. dp[%d][%d] = max(top:%d, left:%d) = %d.",
                                    c1, c2, i, j, top, left, val),
                            6, state.copy(), Map.of("lcsLength", val), null
                    ));
                }
            }
        }

        // Backtrack to build LCS string
        StringBuilder sb = new StringBuilder();
        int i = m;
        int j = n;
        while (i > 0 && j > 0) {
            if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                sb.append(str1.charAt(i - 1));
                i--;
                j--;
            } else if (table.get(i - 1).get(j) >= table.get(i).get(j - 1)) {
                i--;
            } else {
                j--;
            }
        }
        String lcsStr = sb.reverse().toString();
        state.setLcsResult(lcsStr);
        state.setActiveDependencies(Collections.emptyList());

        state.setMessage(String.format("LCS completed! Length: %d, Subsequence: '%s'.", table.get(m).get(n), lcsStr));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 8, state.copy(), Map.of("lcsLength", lcsStr.length()), null
        ));

        return steps;
    }
}
