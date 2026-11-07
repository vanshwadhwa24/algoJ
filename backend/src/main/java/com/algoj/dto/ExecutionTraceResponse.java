package com.algoj.dto;

import java.util.List;
import java.util.Map;

public record ExecutionTraceResponse(
        String algorithmId,
        int totalSteps,
        List<ExecutionStep> steps,
        Map<String, Object> summary) {

    public ExecutionTraceResponse {
        totalSteps = steps != null ? steps.size() : 0;
        if (steps != null) {
            int total = totalSteps;
            steps = steps.stream().map(s -> s.withTotalSteps(total)).toList();
        }
    }

    public ExecutionTraceResponse(String algorithmId, List<ExecutionStep> steps) {
        this(algorithmId, 0, steps, null);
    }

    public ExecutionTraceResponse(String algorithmId, List<ExecutionStep> steps, Map<String, Object> summary) {
        this(algorithmId, 0, steps, summary);
    }
}
