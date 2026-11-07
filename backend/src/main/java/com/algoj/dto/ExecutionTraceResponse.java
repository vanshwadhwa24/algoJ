package com.algoj.dto;

import java.util.List;
import java.util.Map;

public class ExecutionTraceResponse {
    private String algorithmId;
    private int totalSteps;
    private List<ExecutionStep> steps;
    private Map<String, Object> summary;

    public ExecutionTraceResponse() {}

    public ExecutionTraceResponse(String algorithmId, List<ExecutionStep> steps) {
        this.algorithmId = algorithmId;
        this.steps = steps;
        this.totalSteps = steps != null ? steps.size() : 0;
        if (steps != null) {
            for (ExecutionStep s : steps) {
                s.setTotalSteps(this.totalSteps);
            }
        }
    }

    public ExecutionTraceResponse(String algorithmId, List<ExecutionStep> steps, Map<String, Object> summary) {
        this(algorithmId, steps);
        this.summary = summary;
    }

    public String getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(String algorithmId) {
        this.algorithmId = algorithmId;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ExecutionStep> steps) {
        this.steps = steps;
        this.totalSteps = steps != null ? steps.size() : 0;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }
}
