package com.algoj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExecutionStep(
        int stepIndex,
        Integer totalSteps,
        String operationType,
        String description,
        int codeLine,
        Object state,
        Map<String, Integer> counters,
        Map<String, Object> metadata) {

    public ExecutionStep(int stepIndex, String operationType, String description, int codeLine, Object state) {
        this(stepIndex, null, operationType, description, codeLine, state, null, null);
    }

    public ExecutionStep(int stepIndex, String operationType, String description, int codeLine, Object state,
                         Map<String, Integer> counters, Map<String, Object> metadata) {
        this(stepIndex, null, operationType, description, codeLine, state, counters, metadata);
    }

    public ExecutionStep withTotalSteps(Integer totalSteps) {
        return new ExecutionStep(stepIndex, totalSteps, operationType, description, codeLine, state, counters, metadata);
    }
}
