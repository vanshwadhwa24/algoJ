package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.LinearStructureModel.LinkedListDSState;
import com.algoj.model.LinearStructureModel.ListNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LinkedListStructureEngine {

    public LinkedListDSState createInitial(List<Integer> values, boolean isDoubly) {
        LinkedListDSState state = new LinkedListDSState();
        state.setDoubly(isDoubly);
        List<ListNode> nodes = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            nodes.add(new ListNode("node-" + i + "-" + values.get(i), values.get(i), "default"));
        }
        state.setNodes(nodes);
        state.setActiveNodeId(null);
        state.setMessage((isDoubly ? "Doubly" : "Singly") + " Linked List initialized with " + values.size() + " nodes.");
        return state;
    }

    public List<ExecutionStep> executeOperation(LinkedListDSState currentState, String type, Integer value, Integer position) {
        List<ExecutionStep> steps = new ArrayList<>();
        LinkedListDSState state = currentState.copy();
        int val = value != null ? value : 99;
        int pos = position != null ? position : 0;

        if ("insertHead".equalsIgnoreCase(type)) {
            ListNode newNode = new ListNode("node-head-" + val + "-" + System.currentTimeMillis(), val, "new");
            steps.add(new ExecutionStep(
                    steps.size(), "alloc",
                    String.format("Allocated new node with value %d. Next pointer set to current head.", val),
                    2, state.copy()
            ));

            state.getNodes().add(0, newNode);
            state.setActiveNodeId(newNode.getId());
            state.setMessage(String.format("Inserted %d at head in O(1) time.", val));
            steps.add(new ExecutionStep(steps.size(), "insertHead", state.getMessage(), 4, state.copy()));

        } else if ("insertTail".equalsIgnoreCase(type)) {
            ListNode newNode = new ListNode("node-tail-" + val + "-" + System.currentTimeMillis(), val, "new");
            for (int i = 0; i < state.getNodes().size(); i++) {
                state.getNodes().get(i).setHighlight("active");
                state.setActiveNodeId(state.getNodes().get(i).getId());
                steps.add(new ExecutionStep(
                        steps.size(), "traverse",
                        String.format("Traversing towards tail. Current node: %d.", state.getNodes().get(i).getValue()),
                        2, state.copy()
                ));
                state.getNodes().get(i).setHighlight("default");
            }

            state.getNodes().add(newNode);
            state.setActiveNodeId(newNode.getId());
            state.setMessage(String.format("Linked new node %d to tail.", val));
            steps.add(new ExecutionStep(steps.size(), "insertTail", state.getMessage(), 4, state.copy()));

        } else if ("insertAt".equalsIgnoreCase(type)) {
            int targetPos = Math.max(0, Math.min(state.getNodes().size(), pos));
            ListNode newNode = new ListNode("node-pos-" + val + "-" + System.currentTimeMillis(), val, "new");

            for (int i = 0; i < targetPos && i < state.getNodes().size(); i++) {
                state.getNodes().get(i).setHighlight("active");
                state.setActiveNodeId(state.getNodes().get(i).getId());
                steps.add(new ExecutionStep(
                        steps.size(), "traverse",
                        String.format("Traversing to insertion index %d. Inspected index %d (%d).", targetPos, i, state.getNodes().get(i).getValue()),
                        2, state.copy()
                ));
                state.getNodes().get(i).setHighlight("default");
            }

            state.getNodes().add(targetPos, newNode);
            state.setActiveNodeId(newNode.getId());
            state.setMessage(String.format("Inserted %d at position %d by rewiring pointers.", val, targetPos));
            steps.add(new ExecutionStep(steps.size(), "insertAt", state.getMessage(), 5, state.copy()));

        } else if ("deleteHead".equalsIgnoreCase(type)) {
            if (state.getNodes().isEmpty()) {
                state.setMessage("Cannot delete head: Linked list is empty (Underflow).");
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 1, state.copy()));
                return steps;
            }

            ListNode head = state.getNodes().get(0);
            head.setHighlight("deleted");
            state.setActiveNodeId(head.getId());
            steps.add(new ExecutionStep(
                    steps.size(), "deleteHead-prep",
                    String.format("Targeted head node %d for removal. Advancing head to node.next.", head.getValue()),
                    2, state.copy()
            ));

            state.getNodes().remove(0);
            state.setActiveNodeId(null);
            state.setMessage(String.format("Head node %d deleted in O(1) time.", head.getValue()));
            steps.add(new ExecutionStep(steps.size(), "deleteHead", state.getMessage(), 3, state.copy()));

        } else if ("deleteTail".equalsIgnoreCase(type)) {
            if (state.getNodes().isEmpty()) {
                state.setMessage("Cannot delete tail: Linked list is empty.");
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 1, state.copy()));
                return steps;
            }

            int lastIdx = state.getNodes().size() - 1;
            ListNode tail = state.getNodes().get(lastIdx);
            tail.setHighlight("deleted");
            state.setActiveNodeId(tail.getId());
            steps.add(new ExecutionStep(
                    steps.size(), "deleteTail-prep",
                    String.format("Reached tail node %d. Setting predecessor's next pointer to null.", tail.getValue()),
                    3, state.copy()
            ));

            state.getNodes().remove(lastIdx);
            state.setActiveNodeId(null);
            state.setMessage(String.format("Tail node %d deleted.", tail.getValue()));
            steps.add(new ExecutionStep(steps.size(), "deleteTail", state.getMessage(), 4, state.copy()));

        } else if ("deleteAt".equalsIgnoreCase(type)) {
            int targetPos = Math.max(0, Math.min(state.getNodes().size() - 1, pos));
            if (state.getNodes().isEmpty()) {
                state.setMessage("Linked list is empty.");
                steps.add(new ExecutionStep(steps.size(), "error", state.getMessage(), 1, state.copy()));
                return steps;
            }

            for (int i = 0; i <= targetPos; i++) {
                state.getNodes().get(i).setHighlight("active");
                state.setActiveNodeId(state.getNodes().get(i).getId());
                steps.add(new ExecutionStep(
                        steps.size(), "traverse",
                        String.format("Advancing pointer to target index %d. Current node: %d.", targetPos, state.getNodes().get(i).getValue()),
                        2, state.copy()
                ));
                if (i != targetPos) state.getNodes().get(i).setHighlight("default");
            }

            ListNode targetNode = state.getNodes().get(targetPos);
            targetNode.setHighlight("deleted");
            steps.add(new ExecutionStep(
                    steps.size(), "deleteAt-prep",
                    String.format("Bypassing node %d at position %d (prev.next = curr.next).", targetNode.getValue(), targetPos),
                    4, state.copy()
            ));

            state.getNodes().remove(targetPos);
            state.setActiveNodeId(null);
            state.setMessage(String.format("Node %d at position %d successfully deleted.", targetNode.getValue(), targetPos));
            steps.add(new ExecutionStep(steps.size(), "deleteAt", state.getMessage(), 5, state.copy()));

        } else if ("reverse".equalsIgnoreCase(type)) {
            List<ListNode> reversed = new ArrayList<>();
            for (int i = 0; i < state.getNodes().size(); i++) {
                ListNode curr = state.getNodes().get(i);
                curr.setHighlight("active");
                state.setActiveNodeId(curr.getId());
                steps.add(new ExecutionStep(
                        steps.size(), "reverse-step",
                        String.format("Reversing pointer for node %d. Setting curr.next = prev.", curr.getValue()),
                        3, state.copy()
                ));
                curr.setHighlight("default");
            }

            for (int i = state.getNodes().size() - 1; i >= 0; i--) {
                reversed.add(state.getNodes().get(i));
            }
            state.setNodes(reversed);
            state.setActiveNodeId(null);
            state.setMessage("Linked list successfully reversed in O(n) time and O(1) auxiliary space.");
            steps.add(new ExecutionStep(steps.size(), "reverse-complete", state.getMessage(), 6, state.copy()));

        } else if ("search".equalsIgnoreCase(type)) {
            boolean found = false;
            for (int i = 0; i < state.getNodes().size(); i++) {
                ListNode n = state.getNodes().get(i);
                n.setHighlight("active");
                state.setActiveNodeId(n.getId());
                steps.add(new ExecutionStep(
                        steps.size(), "search-step",
                        String.format("Inspecting node with value %d. Target is %d.", n.getValue(), val),
                        2, state.copy()
                ));

                if (n.getValue() == val) {
                    n.setHighlight("inserted"); // highlight as found
                    found = true;
                    state.setMessage(String.format("Found target %d at node position %d!", val, i));
                    steps.add(new ExecutionStep(steps.size(), "found", state.getMessage(), 4, state.copy()));
                    break;
                }
                n.setHighlight("default");
            }
            if (!found) {
                state.setActiveNodeId(null);
                state.setMessage(String.format("Target value %d not found in linked list.", val));
                steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 6, state.copy()));
            }

        } else if ("traverse".equalsIgnoreCase(type)) {
            for (int i = 0; i < state.getNodes().size(); i++) {
                ListNode n = state.getNodes().get(i);
                n.setHighlight("active");
                state.setActiveNodeId(n.getId());
                state.setMessage(String.format("Traversing node %d (value = %d).", i, n.getValue()));
                steps.add(new ExecutionStep(steps.size(), "traverse", state.getMessage(), 2, state.copy()));
                n.setHighlight("default");
            }
            state.setActiveNodeId(null);
            state.setMessage("Traversal reached end of linked list (null).");
            steps.add(new ExecutionStep(steps.size(), "complete", state.getMessage(), 4, state.copy()));
        }

        for (ListNode node : state.getNodes()) {
            node.setHighlight("default");
        }

        return steps;
    }
}
