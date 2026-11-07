package com.algoj;

import com.algoj.dto.AlgorithmRunRequest;
import com.algoj.dto.DataStructureOpRequest;
import com.algoj.dto.ExecutionTraceResponse;
import com.algoj.engine.dp.DPAlgorithmEngine;
import com.algoj.engine.graphs.GraphAlgorithmEngine;
import com.algoj.engine.recursion.RecursionAlgorithmEngine;
import com.algoj.engine.searching.SearchingEngine;
import com.algoj.engine.sorting.SortingEngine;
import com.algoj.engine.structures.*;
import com.algoj.service.AlgorithmExecutionService;
import com.algoj.service.DataStructureExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceLayerTest {

    private AlgorithmExecutionService algorithmService;
    private DataStructureExecutionService dataStructureService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        algorithmService = new AlgorithmExecutionService(
                new SortingEngine(),
                new SearchingEngine(),
                new GraphAlgorithmEngine(),
                new RecursionAlgorithmEngine(),
                new DPAlgorithmEngine(),
                objectMapper
        );

        dataStructureService = new DataStructureExecutionService(
                new ArrayStructureEngine(),
                new LinkedListStructureEngine(),
                new StackStructureEngine(),
                new QueueStructureEngine(),
                new HashTableStructureEngine(),
                new BSTStructureEngine(),
                new AVLStructureEngine(),
                new HeapStructureEngine(),
                new TrieStructureEngine(),
                objectMapper
        );
    }

    @Test
    @DisplayName("AlgorithmExecutionService executes algorithms with default fallbacks")
    void testAlgorithmExecutionService() {
        AlgorithmRunRequest req = new AlgorithmRunRequest(
                "quick-sort", List.of(45, 10, 89, 2), null, null, null, null, null, null, null, null);

        ExecutionTraceResponse res = algorithmService.runAlgorithm(req);
        assertNotNull(res);
        assertEquals("quick-sort", res.algorithmId());
        assertTrue(res.totalSteps() > 0);
        assertFalse(res.steps().isEmpty());
    }

    @Test
    @DisplayName("AlgorithmExecutionService rejects invalid or missing algorithmId")
    void testAlgorithmExecutionServiceValidation() {
        AlgorithmRunRequest req = new AlgorithmRunRequest(
                "unknown-algo", null, null, null, null, null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> algorithmService.runAlgorithm(req));
        assertThrows(IllegalArgumentException.class, () -> algorithmService.runAlgorithm(null));
    }

    @Test
    @DisplayName("DataStructureExecutionService operates structures and produces valid traces")
    void testDataStructureExecutionService() {
        DataStructureOpRequest req = new DataStructureOpRequest(
                "arrays", "access", null, 0, null, null, null, null, null);

        ExecutionTraceResponse res = dataStructureService.operate(req);
        assertNotNull(res);
        assertEquals("arrays", res.algorithmId());
        assertFalse(res.steps().isEmpty());

        Object initial = dataStructureService.getInitialState("linked-lists");
        assertNotNull(initial);
    }
}
