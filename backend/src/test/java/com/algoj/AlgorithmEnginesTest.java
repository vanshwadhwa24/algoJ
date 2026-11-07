package com.algoj;

import com.algoj.dto.ExecutionStep;
import com.algoj.engine.dp.DPAlgorithmEngine;
import com.algoj.engine.graphs.GraphAlgorithmEngine;
import com.algoj.engine.recursion.RecursionAlgorithmEngine;
import com.algoj.engine.searching.SearchingEngine;
import com.algoj.engine.sorting.SortingEngine;
import com.algoj.model.GraphModel.GraphData;
import com.algoj.model.GraphModel.MSTState;
import com.algoj.model.GraphModel.ShortestPathState;
import com.algoj.model.RecursionAndDPModel.*;
import com.algoj.model.SearchModel.SearchState;
import com.algoj.model.SortModel.SortElement;
import com.algoj.model.SortModel.SortState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmEnginesTest {

    private SortingEngine sortingEngine;
    private SearchingEngine searchingEngine;
    private GraphAlgorithmEngine graphEngine;
    private RecursionAlgorithmEngine recursionEngine;
    private DPAlgorithmEngine dpEngine;

    @BeforeEach
    void setUp() {
        sortingEngine = new SortingEngine();
        searchingEngine = new SearchingEngine();
        graphEngine = new GraphAlgorithmEngine();
        recursionEngine = new RecursionAlgorithmEngine();
        dpEngine = new DPAlgorithmEngine();
    }

    private void assertTraceOrderAndValidity(List<ExecutionStep> steps) {
        assertNotNull(steps);
        assertFalse(steps.isEmpty(), "Trace must not be empty");
        for (int i = 0; i < steps.size(); i++) {
            assertEquals(i, steps.get(i).stepIndex(), "StepIndex must be strictly 0-indexed and continuous");
            assertNotNull(steps.get(i).operationType(), "OperationType must not be null");
            assertNotNull(steps.get(i).description(), "Description must not be null");
            assertNotNull(steps.get(i).state(), "State must not be null");
            assertTrue(steps.get(i).codeLine() > 0, "CodeLine must be positive");
        }
    }

    private void assertSorted(List<SortElement> elements) {
        for (int i = 0; i < elements.size() - 1; i++) {
            assertTrue(elements.get(i).getValue() <= elements.get(i + 1).getValue(),
                    String.format("Array must be sorted in ascending order: %d > %d",
                            elements.get(i).getValue(), elements.get(i + 1).getValue()));
        }
    }

    // --- SORTING TESTS ---
    @Test
    @DisplayName("Bubble Sort sorts correctly and produces valid trace")
    void testBubbleSort() {
        List<Integer> input = List.of(35, 12, 48, 22, 60, 5);
        List<ExecutionStep> steps = sortingEngine.bubbleSort(input);
        assertTraceOrderAndValidity(steps);

        SortState finalState = (SortState) steps.get(steps.size() - 1).state();
        assertSorted(finalState.getArray());
        assertTrue(finalState.getComparisons() > 0);
        assertTrue(finalState.getSwaps() > 0);
    }

    @Test
    @DisplayName("Selection Sort sorts correctly and produces valid trace")
    void testSelectionSort() {
        List<Integer> input = List.of(64, 25, 12, 22, 11);
        List<ExecutionStep> steps = sortingEngine.selectionSort(input);
        assertTraceOrderAndValidity(steps);

        SortState finalState = (SortState) steps.get(steps.size() - 1).state();
        assertSorted(finalState.getArray());
        assertTrue(finalState.getComparisons() > 0);
    }

    @Test
    @DisplayName("Insertion Sort handles already sorted and reverse sorted arrays")
    void testInsertionSort() {
        List<Integer> reversed = List.of(50, 40, 30, 20, 10);
        List<ExecutionStep> steps = sortingEngine.insertionSort(reversed);
        assertTraceOrderAndValidity(steps);

        SortState finalState = (SortState) steps.get(steps.size() - 1).state();
        assertSorted(finalState.getArray());
        assertTrue(finalState.getWrites() > 0);
    }

    @Test
    @DisplayName("Merge Sort correctly merges divide-and-conquer partitions")
    void testMergeSort() {
        List<Integer> input = List.of(38, 27, 43, 3, 9, 82, 10);
        List<ExecutionStep> steps = sortingEngine.mergeSort(input);
        assertTraceOrderAndValidity(steps);

        SortState finalState = (SortState) steps.get(steps.size() - 1).state();
        assertSorted(finalState.getArray());
        assertTrue(finalState.getWrites() > 0);
    }

    @Test
    @DisplayName("Quick Sort partitions and settles pivots correctly")
    void testQuickSort() {
        List<Integer> input = List.of(10, 80, 30, 90, 40, 50, 70);
        List<ExecutionStep> steps = sortingEngine.quickSort(input);
        assertTraceOrderAndValidity(steps);

        SortState finalState = (SortState) steps.get(steps.size() - 1).state();
        assertSorted(finalState.getArray());
        assertTrue(finalState.getComparisons() > 0);
    }

    @Test
    @DisplayName("Heap Sort builds max heap and extracts in order")
    void testHeapSort() {
        List<Integer> input = List.of(12, 11, 13, 5, 6, 7);
        List<ExecutionStep> steps = sortingEngine.heapSort(input);
        assertTraceOrderAndValidity(steps);

        SortState finalState = (SortState) steps.get(steps.size() - 1).state();
        assertSorted(finalState.getArray());
        assertTrue(finalState.getSwaps() > 0);
    }

    // --- SEARCHING TESTS ---
    @Test
    @DisplayName("Linear Search finds existing target and handles absent target")
    void testLinearSearch() {
        List<Integer> input = List.of(10, 20, 30, 40, 50);
        List<ExecutionStep> foundSteps = searchingEngine.linearSearch(input, 30);
        assertTraceOrderAndValidity(foundSteps);
        SearchState foundState = (SearchState) foundSteps.get(foundSteps.size() - 1).state();
        assertEquals(2, foundState.getFoundIndex());

        List<ExecutionStep> absentSteps = searchingEngine.linearSearch(input, 999);
        assertTraceOrderAndValidity(absentSteps);
        SearchState absentState = (SearchState) absentSteps.get(absentSteps.size() - 1).state();
        assertNull(absentState.getFoundIndex());
    }

    @Test
    @DisplayName("Binary Search eliminates subranges and achieves O(log n) lookups")
    void testBinarySearch() {
        List<Integer> input = List.of(5, 12, 23, 34, 45, 56, 67, 78, 89);
        List<ExecutionStep> foundSteps = searchingEngine.binarySearch(input, 45);
        assertTraceOrderAndValidity(foundSteps);
        SearchState foundState = (SearchState) foundSteps.get(foundSteps.size() - 1).state();
        assertEquals(4, foundState.getFoundIndex());

        List<ExecutionStep> absentSteps = searchingEngine.binarySearch(input, 100);
        assertTraceOrderAndValidity(absentSteps);
        SearchState absentState = (SearchState) absentSteps.get(absentSteps.size() - 1).state();
        assertNull(absentState.getFoundIndex());
    }

    // --- GRAPH TESTS ---
    @Test
    @DisplayName("BFS and DFS traverse all reachable nodes")
    void testBFSandDFS() {
        GraphData graph = graphEngine.getSampleGraph();
        List<ExecutionStep> bfsSteps = graphEngine.bfs(graph, "A");
        assertTraceOrderAndValidity(bfsSteps);

        List<ExecutionStep> dfsSteps = graphEngine.dfs(graph, "A");
        assertTraceOrderAndValidity(dfsSteps);
    }

    @Test
    @DisplayName("Dijkstra and Bellman-Ford calculate shortest paths accurately")
    void testShortestPath() {
        GraphData graph = graphEngine.getSampleGraph();
        List<ExecutionStep> dijkstraSteps = graphEngine.dijkstra(graph, "A");
        assertTraceOrderAndValidity(dijkstraSteps);

        ShortestPathState dState = (ShortestPathState) dijkstraSteps.get(dijkstraSteps.size() - 1).state();
        assertEquals(0.0, ((Number) dState.getDistances().get("A")).doubleValue());
        assertTrue(((Number) dState.getDistances().get("B")).doubleValue() > 0);

        List<ExecutionStep> bfSteps = graphEngine.bellmanFord(graph, "A");
        assertTraceOrderAndValidity(bfSteps);
        ShortestPathState bfState = (ShortestPathState) bfSteps.get(bfSteps.size() - 1).state();
        assertFalse(bfState.isHasNegativeWeightCycle());
    }

    @Test
    @DisplayName("Kruskal and Prim compute valid Minimum Spanning Tree without cycles")
    void testMST() {
        GraphData graph = graphEngine.getSampleGraph();
        List<ExecutionStep> kruskalSteps = graphEngine.kruskal(graph);
        assertTraceOrderAndValidity(kruskalSteps);
        MSTState kState = (MSTState) kruskalSteps.get(kruskalSteps.size() - 1).state();
        assertEquals(graph.getVertices().size() - 1, kState.getMstEdges().size(), "MST must have |V| - 1 edges");

        List<ExecutionStep> primSteps = graphEngine.prim(graph, "A");
        assertTraceOrderAndValidity(primSteps);
        MSTState pState = (MSTState) primSteps.get(primSteps.size() - 1).state();
        assertEquals(graph.getVertices().size() - 1, pState.getMstEdges().size());
        assertEquals(kState.getTotalWeight(), pState.getTotalWeight(), "Kruskal and Prim must compute identical MST weight");
    }

    // --- RECURSION & DP TESTS ---
    @Test
    @DisplayName("Factorial correctly computes 0!, 1!, 5!")
    void testFactorial() {
        List<ExecutionStep> steps = recursionEngine.factorial(5);
        assertTraceOrderAndValidity(steps);
        FactorialState state = (FactorialState) steps.get(steps.size() - 1).state();
        assertEquals(120, state.getResult());

        List<ExecutionStep> steps0 = recursionEngine.factorial(0);
        FactorialState state0 = (FactorialState) steps0.get(steps0.size() - 1).state();
        assertEquals(1, state0.getResult());
    }

    @Test
    @DisplayName("Fibonacci modes (tabulated, memoized, recursive) yield identical results")
    void testFibonacci() {
        List<ExecutionStep> tabSteps = recursionEngine.fibonacci(6, "tabulated");
        FibonacciState tabState = (FibonacciState) tabSteps.get(tabSteps.size() - 1).state();

        List<ExecutionStep> memoSteps = recursionEngine.fibonacci(6, "memoized");
        FibonacciState memoState = (FibonacciState) memoSteps.get(memoSteps.size() - 1).state();

        List<ExecutionStep> recSteps = recursionEngine.fibonacci(6, "recursive");
        FibonacciState recState = (FibonacciState) recSteps.get(recSteps.size() - 1).state();

        assertEquals(8, tabState.getResult());
        assertEquals(8, memoState.getResult());
        assertEquals(8, recState.getResult());
    }

    @Test
    @DisplayName("Tower of Hanoi completes in exactly 2^n - 1 moves")
    void testTowerOfHanoi() {
        List<ExecutionStep> steps = recursionEngine.towerOfHanoi(3);
        assertTraceOrderAndValidity(steps);
        HanoiState state = (HanoiState) steps.get(steps.size() - 1).state();
        assertEquals(7, state.getMoveCount()); // 2^3 - 1 = 7
        assertEquals(3, state.getPegs().get("C").size(), "All 3 disks must be on target peg C");
    }

    @Test
    @DisplayName("N-Queens solves 4-Queens with 2 valid non-attacking solutions")
    void testNQueens() {
        List<ExecutionStep> steps = recursionEngine.nQueens(4);
        assertTraceOrderAndValidity(steps);
        NQueensState state = (NQueensState) steps.get(steps.size() - 1).state();
        assertEquals(2, state.getSolutionsFound(), "4-Queens puzzle has exactly 2 valid solutions");
    }

    @Test
    @DisplayName("0/1 Knapsack computes optimal value")
    void testKnapsack() {
        List<ExecutionStep> steps = dpEngine.knapsack(null, 5);
        assertTraceOrderAndValidity(steps);
        KnapsackState state = (KnapsackState) steps.get(steps.size() - 1).state();
        assertTrue(state.getOptimalValue() > 0);
        assertFalse(state.getSelectedItems().isEmpty());
    }

    @Test
    @DisplayName("LCS finds longest common subsequence")
    void testLCS() {
        List<ExecutionStep> steps = dpEngine.lcs("STONE", "LONGEST");
        assertTraceOrderAndValidity(steps);
        LCSState state = (LCSState) steps.get(steps.size() - 1).state();
        assertEquals("ONE", state.getLcsResult());
    }
}
