package com.algoj.algorithm.recursion;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.BaseAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import java.util.*;

public class TowerOfHanoi extends BaseAlgorithm {

    public TowerOfHanoi() {
        super("tower-of-hanoi");
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return towerOfHanoi(in.disks() != null ? in.disks() : 3);
    }

    private List<ExecutionStep> towerOfHanoi(int disks) {
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
}
