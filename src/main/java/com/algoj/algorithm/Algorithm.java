package com.algoj.algorithm;

import com.algoj.dto.ExecutionStep;
import java.util.List;

/** A runnable algorithm that records every step of its execution as an ExecutionStep trace. */
public interface Algorithm {
    String id();

    List<ExecutionStep> run(AlgorithmInput input);
}
