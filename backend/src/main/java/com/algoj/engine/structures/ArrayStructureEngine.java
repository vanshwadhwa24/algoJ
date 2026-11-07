package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.LinearStructureModel.ArrayCell;
import com.algoj.model.LinearStructureModel.ArrayDSState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArrayStructureEngine {

    public ArrayDSState createInitial(List<Integer> values) {
        ArrayDSState state = new ArrayDSState();
        List<ArrayCell> cells = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            cells.add(new ArrayCell(i, values.get(i), "default"));
        }
        state.setCells(cells);
        state.setActiveIndex(null);
        state.setOperationCount(0);
        state.setMessage("Array initialized with " + values.size() + " elements.");
        return state;
    }

    public List<ExecutionStep> executeOperation(ArrayDSState currentState, String type, Integer index, Integer value) {
        List<ExecutionStep> steps = new ArrayList<>();
        ArrayDSState state = currentState.copy();

        if ("access".equalsIgnoreCase(type)) {
            int idx = index != null ? index : 0;
            if (idx < 0 || idx >= state.getCells().size()) {
                state.setMessage(String.format("Index %d out of bounds! Range: 0 to %d.", idx, state.getCells().size() - 1));
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 2, state.copy()));
                return steps;
            }
            state.setActiveIndex(idx);
            state.getCells().get(idx).setState("active");
            state.setOperationCount(state.getOperationCount() + 1);
            state.setMessage(String.format("Accessed element at index %d: %d in O(1) time.", idx, state.getCells().get(idx).getValue()));

            steps.add(new ExecutionStep(steps.size(), "access", state.getMessage(), 3, state.copy()));

        } else if ("update".equalsIgnoreCase(type)) {
            int idx = index != null ? index : 0;
            int val = value != null ? value : 99;
            if (idx < 0 || idx >= state.getCells().size()) {
                state.setMessage(String.format("Index %d out of bounds! Range: 0 to %d.", idx, state.getCells().size() - 1));
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 2, state.copy()));
                return steps;
            }
            state.setActiveIndex(idx);
            int oldVal = state.getCells().get(idx).getValue();
            state.getCells().get(idx).setValue(val);
            state.getCells().get(idx).setState("active");
            state.setOperationCount(state.getOperationCount() + 1);
            state.setMessage(String.format("Updated index %d: changed %d -> %d in O(1) time.", idx, oldVal, val));

            steps.add(new ExecutionStep(steps.size(), "update", state.getMessage(), 3, state.copy()));

        } else if ("insert".equalsIgnoreCase(type)) {
            int idx = index != null ? Math.max(0, Math.min(state.getCells().size(), index)) : state.getCells().size();
            int val = value != null ? value : 50;

            // Shift right steps
            for (int i = state.getCells().size() - 1; i >= idx; i--) {
                state.getCells().get(i).setState("shifted");
                steps.add(new ExecutionStep(
                        steps.size(), "shift",
                        String.format("Shifting element %d from index %d to %d to make room.",
                                state.getCells().get(i).getValue(), i, i + 1),
                        4, state.copy()
                ));
            }

            ArrayCell newCell = new ArrayCell(idx, val, "inserted");
            state.getCells().add(idx, newCell);
            for (int i = 0; i < state.getCells().size(); i++) {
                state.getCells().get(i).setIndex(i);
            }
            state.setActiveIndex(idx);
            state.setOperationCount(state.getOperationCount() + 1);
            state.setMessage(String.format("Inserted value %d at index %d (Required %d shifts).",
                    val, idx, state.getCells().size() - 1 - idx));

            steps.add(new ExecutionStep(steps.size(), "inserted", state.getMessage(), 6, state.copy()));

        } else if ("delete".equalsIgnoreCase(type)) {
            int idx = index != null ? index : 0;
            if (idx < 0 || idx >= state.getCells().size()) {
                state.setMessage(String.format("Index %d out of bounds! Range: 0 to %d.", idx, state.getCells().size() - 1));
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 2, state.copy()));
                return steps;
            }

            int deletedVal = state.getCells().get(idx).getValue();
            state.getCells().get(idx).setState("deleted");
            steps.add(new ExecutionStep(
                    steps.size(), "delete-target",
                    String.format("Targeted element %d at index %d for deletion.", deletedVal, idx),
                    3, state.copy()
            ));

            state.getCells().remove(idx);
            for (int i = 0; i < state.getCells().size(); i++) {
                state.getCells().get(i).setIndex(i);
                if (i >= idx) state.getCells().get(i).setState("shifted");
            }
            state.setActiveIndex(null);
            state.setOperationCount(state.getOperationCount() + 1);
            state.setMessage(String.format("Deleted element %d at index %d. Shifted subsequent elements left.", deletedVal, idx));

            steps.add(new ExecutionStep(steps.size(), "deleted", state.getMessage(), 5, state.copy()));

        } else if ("search".equalsIgnoreCase(type)) {
            int val = value != null ? value : 40;
            boolean found = false;

            for (int i = 0; i < state.getCells().size(); i++) {
                state.setActiveIndex(i);
                state.getCells().get(i).setState("active");
                int currentVal = state.getCells().get(i).getValue();

                steps.add(new ExecutionStep(
                        steps.size(), "search-step",
                        String.format("Inspecting index %d (value = %d). Looking for %d.", i, currentVal, val),
                        3, state.copy()
                ));

                if (currentVal == val) {
                    found = true;
                    state.setMessage(String.format("Value %d found at index %d!", val, i));
                    steps.add(new ExecutionStep(steps.size(), "found", state.getMessage(), 4, state.copy()));
                    break;
                }
                state.getCells().get(i).setState("default");
            }

            if (!found) {
                state.setActiveIndex(null);
                state.setMessage(String.format("Value %d not found in array after full scan.", val));
                steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 6, state.copy()));
            }

        } else if ("traverse".equalsIgnoreCase(type)) {
            for (int i = 0; i < state.getCells().size(); i++) {
                state.setActiveIndex(i);
                state.getCells().get(i).setState("active");
                state.setMessage(String.format("Traversing index %d: value is %d.", i, state.getCells().get(i).getValue()));

                steps.add(new ExecutionStep(steps.size(), "traverse", state.getMessage(), 3, state.copy()));
                state.getCells().get(i).setState("default");
            }
            state.setActiveIndex(null);
            state.setMessage("Traversal complete across all array indices.");
            steps.add(new ExecutionStep(steps.size(), "complete", state.getMessage(), 5, state.copy()));
        }

        // Reset highlights for final state
        for (ArrayCell cell : state.getCells()) {
            cell.setState("default");
        }
        if (state.getActiveIndex() != null && state.getActiveIndex() < state.getCells().size()) {
            state.getCells().get(state.getActiveIndex()).setState("active");
        }

        return steps;
    }
}
