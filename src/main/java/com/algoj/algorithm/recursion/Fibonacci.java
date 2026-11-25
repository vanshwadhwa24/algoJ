package com.algoj.algorithm.recursion;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.BaseAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import java.util.*;

public class Fibonacci extends BaseAlgorithm {

    public Fibonacci() {
        super("fibonacci");
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return fibonacci(in.n() != null ? in.n() : 6, in.mode() != null ? in.mode() : "tabulated");
    }

    private List<ExecutionStep> fibonacci(int n, String mode) {
        List<ExecutionStep> steps = new ArrayList<>();
        String actualMode = (mode != null) ? mode.toLowerCase() : "tabulated";
        int clampedN = Math.max(0, Math.min("recursive".equals(actualMode) ? 6 : 10, n));

        FibonacciState state = new FibonacciState();
        state.setN(clampedN);
        state.setMode(actualMode);
        state.setMessage(String.format("Computing Fibonacci(%d) using %s approach.", clampedN, actualMode));

        if ("tabulated".equals(actualMode)) {
            List<Integer> dp = new ArrayList<>(Collections.nCopies(clampedN + 1, 0));
            state.setDpArray(dp);

            steps.add(new ExecutionStep(
                    steps.size(), "init",
                    String.format("Initialized Fibonacci DP table of size %d. Base cases: dp[0]=0, dp[1]=1.", clampedN + 1),
                    1, state.copy(), Map.of("totalCalls", 0), null
            ));

            if (clampedN >= 0) dp.set(0, 0);
            if (clampedN >= 1) dp.set(1, 1);
            state.setDpArray(new ArrayList<>(dp));

            for (int i = 2; i <= clampedN; i++) {
                state.setCurrentIndex(i);
                int val = dp.get(i - 1) + dp.get(i - 2);
                dp.set(i, val);
                state.setDpArray(new ArrayList<>(dp));

                steps.add(new ExecutionStep(
                        steps.size(), "tabulate",
                        String.format("dp[%d] = dp[%d] (%d) + dp[%d] (%d) = %d.", i, i - 1, dp.get(i - 1), i - 2, dp.get(i - 2), val),
                        4, state.copy(), Map.of("totalCalls", i), null
                ));
            }

            int finalResult = dp.get(clampedN);
            state.setResult(finalResult);
            steps.add(new ExecutionStep(
                    steps.size(), "complete",
                    String.format("Tabulated Fibonacci(%d) = %d. Bottom-up O(n) time, O(n) space.", clampedN, finalResult),
                    6, state.copy(), Map.of("totalCalls", clampedN), null
            ));

        } else if ("memoized".equals(actualMode)) {
            Map<Integer, Integer> memo = new HashMap<>();
            state.setMemoTable(memo);

            steps.add(new ExecutionStep(
                    steps.size(), "init",
                    String.format("Memoized top-down Fibonacci(%d) initialized.", clampedN),
                    1, state.copy(), Map.of("totalCalls", 0), null
            ));

            int[] calls = new int[]{0};
            int res = fibMemoHelper(clampedN, memo, state, steps, calls);
            state.setResult(res);

            steps.add(new ExecutionStep(
                    steps.size(), "complete",
                    String.format("Memoized Fibonacci(%d) = %d completed! Total calls: %d.", clampedN, res, calls[0]),
                    7, state.copy(), Map.of("totalCalls", calls[0]), null
            ));

        } else {
            // Naive recursive
            steps.add(new ExecutionStep(
                    steps.size(), "init",
                    String.format("Naive Recursive Fibonacci(%d) initialized. Caution: Exponential O(2^n) calls.", clampedN),
                    1, state.copy(), Map.of("totalCalls", 0), null
            ));

            int[] calls = new int[]{0};
            List<String> stack = new ArrayList<>();
            int res = fibRecHelper(clampedN, stack, state, steps, calls);
            state.setResult(res);

            steps.add(new ExecutionStep(
                    steps.size(), "complete",
                    String.format("Naive Recursive Fibonacci(%d) = %d completed! Total calls made: %d.", clampedN, res, calls[0]),
                    6, state.copy(), Map.of("totalCalls", calls[0]), null
            ));
        }

        return steps;
    }

    private int fibMemoHelper(int n, Map<Integer, Integer> memo, FibonacciState state, List<ExecutionStep> steps, int[] calls) {
        calls[0]++;
        state.setTotalCalls(calls[0]);

        if (n <= 1) {
            memo.put(n, n);
            state.setMemoTable(new HashMap<>(memo));
            steps.add(new ExecutionStep(
                    steps.size(), "base-case",
                    String.format("Base case: fib(%d) = %d.", n, n),
                    2, state.copy(), Map.of("totalCalls", calls[0]), null
            ));
            return n;
        }

        if (memo.containsKey(n)) {
            steps.add(new ExecutionStep(
                    steps.size(), "cache-hit",
                    String.format("Cache hit! fib(%d) retrieved from memo table in O(1) time: %d.", n, memo.get(n)),
                    3, state.copy(), Map.of("totalCalls", calls[0]), null
            ));
            return memo.get(n);
        }

        int res = fibMemoHelper(n - 1, memo, state, steps, calls) + fibMemoHelper(n - 2, memo, state, steps, calls);
        memo.put(n, res);
        state.setMemoTable(new HashMap<>(memo));

        steps.add(new ExecutionStep(
                steps.size(), "store-memo",
                String.format("Stored fib(%d) = %d in memo table.", n, res),
                5, state.copy(), Map.of("totalCalls", calls[0]), null
        ));

        return res;
    }

    private int fibRecHelper(int n, List<String> stack, FibonacciState state, List<ExecutionStep> steps, int[] calls) {
        calls[0]++;
        state.setTotalCalls(calls[0]);
        stack.add("fib(" + n + ")");
        state.setCallStack(new ArrayList<>(stack));

        steps.add(new ExecutionStep(
                steps.size(), "call",
                String.format("Calling fib(%d). Stack depth: %d.", n, stack.size()),
                2, state.copy(), Map.of("totalCalls", calls[0]), null
        ));

        if (n <= 1) {
            steps.add(new ExecutionStep(
                    steps.size(), "base-case",
                    String.format("Base case: fib(%d) returns %d.", n, n),
                    3, state.copy(), Map.of("totalCalls", calls[0]), null
            ));
            stack.remove(stack.size() - 1);
            state.setCallStack(new ArrayList<>(stack));
            return n;
        }

        int res = fibRecHelper(n - 1, stack, state, steps, calls) + fibRecHelper(n - 2, stack, state, steps, calls);
        stack.remove(stack.size() - 1);
        state.setCallStack(new ArrayList<>(stack));
        return res;
    }
}
