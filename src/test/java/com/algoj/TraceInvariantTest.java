package com.algoj;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.GraphModel.GraphData;
import com.algoj.model.SortModel.SortState;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.algoj.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

/** Checks the intermediate steps, not just the final answer: that is what the visualizer plays back. */
class TraceInvariantTest {

    private static final List<String> SORTS = List.of("bubble-sort", "selection-sort", "insertion-sort", "merge-sort", "quick-sort", "heap-sort");

    private static final Set<String> IN_TRANSIT = Set.of("insertion-sort", "merge-sort");

    @Test
    void everySortStepKeepsTheSameElementsAndCountersNeverDecrease() {
        Random random = new Random(11);
        for (String id : SORTS) {
            for (int it = 0; it < 60; it++) {
                List<Integer> in = new ArrayList<>();
                for (int i = 0, n = 2 + random.nextInt(9); i < n; i++) in.add(random.nextInt(20));
                List<Integer> multiset = new ArrayList<>(in);
                Collections.sort(multiset);
                List<ExecutionStep> steps = run(id, AlgorithmInput.ofArray(in, null));
                int comparisons = 0, swaps = 0;
                for (int s = 0; s < steps.size(); s++) {
                    ExecutionStep step = steps.get(s);
                    assertEquals(s, step.stepIndex(), id + " step index");
                    SortState state = (SortState) step.state();
                    List<Integer> now = new ArrayList<>();
                    state.getArray().forEach(e -> now.add(e.getValue()));
                    Collections.sort(now);
                    if (IN_TRANSIT.contains(id)) {
                        // These sorts overwrite slots while shifting, so a value can briefly appear twice.
                        assertEquals(in.size(), now.size(), id + " step " + s + " changed the array length");
                        assertTrue(new HashSet<>(in).containsAll(now), id + " step " + s + " invented a value for input " + in);
                    } else {
                        assertEquals(multiset, now, id + " step " + s + " lost or invented an element for input " + in);
                    }
                    for (int idx : state.getComparingIndices()) assertTrue(idx >= 0 && idx < in.size(), id + " comparing index out of range");
                    for (int idx : state.getSwappingIndices()) assertTrue(idx >= 0 && idx < in.size(), id + " swapping index out of range");
                    assertTrue(state.getComparisons() >= comparisons, id + " comparisons went down at step " + s);
                    assertTrue(state.getSwaps() >= swaps, id + " swaps went down at step " + s);
                    comparisons = state.getComparisons();
                    swaps = state.getSwaps();
                }
                List<Integer> last = new ArrayList<>();
                ((SortState) steps.get(steps.size() - 1).state()).getArray().forEach(e -> last.add(e.getValue()));
                assertEquals(multiset, last);
            }
        }
    }

    @Test
    void everyAlgorithmProducesAnOrderedNonEmptyTraceOnDefaultInput() {
        for (String id : List.of("bubble-sort", "selection-sort", "insertion-sort", "merge-sort", "quick-sort", "heap-sort",
                "linear-search", "binary-search", "bfs", "dfs", "dijkstra", "bellman-ford", "floyd-warshall", "kruskal", "prim",
                "factorial", "fibonacci", "tower-of-hanoi", "n-queens", "knapsack", "lcs")) {
            List<ExecutionStep> steps = run(id, AlgorithmInput.empty());
            assertFalse(steps.isEmpty(), id);
            for (int i = 0; i < steps.size(); i++) {
                assertEquals(i, steps.get(i).stepIndex(), id);
                assertNotNull(steps.get(i).state(), id + " step " + i);
                assertNotNull(steps.get(i).description(), id + " step " + i);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    void shortestPathDistancesOnlyDecreaseAndTraversalsOnlyGrow() {
        for (String id : List.of("dijkstra", "bellman-ford")) {
            Map<String, Double> best = new HashMap<>();
            for (ExecutionStep step : run(id, AlgorithmInput.empty())) {
                Map<String, Object> distances = (Map<String, Object>) state(step).get("distances");
                distances.forEach((v, d) -> {
                    double now = num(d);
                    assertTrue(now <= best.getOrDefault(v, Double.POSITIVE_INFINITY), id + " distance to " + v + " increased");
                    best.put(v, now);
                });
            }
        }
        for (String id : List.of("bfs", "dfs")) {
            int seen = 0;
            for (ExecutionStep step : run(id, AlgorithmInput.empty())) {
                int now = ((List<?>) state(step).get("visited")).size();
                assertTrue(now >= seen, id + " visited set shrank");
                seen = now;
            }
        }
    }

    @Test
    void hanoiMovesAreLegalAtEveryStep() {
        List<ExecutionStep> steps = run("tower-of-hanoi", new AlgorithmInput(null, null, null, null, 4, null, null, null));
        for (ExecutionStep step : steps) {
            @SuppressWarnings("unchecked") Map<String, List<Integer>> pegs = (Map<String, List<Integer>>) state(step).get("pegs");
            pegs.forEach((peg, disks) -> {
                for (int i = 1; i < disks.size(); i++) assertTrue(disks.get(i - 1) > disks.get(i), "larger disk on top of smaller at peg " + peg + ": " + disks);
            });
        }
    }
}
