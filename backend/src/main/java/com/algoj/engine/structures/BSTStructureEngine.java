package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.TreeStructureModel.BSTDSState;
import com.algoj.model.TreeStructureModel.BSTNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BSTStructureEngine {

    public BSTDSState createInitial(List<Integer> values) {
        BSTDSState state = new BSTDSState();
        for (int v : values) {
            state.setRoot(insertNode(state.getRoot(), v));
        }
        layoutTree(state.getRoot(), 350.0, 50.0, 160.0);
        updateStats(state);
        state.setMessage("Binary Search Tree initialized with " + values.size() + " keys.");
        return state;
    }

    private BSTNode insertNode(BSTNode root, int val) {
        if (root == null) {
            return new BSTNode("bst-" + val, val);
        }
        if (val < root.getValue()) {
            root.setLeft(insertNode(root.getLeft(), val));
        } else if (val > root.getValue()) {
            root.setRight(insertNode(root.getRight(), val));
        }
        return root;
    }

    private void layoutTree(BSTNode node, double x, double y, double offset) {
        if (node == null) return;
        node.setX(x);
        node.setY(y);
        layoutTree(node.getLeft(), x - offset, y + 60.0, offset * 0.55);
        layoutTree(node.getRight(), x + offset, y + 60.0, offset * 0.55);
    }

    private void updateStats(BSTDSState state) {
        state.setHeight(computeHeight(state.getRoot()));
        state.setNodeCount(countNodes(state.getRoot()));
        state.setLeafCount(countLeaves(state.getRoot()));
    }

    private int computeHeight(BSTNode node) {
        if (node == null) return 0;
        return 1 + Math.max(computeHeight(node.getLeft()), computeHeight(node.getRight()));
    }

    private int countNodes(BSTNode node) {
        if (node == null) return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    private int countLeaves(BSTNode node) {
        if (node == null) return 0;
        if (node.getLeft() == null && node.getRight() == null) return 1;
        return countLeaves(node.getLeft()) + countLeaves(node.getRight());
    }

    public List<ExecutionStep> executeOperation(BSTDSState currentState, String type, Integer value) {
        List<ExecutionStep> steps = new ArrayList<>();
        BSTDSState state = currentState.copy();
        int val = value != null ? value : 35;

        if ("search".equalsIgnoreCase(type)) {
            BSTNode curr = state.getRoot();
            boolean found = false;

            while (curr != null) {
                curr.setHighlight("active");
                state.setActiveNodeId(curr.getId());

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Comparing search key %d with current node %d.", val, curr.getValue()),
                        2, state.copy()
                ));

                if (val == curr.getValue()) {
                    curr.setHighlight("found");
                    found = true;
                    state.setMessage(String.format("Key %d FOUND in BST!", val));
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
                state.setActiveNodeId(null);
                state.setMessage(String.format("Key %d NOT FOUND in BST. Reached null leaf pointer.", val));
                steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 5, state.copy()));
            }

        } else if ("insert".equalsIgnoreCase(type)) {
            if (state.getRoot() == null) {
                state.setRoot(new BSTNode("bst-" + val, val));
                layoutTree(state.getRoot(), 350.0, 50.0, 160.0);
                updateStats(state);
                state.setMessage(String.format("Inserted %d as new root of BST.", val));
                steps.add(new ExecutionStep(steps.size(), "insert-root", state.getMessage(), 2, state.copy()));
                return steps;
            }

            BSTNode curr = state.getRoot();
            BSTNode parent = null;

            while (curr != null) {
                parent = curr;
                curr.setHighlight("active");
                state.setActiveNodeId(curr.getId());

                steps.add(new ExecutionStep(
                        steps.size(), "compare",
                        String.format("Navigating BST: Comparing %d with %d.", val, curr.getValue()),
                        2, state.copy()
                ));

                if (val == curr.getValue()) {
                    state.setMessage(String.format("Duplicate key %d already exists in BST! Ignored.", val));
                    steps.add(new ExecutionStep(steps.size(), "duplicate", state.getMessage(), 3, state.copy()));
                    curr.setHighlight("default");
                    return steps;
                } else if (val < curr.getValue()) {
                    curr.setHighlight("default");
                    curr = curr.getLeft();
                } else {
                    curr.setHighlight("default");
                    curr = curr.getRight();
                }
            }

            BSTNode newNode = new BSTNode("bst-" + val, val);
            newNode.setHighlight("inserted");
            if (val < parent.getValue()) {
                parent.setLeft(newNode);
            } else {
                parent.setRight(newNode);
            }

            layoutTree(state.getRoot(), 350.0, 50.0, 160.0);
            updateStats(state);
            state.setActiveNodeId(newNode.getId());
            state.setMessage(String.format("Inserted new node %d under parent %d.", val, parent.getValue()));
            steps.add(new ExecutionStep(steps.size(), "insert", state.getMessage(), 5, state.copy()));

        } else if ("delete".equalsIgnoreCase(type)) {
            state.setRoot(deleteRecursive(state.getRoot(), val, steps, state));
            layoutTree(state.getRoot(), 350.0, 50.0, 160.0);
            updateStats(state);
            state.setActiveNodeId(null);
            state.setMessage(String.format("Finished deletion operation for key %d.", val));
            steps.add(new ExecutionStep(steps.size(), "delete-complete", state.getMessage(), 6, state.copy()));

        } else if ("inorder".equalsIgnoreCase(type) || "preorder".equalsIgnoreCase(type) || "postorder".equalsIgnoreCase(type)) {
            List<Integer> result = new ArrayList<>();
            state.setTraversalOutput(result);

            if ("inorder".equalsIgnoreCase(type)) {
                inorderTraverse(state.getRoot(), result, steps, state);
            } else if ("preorder".equalsIgnoreCase(type)) {
                preorderTraverse(state.getRoot(), result, steps, state);
            } else {
                postorderTraverse(state.getRoot(), result, steps, state);
            }

            state.setActiveNodeId(null);
            state.setMessage(String.format("%s traversal complete: %s.", type.toUpperCase(), result));
            steps.add(new ExecutionStep(steps.size(), "traverse-complete", state.getMessage(), 5, state.copy()));
        }

        clearHighlights(state.getRoot());
        return steps;
    }

    private BSTNode deleteRecursive(BSTNode root, int val, List<ExecutionStep> steps, BSTDSState state) {
        if (root == null) return null;

        root.setHighlight("active");
        state.setActiveNodeId(root.getId());
        steps.add(new ExecutionStep(
                steps.size(), "search",
                String.format("Searching for node %d to delete (Current: %d).", val, root.getValue()),
                2, state.copy()
        ));
        root.setHighlight("default");

        if (val < root.getValue()) {
            root.setLeft(deleteRecursive(root.getLeft(), val, steps, state));
        } else if (val > root.getValue()) {
            root.setRight(deleteRecursive(root.getRight(), val, steps, state));
        } else {
            // Found node
            root.setHighlight("deleted");
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }

            // Two children: find inorder successor
            BSTNode successor = root.getRight();
            while (successor.getLeft() != null) {
                successor = successor.getLeft();
            }
            root.setValue(successor.getValue());
            root.setRight(deleteRecursive(root.getRight(), successor.getValue(), steps, state));
        }
        return root;
    }

    private void inorderTraverse(BSTNode node, List<Integer> res, List<ExecutionStep> steps, BSTDSState state) {
        if (node == null) return;
        inorderTraverse(node.getLeft(), res, steps, state);

        node.setHighlight("traversed");
        state.setActiveNodeId(node.getId());
        res.add(node.getValue());
        state.setTraversalOutput(new ArrayList<>(res));

        steps.add(new ExecutionStep(
                steps.size(), "visit",
                String.format("Inorder visit: node %d. (Left subtree finished -> Root -> Right subtree).", node.getValue()),
                3, state.copy()
        ));

        inorderTraverse(node.getRight(), res, steps, state);
    }

    private void preorderTraverse(BSTNode node, List<Integer> res, List<ExecutionStep> steps, BSTDSState state) {
        if (node == null) return;
        node.setHighlight("traversed");
        state.setActiveNodeId(node.getId());
        res.add(node.getValue());
        state.setTraversalOutput(new ArrayList<>(res));

        steps.add(new ExecutionStep(
                steps.size(), "visit",
                String.format("Preorder visit: node %d.", node.getValue()),
                2, state.copy()
        ));

        preorderTraverse(node.getLeft(), res, steps, state);
        preorderTraverse(node.getRight(), res, steps, state);
    }

    private void postorderTraverse(BSTNode node, List<Integer> res, List<ExecutionStep> steps, BSTDSState state) {
        if (node == null) return;
        postorderTraverse(node.getLeft(), res, steps, state);
        postorderTraverse(node.getRight(), res, steps, state);

        node.setHighlight("traversed");
        state.setActiveNodeId(node.getId());
        res.add(node.getValue());
        state.setTraversalOutput(new ArrayList<>(res));

        steps.add(new ExecutionStep(
                steps.size(), "visit",
                String.format("Postorder visit: node %d.", node.getValue()),
                4, state.copy()
        ));
    }

    private void clearHighlights(BSTNode node) {
        if (node == null) return;
        node.setHighlight("default");
        clearHighlights(node.getLeft());
        clearHighlights(node.getRight());
    }
}
