package com.algoj.service;

import com.algoj.dto.AlgorithmRunRequest;
import com.algoj.dto.ExecutionStep;
import com.algoj.dto.ExecutionTraceResponse;
import com.algoj.engine.dp.DPAlgorithmEngine;
import com.algoj.engine.graphs.GraphAlgorithmEngine;
import com.algoj.engine.recursion.RecursionAlgorithmEngine;
import com.algoj.engine.searching.SearchingEngine;
import com.algoj.engine.sorting.SortingEngine;
import com.algoj.model.GraphModel.GraphData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service orchestrating authoritative execution of algorithms in Java.
 * Handles validation, default parameter resolution, model deserialization,
 * and execution trace creation.
 */
@Service
public class AlgorithmExecutionService {

    private final SortingEngine sortingEngine;
    private final SearchingEngine searchingEngine;
    private final GraphAlgorithmEngine graphEngine;
    private final RecursionAlgorithmEngine recursionEngine;
    private final DPAlgorithmEngine dpEngine;
    private final ObjectMapper objectMapper;

    public AlgorithmExecutionService(SortingEngine sortingEngine,
                                     SearchingEngine searchingEngine,
                                     GraphAlgorithmEngine graphEngine,
                                     RecursionAlgorithmEngine recursionEngine,
                                     DPAlgorithmEngine dpEngine,
                                     ObjectMapper objectMapper) {
        this.sortingEngine = sortingEngine;
        this.searchingEngine = searchingEngine;
        this.graphEngine = graphEngine;
        this.recursionEngine = recursionEngine;
        this.dpEngine = dpEngine;
        this.objectMapper = objectMapper;
    }

    public ExecutionTraceResponse runAlgorithm(AlgorithmRunRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AlgorithmRunRequest must not be null.");
        }

        String algoId = request.algorithmId();
        if (algoId == null || algoId.trim().isEmpty()) {
            throw new IllegalArgumentException("algorithmId must not be null or empty.");
        }

        List<Integer> array = request.array();
        if (array == null || array.isEmpty()) {
            array = List.of(35, 12, 48, 22, 60, 5);
        }

        List<ExecutionStep> steps;

        switch (algoId) {
            // Sorting
            case "bubble-sort":
                steps = sortingEngine.bubbleSort(array);
                break;
            case "selection-sort":
                steps = sortingEngine.selectionSort(array);
                break;
            case "insertion-sort":
                steps = sortingEngine.insertionSort(array);
                break;
            case "merge-sort":
                steps = sortingEngine.mergeSort(array);
                break;
            case "quick-sort":
                steps = sortingEngine.quickSort(array);
                break;
            case "heap-sort":
                steps = sortingEngine.heapSort(array);
                break;

            // Searching
            case "linear-search":
                steps = searchingEngine.linearSearch(array, request.target() != null ? request.target() : 22);
                break;
            case "binary-search":
                steps = searchingEngine.binarySearch(array, request.target() != null ? request.target() : 22);
                break;

            // Graphs
            case "bfs":
                steps = graphEngine.bfs(parseGraph(request.graph()), request.target() != null ? String.valueOf(request.target()) : null);
                break;
            case "dfs":
                steps = graphEngine.dfs(parseGraph(request.graph()), request.target() != null ? String.valueOf(request.target()) : null);
                break;
            case "dijkstra":
                steps = graphEngine.dijkstra(parseGraph(request.graph()), request.target() != null ? String.valueOf(request.target()) : null);
                break;
            case "bellman-ford":
                steps = graphEngine.bellmanFord(parseGraph(request.graph()), request.target() != null ? String.valueOf(request.target()) : null);
                break;
            case "floyd-warshall":
                steps = graphEngine.floydWarshall(parseGraph(request.graph()));
                break;
            case "kruskal":
                steps = graphEngine.kruskal(parseGraph(request.graph()));
                break;
            case "prim":
                steps = graphEngine.prim(parseGraph(request.graph()), request.target() != null ? String.valueOf(request.target()) : null);
                break;

            // Recursion & Backtracking
            case "factorial":
                steps = recursionEngine.factorial(request.n() != null ? request.n() : 4);
                break;
            case "fibonacci":
                steps = recursionEngine.fibonacci(request.n() != null ? request.n() : 6, request.mode() != null ? request.mode() : "tabulated");
                break;
            case "tower-of-hanoi":
                steps = recursionEngine.towerOfHanoi(request.disks() != null ? request.disks() : 3);
                break;
            case "n-queens":
                steps = recursionEngine.nQueens(request.n() != null ? request.n() : 4);
                break;

            // Dynamic Programming
            case "knapsack":
                steps = dpEngine.knapsack(null, request.n() != null ? request.n() : 5);
                break;
            case "lcs":
                steps = dpEngine.lcs(
                        request.text1() != null ? request.text1() : "STONE",
                        request.text2() != null ? request.text2() : "LONGEST"
                );
                break;

            default:
                throw new IllegalArgumentException("Unsupported algorithm ID: " + algoId);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("algorithmId", algoId);
        summary.put("totalSteps", steps.size());
        summary.put("completedSuccessfully", true);

        return new ExecutionTraceResponse(algoId, steps, summary);
    }

    public GraphData getSampleGraph() {
        return graphEngine.getSampleGraph();
    }

    private GraphData parseGraph(Map<String, Object> raw) {
        if (raw == null) return graphEngine.getSampleGraph();
        try {
            return objectMapper.convertValue(raw, GraphData.class);
        } catch (Exception e) {
            return graphEngine.getSampleGraph();
        }
    }
}
