package com.algoj.structure;

import com.algoj.dto.ExecutionStep;
import java.util.List;

/** One interactive data structure: its starting state and how an operation turns into execution steps. */
public interface StructureEngine<S> {
    String id();

    S initialState();

    List<ExecutionStep> apply(S state, Operation op);
}
