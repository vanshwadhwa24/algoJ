package com.algoj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionStep {
    private int stepIndex;
    private Integer totalSteps;
    private String operationType;
    private String description;
    private int codeLine;
    private Object state;
    private Map<String, Integer> counters;
    private Map<String, Object> metadata;

    public ExecutionStep() {}

    public ExecutionStep(int stepIndex, String operationType, String description, int codeLine, Object state) {
        this.stepIndex = stepIndex;
        this.operationType = operationType;
        this.description = description;
        this.codeLine = codeLine;
        this.state = state;
    }

    public ExecutionStep(int stepIndex, String operationType, String description, int codeLine, Object state,
                         Map<String, Integer> counters, Map<String, Object> metadata) {
        this.stepIndex = stepIndex;
        this.operationType = operationType;
        this.description = description;
        this.codeLine = codeLine;
        this.state = state;
        this.counters = counters;
        this.metadata = metadata;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCodeLine() {
        return codeLine;
    }

    public void setCodeLine(int codeLine) {
        this.codeLine = codeLine;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Map<String, Integer> getCounters() {
        return counters;
    }

    public void setCounters(Map<String, Integer> counters) {
        this.counters = counters;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
