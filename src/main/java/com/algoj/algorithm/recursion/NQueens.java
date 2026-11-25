package com.algoj.algorithm.recursion;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.BaseAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import java.util.*;

public class NQueens extends BaseAlgorithm {

    public NQueens() {
        super("n-queens");
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return nQueens(in.n() != null ? in.n() : 4);
    }

    private List<ExecutionStep> nQueens(int n) {
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
