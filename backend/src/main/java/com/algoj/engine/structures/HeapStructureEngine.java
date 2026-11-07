package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.TreeStructureModel.HeapDSState;
import com.algoj.model.TreeStructureModel.HeapElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HeapStructureEngine {

    public HeapDSState createInitial(String type, List<Integer> values) {
        HeapDSState state = new HeapDSState();
        state.setType("min".equalsIgnoreCase(type) ? "min" : "max");
        List<HeapElement> arr = new ArrayList<>();
        for (int v : values) {
            arr.add(new HeapElement(v, "default"));
        }
        state.setArray(arr);
        state.setMessage(String.format("%s Heap initialized with %d elements.",
                state.getType().toUpperCase(), values.size()));
        return state;
    }

    public List<ExecutionStep> executeOperation(HeapDSState currentState, String operationType, Integer value) {
        List<ExecutionStep> steps = new ArrayList<>();
        HeapDSState state = currentState.copy();
        boolean isMax = !"min".equalsIgnoreCase(state.getType());
        int val = value != null ? value : 95;

        if ("insert".equalsIgnoreCase(operationType)) {
            HeapElement newElem = new HeapElement(val, "placed");
            state.getArray().add(newElem);
            int idx = state.getArray().size() - 1;
            state.setActiveIndices(List.of(idx));

            steps.add(new ExecutionStep(
                    steps.size(), "insert-append",
                    String.format("Appended %d at the end of the heap array (index %d).", val, idx),
                    2, state.copy()
            ));

            // Sift-up (Bubble-up)
            while (idx > 0) {
                int parentIdx = (idx - 1) / 2;
                state.setComparingIndices(List.of(idx, parentIdx));
                state.getArray().get(idx).setState("comparing");
                state.getArray().get(parentIdx).setState("comparing");

                int childVal = state.getArray().get(idx).getValue();
                int parentVal = state.getArray().get(parentIdx).getValue();

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing child %d (idx %d) with parent %d (idx %d).",
                                childVal, idx, parentVal, parentIdx),
                        3, state.copy()
                ));

                boolean condition = isMax ? (childVal > parentVal) : (childVal < parentVal);
                if (condition) {
                    state.getArray().get(idx).setState("swapping");
                    state.getArray().get(parentIdx).setState("swapping");

                    HeapElement temp = state.getArray().get(idx);
                    state.getArray().set(idx, state.getArray().get(parentIdx));
                    state.getArray().set(parentIdx, temp);

                    steps.add(new ExecutionStep(
                            steps.size(), "sift-up-swap",
                            String.format("Heap property violated. Swapped %d with parent %d.", childVal, parentVal),
                            4, state.copy()
                    ));

                    state.getArray().get(idx).setState("default");
                    state.getArray().get(parentIdx).setState("default");
                    idx = parentIdx;
                    state.setActiveIndices(List.of(idx));
                } else {
                    state.getArray().get(idx).setState("default");
                    state.getArray().get(parentIdx).setState("default");
                    break;
                }
            }

            state.setActiveIndices(Collections.emptyList());
            state.setComparingIndices(Collections.emptyList());
            state.setMessage(String.format("Inserted %d into heap. Heap invariant satisfied.", val));
            steps.add(new ExecutionStep(steps.size(), "insert-complete", state.getMessage(), 6, state.copy()));

        } else if ("extract".equalsIgnoreCase(operationType)) {
            if (state.getArray().isEmpty()) {
                state.setMessage("Cannot extract: Heap is empty!");
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 1, state.copy()));
                return steps;
            }

            int rootVal = state.getArray().get(0).getValue();
            state.getArray().get(0).setState("violation");

            steps.add(new ExecutionStep(
                    steps.size(), "extract-root",
                    String.format("Targeted root %d (extreme element) for extraction.", rootVal),
                    2, state.copy()
            ));

            if (state.getArray().size() == 1) {
                state.getArray().clear();
                state.setMessage(String.format("Extracted root %d. Heap is now empty.", rootVal));
                steps.add(new ExecutionStep(steps.size(), "extract-complete", state.getMessage(), 3, state.copy()));
                return steps;
            }

            HeapElement last = state.getArray().remove(state.getArray().size() - 1);
            state.getArray().set(0, last);
            state.getArray().get(0).setState("placed");

            steps.add(new ExecutionStep(
                    steps.size(), "replace-root",
                    String.format("Moved last leaf %d to root index 0. Beginning sift-down.", last.getValue()),
                    4, state.copy()
            ));

            // Sift-down (Heapify)
            int idx = 0;
            int n = state.getArray().size();

            while (true) {
                int left = 2 * idx + 1;
                int right = 2 * idx + 2;
                int target = idx;

                if (left < n) {
                    int lVal = state.getArray().get(left).getValue();
                    int tVal = state.getArray().get(target).getValue();
                    if (isMax ? (lVal > tVal) : (lVal < tVal)) {
                        target = left;
                    }
                }
                if (right < n) {
                    int rVal = state.getArray().get(right).getValue();
                    int tVal = state.getArray().get(target).getValue();
                    if (isMax ? (rVal > tVal) : (rVal < tVal)) {
                        target = right;
                    }
                }

                if (target != idx) {
                    state.setComparingIndices(List.of(idx, target));
                    state.getArray().get(idx).setState("swapping");
                    state.getArray().get(target).setState("swapping");

                    HeapElement temp = state.getArray().get(idx);
                    state.getArray().set(idx, state.getArray().get(target));
                    state.getArray().set(target, temp);

                    steps.add(new ExecutionStep(
                            steps.size(), "sift-down-swap",
                            String.format("Sift-down: Swapped %d with %s child %d.",
                                    temp.getValue(), isMax ? "larger" : "smaller", state.getArray().get(idx).getValue()),
                            5, state.copy()
                    ));

                    state.getArray().get(idx).setState("default");
                    state.getArray().get(target).setState("default");
                    idx = target;
                } else {
                    break;
                }
            }

            state.setActiveIndices(Collections.emptyList());
            state.setComparingIndices(Collections.emptyList());
            state.setMessage(String.format("Extracted root %d. Heapify complete.", rootVal));
            steps.add(new ExecutionStep(steps.size(), "extract-complete", state.getMessage(), 7, state.copy()));

        } else if ("peek".equalsIgnoreCase(operationType)) {
            if (state.getArray().isEmpty()) {
                state.setMessage("Heap is empty.");
                steps.add(new ExecutionStep(steps.size(), "empty", state.getMessage(), 1, state.copy()));
            } else {
                state.getArray().get(0).setState("placed");
                state.setMessage(String.format("Peeked at root: %d in O(1) time.", state.getArray().get(0).getValue()));
                steps.add(new ExecutionStep(steps.size(), "peek", state.getMessage(), 2, state.copy()));
                state.getArray().get(0).setState("default");
            }
        }

        for (HeapElement el : state.getArray()) el.setState("default");
        return steps;
    }
}
