package com.algoj.controller;

import com.algoj.dto.AlgorithmRunRequest;
import com.algoj.dto.ExecutionTraceResponse;
import com.algoj.model.GraphModel.GraphData;
import com.algoj.service.AlgorithmExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for executing algorithms and fetching algorithm sample datasets.
 * Delegates execution orchestration to AlgorithmExecutionService.
 */
@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    private final AlgorithmExecutionService algorithmService;

    public AlgorithmController(AlgorithmExecutionService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PostMapping("/run")
    public ResponseEntity<ExecutionTraceResponse> runAlgorithm(@RequestBody AlgorithmRunRequest request) {
        return ResponseEntity.ok(algorithmService.runAlgorithm(request));
    }

    @GetMapping("/sample-graph")
    public ResponseEntity<GraphData> getSampleGraph() {
        return ResponseEntity.ok(algorithmService.getSampleGraph());
    }
}
