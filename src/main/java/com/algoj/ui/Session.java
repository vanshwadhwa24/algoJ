package com.algoj.ui;

import com.algoj.algorithm.Algorithm;
import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.Algorithms;
import com.algoj.catalog.AlgorithmInfo;
import com.algoj.dto.ExecutionStep;
import com.algoj.structure.Operation;
import com.algoj.structure.StructureEngine;
import com.algoj.structure.Structures;

import java.util.List;

/**
 * One visualization session for an algorithm or structure. Algorithms re-run from an input; structures keep their
 * state between operations, so each operation's trace starts where the previous one ended.
 */
public class Session {
    private final AlgorithmInfo info;
    private StructureEngine<Object> engine;
    private Object structureState;

    @SuppressWarnings("unchecked")
    public Session(AlgorithmInfo info) {
        this.info = info;
        if (Structures.exists(info.id())) {
            engine = (StructureEngine<Object>) Structures.get(info.id());
            structureState = engine.initialState();
        }
    }

    public boolean isStructure() {
        return engine != null;
    }

    public List<ExecutionStep> run(AlgorithmInput input) {
        Algorithm a = Algorithms.get(info.id());
        return a.run(input);
    }

    /** The structure's resting state, shown as a one-step trace before any operation. */
    public List<ExecutionStep> idle() {
        return List.of(new ExecutionStep(0, "init", "Ready. Pick an operation.", 0, structureState));
    }

    public List<ExecutionStep> apply(Operation op) {
        List<ExecutionStep> steps = engine.apply(structureState, op);
        if (!steps.isEmpty()) structureState = steps.get(steps.size() - 1).state();
        return steps;
    }

    public List<ExecutionStep> reset() {
        structureState = engine.initialState();
        return idle();
    }
}
