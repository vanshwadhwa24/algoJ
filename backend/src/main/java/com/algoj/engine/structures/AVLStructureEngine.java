package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.TreeStructureModel.AVLDSState;
import com.algoj.model.TreeStructureModel.AVLNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AVLStructureEngine {

    public AVLDSState createInitial(List<Integer> values) {
        AVLDSState state = new AVLDSState();
        for (int v : values) {
            state.setRoot(insert(state.getRoot(), v, null, null));
        }
        layoutTree(state.getRoot(), 350.0, 50.0, 150.0);
        state.setBalanced(true);
        state.setMessage("AVL Tree initialized with " + values.size() + " balanced nodes.");
        return state;
    }

    private int height(AVLNode n) {
        return n == null ? 0 : n.getHeight();
    }

    private void updateHeightAndBF(AVLNode n) {
        if (n == null) return;
        n.setHeight(1 + Math.max(height(n.getLeft()), height(n.getRight())));
        n.setBalanceFactor(height(n.getLeft()) - height(n.getRight()));
    }

    private void layoutTree(AVLNode node, double x, double y, double offset) {
        if (node == null) return;
        node.setX(x);
        node.setY(y);
        layoutTree(node.getLeft(), x - offset, y + 60.0, offset * 0.55);
        layoutTree(node.getRight(), x + offset, y + 60.0, offset * 0.55);
    }

    public List<ExecutionStep> executeOperation(AVLDSState currentState, String type, Integer value) {
        List<ExecutionStep> steps = new ArrayList<>();
        AVLDSState state = currentState.copy();
        int val = value != null ? value : 28;

        if ("insert".equalsIgnoreCase(type)) {
            state.setRoot(insert(state.getRoot(), val, steps, state));
            layoutTree(state.getRoot(), 350.0, 50.0, 150.0);
            state.setMessage(String.format("Successfully inserted %d into AVL Tree. Self-balancing properties preserved.", val));
            steps.add(new ExecutionStep(steps.size(), "insert-complete", state.getMessage(), 7, state.copy()));

        } else if ("search".equalsIgnoreCase(type)) {
            AVLNode curr = state.getRoot();
            boolean found = false;

            while (curr != null) {
                curr.setHighlight("active");
                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Inspecting node %d (Height %d, BF %d). Looking for %d.",
                                curr.getValue(), curr.getHeight(), curr.getBalanceFactor(), val),
                        2, state.copy()
                ));

                if (val == curr.getValue()) {
                    curr.setHighlight("inserted");
                    found = true;
                    state.setMessage(String.format("Target %d found in O(log n) time!", val));
                    steps.add(new ExecutionStep(steps.size(), "found", state.getMessage(), 3, state.copy()));
                    break;
                } else if (val < curr.getValue()) {
                    curr.setHighlight("default");
                    curr = curr.getLeft();
                } else {
                    curr.setHighlight("default");
                    curr = curr.getRight();
                }
            }

            if (!found) {
                state.setMessage(String.format("Target %d not present in AVL Tree.", val));
                steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 5, state.copy()));
            }
        }

        clearHighlights(state.getRoot());
        return steps;
    }

    private AVLNode insert(AVLNode node, int key, List<ExecutionStep> steps, AVLDSState state) {
        if (node == null) {
            AVLNode newNode = new AVLNode("avl-" + key, key);
            newNode.setHighlight("inserted");
            if (steps != null) {
                layoutTree(state.getRoot(), 350.0, 50.0, 150.0);
                steps.add(new ExecutionStep(
                        steps.size(), "insert-node",
                        String.format("Inserted new node %d.", key),
                        2, state.copy()
                ));
            }
            return newNode;
        }

        if (key < node.getValue()) {
            node.setLeft(insert(node.getLeft(), key, steps, state));
        } else if (key > node.getValue()) {
            node.setRight(insert(node.getRight(), key, steps, state));
        } else {
            return node; // Duplicate keys not allowed
        }

        updateHeightAndBF(node);
        int balance = node.getBalanceFactor();

        // 1. Left Left Case
        if (balance > 1 && key < node.getLeft().getValue()) {
            if (steps != null) {
                node.setHighlight("imbalanced");
                steps.add(new ExecutionStep(
                        steps.size(), "imbalance-ll",
                        String.format("Imbalance at node %d (BF=%d). Performing Right Rotation (LL Case).", node.getValue(), balance),
                        4, state.copy()
                ));
            }
            return rightRotate(node, steps, state);
        }

        // 2. Right Right Case
        if (balance < -1 && key > node.getRight().getValue()) {
            if (steps != null) {
                node.setHighlight("imbalanced");
                steps.add(new ExecutionStep(
                        steps.size(), "imbalance-rr",
                        String.format("Imbalance at node %d (BF=%d). Performing Left Rotation (RR Case).", node.getValue(), balance),
                        4, state.copy()
                ));
            }
            return leftRotate(node, steps, state);
        }

        // 3. Left Right Case
        if (balance > 1 && key > node.getLeft().getValue()) {
            if (steps != null) {
                node.setHighlight("imbalanced");
                steps.add(new ExecutionStep(
                        steps.size(), "imbalance-lr",
                        String.format("Imbalance at node %d (BF=%d). Performing Left-Right Rotation (LR Case).", node.getValue(), balance),
                        5, state.copy()
                ));
            }
            node.setLeft(leftRotate(node.getLeft(), steps, state));
            return rightRotate(node, steps, state);
        }

        // 4. Right Left Case
        if (balance < -1 && key < node.getRight().getValue()) {
            if (steps != null) {
                node.setHighlight("imbalanced");
                steps.add(new ExecutionStep(
                        steps.size(), "imbalance-rl",
                        String.format("Imbalance at node %d (BF=%d). Performing Right-Left Rotation (RL Case).", node.getValue(), balance),
                        5, state.copy()
                ));
            }
            node.setRight(rightRotate(node.getRight(), steps, state));
            return leftRotate(node, steps, state);
        }

        return node;
    }

    private AVLNode rightRotate(AVLNode y, List<ExecutionStep> steps, AVLDSState state) {
        AVLNode x = y.getLeft();
        AVLNode T2 = x.getRight();

        x.setRight(y);
        y.setLeft(T2);

        updateHeightAndBF(y);
        updateHeightAndBF(x);

        if (steps != null) {
            y.setHighlight("rotating");
            x.setHighlight("rotating");
            layoutTree(state.getRoot(), 350.0, 50.0, 150.0);
            steps.add(new ExecutionStep(
                    steps.size(), "rotate-right",
                    String.format("Right rotation completed: Node %d is new subtree root replacing %d.", x.getValue(), y.getValue()),
                    6, state.copy()
            ));
            y.setHighlight("default");
            x.setHighlight("default");
        }

        return x;
    }

    private AVLNode leftRotate(AVLNode x, List<ExecutionStep> steps, AVLDSState state) {
        AVLNode y = x.getRight();
        AVLNode T2 = y.getLeft();

        y.setLeft(x);
        x.setRight(T2);

        updateHeightAndBF(x);
        updateHeightAndBF(y);

        if (steps != null) {
            x.setHighlight("rotating");
            y.setHighlight("rotating");
            layoutTree(state.getRoot(), 350.0, 50.0, 150.0);
            steps.add(new ExecutionStep(
                    steps.size(), "rotate-left",
                    String.format("Left rotation completed: Node %d is new subtree root replacing %d.", y.getValue(), x.getValue()),
                    6, state.copy()
            ));
            x.setHighlight("default");
            y.setHighlight("default");
        }

        return y;
    }

    private void clearHighlights(AVLNode node) {
        if (node == null) return;
        node.setHighlight("default");
        clearHighlights(node.getLeft());
        clearHighlights(node.getRight());
    }
}
