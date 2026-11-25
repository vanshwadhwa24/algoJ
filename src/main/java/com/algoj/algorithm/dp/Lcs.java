package com.algoj.algorithm.dp;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.BaseAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import java.util.*;

public class Lcs extends BaseAlgorithm {

    public Lcs() {
        super("lcs");
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return lcs(in.text1(), in.text2());
    }

    private List<ExecutionStep> lcs(String s1Input, String s2Input) {
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
