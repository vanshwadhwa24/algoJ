package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.LinearStructureModel.QueueDSState;
import com.algoj.model.LinearStructureModel.QueueSlot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueueStructureEngine {

    public QueueDSState createInitial(List<Integer> values, int capacity, boolean isCircular) {
        QueueDSState state = new QueueDSState();
        state.setCapacity(capacity);
        state.setCircular(isCircular);

        List<QueueSlot> slots = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            Integer val = i < values.size() ? values.get(i) : null;
            String slotState = i < values.size() ? "occupied" : "empty";
            slots.add(new QueueSlot(i, val, slotState));
        }
        state.setSlots(slots);
        state.setSize(values.size());
        state.setFront(values.isEmpty() ? -1 : 0);
        state.setRear(values.isEmpty() ? -1 : values.size() - 1);

        if (!values.isEmpty()) {
            state.getSlots().get(state.getFront()).setState("front");
            if (state.getRear() != state.getFront()) {
                state.getSlots().get(state.getRear()).setState("rear");
            }
        }
        state.setMessage("Queue initialized with capacity " + capacity + ".");
        return state;
    }

    public List<ExecutionStep> executeOperation(QueueDSState currentState, String type, Integer value) {
        List<ExecutionStep> steps = new ArrayList<>();
        QueueDSState state = currentState.copy();
        int val = value != null ? value : 48;

        if ("enqueue".equalsIgnoreCase(type)) {
            if (state.getSize() >= state.getCapacity()) {
                state.setMessage(String.format("Queue Overflow! Queue is full with %d elements.", state.getCapacity()));
                steps.add(new ExecutionStep(steps.size(), "overflow", state.getMessage(), 2, state.copy()));
                return steps;
            }

            int newRear;
            if (state.getSize() == 0) {
                state.setFront(0);
                newRear = 0;
            } else {
                newRear = (state.getRear() + 1) % state.getCapacity();
            }
            state.setRear(newRear);

            QueueSlot slot = state.getSlots().get(newRear);
            slot.setValue(val);
            slot.setState("enqueueing");
            state.setSize(state.getSize() + 1);

            state.setMessage(String.format("Enqueued %d at rear position %d.", val, newRear));
            steps.add(new ExecutionStep(steps.size(), "enqueue-prep", state.getMessage(), 4, state.copy()));

            updateSlotLabels(state);
            state.setMessage(String.format("Enqueued %d (FIFO order). Current size: %d/%d.", val, state.getSize(), state.getCapacity()));
            steps.add(new ExecutionStep(steps.size(), "enqueue", state.getMessage(), 5, state.copy()));

        } else if ("dequeue".equalsIgnoreCase(type)) {
            if (state.getSize() == 0) {
                state.setMessage("Queue Underflow! Cannot dequeue from empty queue.");
                steps.add(new ExecutionStep(steps.size(), "underflow", state.getMessage(), 2, state.copy()));
                return steps;
            }

            int oldFront = state.getFront();
            QueueSlot slot = state.getSlots().get(oldFront);
            int dequeuedVal = slot.getValue() != null ? slot.getValue() : 0;
            slot.setState("dequeueing");

            state.setMessage(String.format("Dequeuing element %d from front index %d.", dequeuedVal, oldFront));
            steps.add(new ExecutionStep(steps.size(), "dequeue-prep", state.getMessage(), 3, state.copy()));

            slot.setValue(null);
            slot.setState("empty");
            state.setSize(state.getSize() - 1);

            if (state.getSize() == 0) {
                state.setFront(-1);
                state.setRear(-1);
            } else {
                state.setFront((oldFront + 1) % state.getCapacity());
            }

            updateSlotLabels(state);
            state.setMessage(String.format("Dequeued %d in O(1) time. Front advanced to index %d.", dequeuedVal, state.getFront()));
            steps.add(new ExecutionStep(steps.size(), "dequeue", state.getMessage(), 5, state.copy()));

        } else if ("peekFront".equalsIgnoreCase(type)) {
            if (state.getSize() == 0) {
                state.setMessage("Queue is empty. Front is null.");
                steps.add(new ExecutionStep(steps.size(), "empty", state.getMessage(), 2, state.copy()));
                return steps;
            }

            int frontVal = state.getSlots().get(state.getFront()).getValue();
            state.setMessage(String.format("Peeked at front element: %d at index %d in O(1) time.", frontVal, state.getFront()));
            steps.add(new ExecutionStep(steps.size(), "peekFront", state.getMessage(), 3, state.copy()));

        } else if ("isEmpty".equalsIgnoreCase(type)) {
            boolean empty = state.getSize() == 0;
            state.setMessage(empty ? "Queue is EMPTY." : "Queue has " + state.getSize() + " elements.");
            steps.add(new ExecutionStep(steps.size(), "isEmpty", state.getMessage(), 2, state.copy()));
        }

        return steps;
    }

    private void updateSlotLabels(QueueDSState state) {
        for (int i = 0; i < state.getSlots().size(); i++) {
            QueueSlot s = state.getSlots().get(i);
            if (s.getValue() == null) {
                s.setState("empty");
            } else {
                s.setState("occupied");
            }
        }
        if (state.getSize() > 0 && state.getFront() >= 0 && state.getFront() < state.getSlots().size()) {
            state.getSlots().get(state.getFront()).setState("front");
        }
        if (state.getSize() > 0 && state.getRear() >= 0 && state.getRear() < state.getSlots().size() && state.getRear() != state.getFront()) {
            state.getSlots().get(state.getRear()).setState("rear");
        }
    }
}
