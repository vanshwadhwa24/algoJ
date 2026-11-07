package com.algoj.controller;

import com.algoj.dto.DataStructureOpRequest;
import com.algoj.dto.ExecutionTraceResponse;
import com.algoj.service.DataStructureExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for executing interactive data structure operations.
 * Delegates execution orchestration to DataStructureExecutionService.
 */
@RestController
@RequestMapping("/api/structures")
public class DataStructureController {

    private final DataStructureExecutionService dataStructureService;

    public DataStructureController(DataStructureExecutionService dataStructureService) {
        this.dataStructureService = dataStructureService;
    }

    @PostMapping("/operate")
    public ResponseEntity<ExecutionTraceResponse> operate(@RequestBody DataStructureOpRequest request) {
        return ResponseEntity.ok(dataStructureService.operate(request));
    }

    @GetMapping("/initial/{structureId}")
    public ResponseEntity<Object> getInitial(@PathVariable String structureId) {
        return ResponseEntity.ok(dataStructureService.getInitialState(structureId));
    }
}
