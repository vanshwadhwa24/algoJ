package com.algoj.algorithm.recursion;

import com.algoj.algorithm.AlgorithmInput;
import com.algoj.algorithm.BaseAlgorithm;
import com.algoj.dto.ExecutionStep;
import com.algoj.model.RecursionAndDPModel.*;
import java.util.*;

public class Factorial extends BaseAlgorithm {

    public Factorial() {
        super("factorial");
    }

    @Override
    public List<ExecutionStep> run(AlgorithmInput in) {
        return factorial(in.n() != null ? in.n() : 4);
    }

    private List<ExecutionStep> factorial(int n) {
        List<ExecutionStep> steps = new ArrayList<>();
        int clampedN = Math.max(0, Math.min(7, n));

        FactorialState state = new FactorialState();
        state.setN(clampedN);
        state.setMessage(String.format("Computing Factorial(%d).", clampedN));

        steps.add(new ExecutionStep(
                steps.size(), "init",
                String.format("Calling Factorial(%d). Base case is n <= 1.", clampedN),
                1, state.copy(), Map.of("callStackDepth", 0), null
        ));

        List<StackFrame> callStack = new ArrayList<>();
        int res = factHelper(clampedN, callStack, state, steps);
        state.setResult(res);

        steps.add(new ExecutionStep(
                steps.size(), "complete",
                String.format("Factorial(%d) = %d completed. Call stack unwound completely.", clampedN, res),
                6, state.copy(), Map.of("callStackDepth", 0), null
        ));

        return steps;
    }

    private int factHelper(int val, List<StackFrame> callStack, FactorialState state, List<ExecutionStep> steps) {
        StackFrame frame = new StackFrame("frame-" + val, "fact(" + val + ")", val, val <= 1 ? "base-case" : "calling");
        callStack.add(frame);
        state.setCallStack(new ArrayList<>(callStack));

        steps.add(new ExecutionStep(
                steps.size(), val <= 1 ? "base-case" : "call",
                val <= 1 ? String.format("Reached base case: fact(%d) returns 1 immediately.", val)
                         : String.format("Pushed fact(%d) onto call stack. Awaiting fact(%d).", val, val - 1),
                val <= 1 ? 2 : 4, state.copy(), Map.of("callStackDepth", callStack.size()), null
        ));

        if (val <= 1) {
            frame.setReturnValue(1);
            frame.setState("returned");
            return 1;
        }

        int sub = factHelper(val - 1, callStack, state, steps);
        int total = val * sub;
        frame.setReturnValue(total);
        frame.setState("returned");

        steps.add(new ExecutionStep(
                steps.size(), "return-multiply",
                String.format("fact(%d) computes %d * fact(%d) (%d) = %d. Returning %d.", val, val, val - 1, sub, total, total),
                5, state.copy(), Map.of("callStackDepth", callStack.size()), null
        ));

        callStack.remove(callStack.size() - 1);
        state.setCallStack(new ArrayList<>(callStack));
        return total;
    }
}
