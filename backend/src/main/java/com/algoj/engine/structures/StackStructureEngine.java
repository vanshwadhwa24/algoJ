package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.LinearStructureModel.StackDSState;
import com.algoj.model.LinearStructureModel.StackItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StackStructureEngine {

    public StackDSState createInitial(List<Integer> values, int capacity) {
        StackDSState state = new StackDSState();
        state.setMaxCapacity(capacity);
        List<StackItem> items = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            String highlight = (i == values.size() - 1) ? "top" : "default";
            items.add(new StackItem("stack-" + i + "-" + values.get(i), values.get(i), highlight));
        }
        state.setItems(items);
        state.setMessage("Stack initialized with capacity " + capacity + ".");
        return state;
    }

    public List<ExecutionStep> executeOperation(StackDSState currentState, String type, Integer value) {
        List<ExecutionStep> steps = new ArrayList<>();
        StackDSState state = currentState.copy();
        int val = value != null ? value : 50;

        if ("push".equalsIgnoreCase(type)) {
            if (state.getItems().size() >= state.getMaxCapacity()) {
                state.setMessage(String.format("Stack Overflow! Maximum capacity %d reached.", state.getMaxCapacity()));
                steps.add(new ExecutionStep(steps.size(), "overflow", state.getMessage(), 2, state.copy()));
                return steps;
            }

            for (StackItem it : state.getItems()) it.setHighlight("default");

            StackItem newItem = new StackItem("stack-" + state.getItems().size() + "-" + val, val, "pushing");
            state.getItems().add(newItem);
            state.setMessage(String.format("Pushing %d onto top of stack (LIFO).", val));
            steps.add(new ExecutionStep(steps.size(), "push-prep", state.getMessage(), 3, state.copy()));

            newItem.setHighlight("top");
            state.setMessage(String.format("Pushed %d onto stack. Top pointer points to index %d.", val, state.getItems().size() - 1));
            steps.add(new ExecutionStep(steps.size(), "push", state.getMessage(), 4, state.copy()));

        } else if ("pop".equalsIgnoreCase(type)) {
            if (state.getItems().isEmpty()) {
                state.setMessage("Stack Underflow! Cannot pop from empty stack.");
                steps.add(new ExecutionStep(steps.size(), "underflow", state.getMessage(), 2, state.copy()));
                return steps;
            }

            int topIdx = state.getItems().size() - 1;
            StackItem popped = state.getItems().get(topIdx);
            popped.setHighlight("popping");
            state.setMessage(String.format("Popping top element %d from stack.", popped.getValue()));
            steps.add(new ExecutionStep(steps.size(), "pop-prep", state.getMessage(), 3, state.copy()));

            state.getItems().remove(topIdx);
            if (!state.getItems().isEmpty()) {
                state.getItems().get(state.getItems().size() - 1).setHighlight("top");
            }
            state.setMessage(String.format("Popped %d from stack in O(1) time.", popped.getValue()));
            steps.add(new ExecutionStep(steps.size(), "pop", state.getMessage(), 5, state.copy()));

        } else if ("peek".equalsIgnoreCase(type)) {
            if (state.getItems().isEmpty()) {
                state.setMessage("Stack is empty. Peek returns null.");
                steps.add(new ExecutionStep(steps.size(), "empty", state.getMessage(), 2, state.copy()));
                return steps;
            }

            int topIdx = state.getItems().size() - 1;
            StackItem top = state.getItems().get(topIdx);
            top.setHighlight("top");
            state.setMessage(String.format("Peeked at top of stack: %d in O(1) time (no element removed).", top.getValue()));
            steps.add(new ExecutionStep(steps.size(), "peek", state.getMessage(), 3, state.copy()));

        } else if ("isEmpty".equalsIgnoreCase(type)) {
            boolean empty = state.getItems().isEmpty();
            state.setMessage(empty ? "Stack is currently EMPTY (size 0)." : "Stack is NOT empty (size " + state.getItems().size() + ").");
            steps.add(new ExecutionStep(steps.size(), "isEmpty", state.getMessage(), 2, state.copy()));
        }

        return steps;
    }
}
