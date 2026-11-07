package com.algoj.engine.recursion;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecursionAlgorithmEngine {

    // ==========================================
    // 1. FACTORIAL
    // ==========================================
    public List<ExecutionStep> factorial(int n) {
        List<ExecutionStep> steps = new ArrayList<>();
        int clampedN = Math.max(0, Math.min(7, n));

        FactorialState state = new FactorialState();
        state.setN(clampedN);
        state.setMessage(String.format("Computing Factorial(%d).", clampedN));

        steps.add(new ExecutionStep(
                steps.size(), "init",
                String.format("Calling Factorial(%d). Base case is n <= 1.", clampedN),
                1, state.copy(), Map.of("callStackDepth", 0), null
        ));

        List<StackFrame> callStack = new ArrayList<>();
        int res = factHelper(clampedN, callStack, state, steps);
        state.setResult(res);

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Factorial(%d) = %d completed. Call stack unwound completely.", clampedN, res),
                6, state.copy(), Map.of("callStackDepth", 0), null
        ));

        return steps;
    }

    private int factHelper(int val, List<StackFrame> callStack, FactorialState state, List<ExecutionStep> steps) {
        StackFrame frame = new StackFrame("frame-" + val, "fact(" + val + ")", val, val <= 1 ? "base-case" : "calling");
        callStack.add(frame);
        state.setCallStack(new ArrayList<>(callStack));

        steps.add(new ExecutionStep(
                steps.size(), val <= 1 ? "base-case" : "call",
                val <= 1 ? String.format("Reached base case: fact(%d) returns 1 immediately.", val)
                         : String.format("Pushed fact(%d) onto call stack. Awaiting fact(%d).", val, val - 1),
                val <= 1 ? 2 : 4, state.copy(), Map.of("callStackDepth", callStack.size()), null
        ));

        if (val <= 1) {
            frame.setReturnValue(1);
            frame.setState("returned");
            return 1;
        }

        int sub = factHelper(val - 1, callStack, state, steps);
        int total = val * sub;
        frame.setReturnValue(total);
        frame.setState("returned");

        steps.add(new ExecutionStep(
                steps.size(), "return-multiply",
                String.format("fact(%d) computes %d * fact(%d) (%d) = %d. Returning %d.", val, val, val - 1, sub, total, total),
                5, state.copy(), Map.of("callStackDepth", callStack.size()), null
        ));

        callStack.remove(callStack.size() - 1);
        state.setCallStack(new ArrayList<>(callStack));
        return total;
    }

    // ==========================================
    // 2. FIBONACCI
    // ==========================================
    public List<ExecutionStep> fibonacci(int n, String mode) {
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

    // ==========================================
    // 3. TOWER OF HANOI
    // ==========================================
    public List<ExecutionStep> towerOfHanoi(int disks) {
        List<ExecutionStep> steps = new ArrayList<>();
        int clampedDisks = Math.max(1, Math.min(5, disks));

        HanoiState state = new HanoiState();
        state.setDisksCount(clampedDisks);
        state.setMoveCount(0);

        Map<String, List<Integer>> pegs = new HashMap<>();
        List<Integer> pegA = new ArrayList<>();
        for (int i = clampedDisks; i >= 1; i--) pegA.add(i);
        pegs.put("A", pegA);
        pegs.put("B", new ArrayList<>());
        pegs.put("C", new ArrayList<>());
        state.setPegs(pegs);

        state.setMessage(String.format("Tower of Hanoi with %d disks initialized. Minimum moves: 2^%d - 1 = %d.",
                clampedDisks, clampedDisks, (1 << clampedDisks) - 1));

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy(), Map.of("moveCount", 0), null
        ));

        hanoiHelper(clampedDisks, "A", "C", "B", state, steps);

        state.setMessage(String.format("Tower of Hanoi completed in %d moves! All disks successfully transferred.", state.getMoveCount()));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 6, state.copy(), Map.of("moveCount", state.getMoveCount()), null
        ));

        return steps;
    }

    private void hanoiHelper(int n, String source, String target, String auxiliary, HanoiState state, List<ExecutionStep> steps) {
        if (n == 0) return;

        hanoiHelper(n - 1, source, auxiliary, target, state, steps);

        // Move disk from source to target
        List<Integer> fromPeg = state.getPegs().get(source);
        List<Integer> toPeg = state.getPegs().get(target);
        int disk = fromPeg.remove(fromPeg.size() - 1);
        toPeg.add(disk);

        state.setMoveCount(state.getMoveCount() + 1);
        Map<String, Object> move = Map.of("disk", disk, "from", source, "to", target);
        state.setLastMove(move);

        steps.add(new ExecutionStep(
                steps.size(), "move",
                String.format("Move #%d: Moved disk %d from Peg %s to Peg %s.", state.getMoveCount(), disk, source, target),
                4, state.copy(), Map.of("moveCount", state.getMoveCount()), null
        ));

        hanoiHelper(n - 1, auxiliary, target, source, state, steps);
    }

    // ==========================================
    // 4. N-QUEENS
    // ==========================================
    public List<ExecutionStep> nQueens(int n) {
        List<ExecutionStep> steps = new ArrayList<>();
        int clampedN = Math.max(4, Math.min(6, n));

        NQueensState state = new NQueensState();
        state.setN(clampedN);
        state.setBoard(new ArrayList<>(Collections.nCopies(clampedN, -1)));
        state.setCurrentRow(0);
        state.setCurrentCol(0);
        state.setConflict(false);
        state.setSolutionsFound(0);
        state.setMessage(String.format("Initialized %d-Queens puzzle on a %dx%d board.", clampedN, clampedN, clampedN));

        steps.add(new ExecutionStep(
                steps.size(), "init", state.getMessage(), 1, state.copy(), Map.of("solutions", 0), null
        ));

        nQueensSolve(0, clampedN, state, steps);

        state.setMessage(String.format("N-Queens backtracking complete! Total valid solutions found: %d.", state.getSolutionsFound()));
        steps.add(new ExecutionStep(
                steps.size(), "complete", state.getMessage(), 8, state.copy(), Map.of("solutions", state.getSolutionsFound()), null
        ));

        return steps;
    }

    private void nQueensSolve(int row, int n, NQueensState state, List<ExecutionStep> steps) {
        if (row == n) {
            state.setSolutionsFound(state.getSolutionsFound() + 1);
            state.setMessage(String.format("VALID SOLUTION #%d FOUND! All %d queens placed peacefully without attacks.", state.getSolutionsFound(), n));
            steps.add(new ExecutionStep(
                    steps.size(), "solution-found", state.getMessage(), 3, state.copy(), Map.of("solutions", state.getSolutionsFound()), null
            ));
            return;
        }

        for (int col = 0; col < n; col++) {
            state.setCurrentRow(row);
            state.setCurrentCol(col);
            state.getBoard().set(row, col);

            boolean safe = isSafe(row, col, state.getBoard());
            state.setConflict(!safe);

            steps.add(new ExecutionStep(
                    steps.size(), safe ? "place-queen" : "conflict",
                    safe ? String.format("Placed Queen at (row %d, col %d). No conflicts. Advancing to row %d.", row, col, row + 1)
                         : String.format("Conflict at (row %d, col %d) with existing queens. Backtracking...", row, col),
                    safe ? 5 : 6, state.copy(), Map.of("solutions", state.getSolutionsFound()), null
            ));

            if (safe) {
                nQueensSolve(row + 1, n, state, steps);
            }
            state.getBoard().set(row, -1);
        }
    }

    private boolean isSafe(int row, int col, List<Integer> board) {
        for (int prevRow = 0; prevRow < row; prevRow++) {
            int prevCol = board.get(prevRow);
            if (prevCol == col) return false; // same column
            if (Math.abs(prevCol - col) == Math.abs(prevRow - row)) return false; // diagonal
        }
        return true;
    }
}
