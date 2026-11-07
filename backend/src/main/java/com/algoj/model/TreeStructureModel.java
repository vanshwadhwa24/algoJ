package com.algoj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeStructureModel {

    // --- BST ---
    public static class BSTNode {
        private String id;
        private int value;
        private BSTNode left;
        private BSTNode right;
        private String highlight; // default, active, found, inserted, deleted, traversed
        private Double x;
        private Double y;

        public BSTNode() {}
        public BSTNode(String id, int value) {
            this.id = id;
            this.value = value;
            this.highlight = "default";
        }

        public BSTNode copy() {
            BSTNode n = new BSTNode();
            n.id = this.id;
            n.value = this.value;
            n.highlight = this.highlight;
            n.x = this.x;
            n.y = this.y;
            if (this.left != null) n.left = this.left.copy();
            if (this.right != null) n.right = this.right.copy();
            return n;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public BSTNode getLeft() { return left; }
        public void setLeft(BSTNode left) { this.left = left; }
        public BSTNode getRight() { return right; }
        public void setRight(BSTNode right) { this.right = right; }
        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }
        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
    }

    public static class BSTDSState {
        private BSTNode root;
        private String activeNodeId;
        private List<Integer> traversalOutput;
        private int height;
        private int nodeCount;
        private int leafCount;
        private String message;

        public BSTDSState() {}
        public BSTDSState copy() {
            BSTDSState s = new BSTDSState();
            s.root = this.root != null ? this.root.copy() : null;
            s.activeNodeId = this.activeNodeId;
            if (this.traversalOutput != null) {
                s.traversalOutput = new ArrayList<>(this.traversalOutput);
            }
            s.height = this.height;
            s.nodeCount = this.nodeCount;
            s.leafCount = this.leafCount;
            s.message = this.message;
            return s;
        }

        public BSTNode getRoot() { return root; }
        public void setRoot(BSTNode root) { this.root = root; }
        public String getActiveNodeId() { return activeNodeId; }
        public void setActiveNodeId(String activeNodeId) { this.activeNodeId = activeNodeId; }
        public List<Integer> getTraversalOutput() { return traversalOutput; }
        public void setTraversalOutput(List<Integer> traversalOutput) { this.traversalOutput = traversalOutput; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        public int getNodeCount() { return nodeCount; }
        public void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }
        public int getLeafCount() { return leafCount; }
        public void setLeafCount(int leafCount) { this.leafCount = leafCount; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- AVL ---
    public static class AVLNode {
        private String id;
        private int value;
        private int height = 1;
        private int balanceFactor = 0;
        private AVLNode left;
        private AVLNode right;
        private String highlight; // default, active, imbalanced, rotating, inserted, deleted
        private Double x;
        private Double y;

        public AVLNode() {}
        public AVLNode(String id, int value) {
            this.id = id;
            this.value = value;
            this.height = 1;
            this.balanceFactor = 0;
            this.highlight = "default";
        }

        public AVLNode copy() {
            AVLNode n = new AVLNode();
            n.id = this.id;
            n.value = this.value;
            n.height = this.height;
            n.balanceFactor = this.balanceFactor;
            n.highlight = this.highlight;
            n.x = this.x;
            n.y = this.y;
            if (this.left != null) n.left = this.left.copy();
            if (this.right != null) n.right = this.right.copy();
            return n;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        public int getBalanceFactor() { return balanceFactor; }
        public void setBalanceFactor(int balanceFactor) { this.balanceFactor = balanceFactor; }
        public AVLNode getLeft() { return left; }
        public void setLeft(AVLNode left) { this.left = left; }
        public AVLNode getRight() { return right; }
        public void setRight(AVLNode right) { this.right = right; }
        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }
        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
    }

    public static class AVLDSState {
        private AVLNode root;
        private String message;
        private boolean isBalanced = true;

        public AVLDSState() {}
        public AVLDSState copy() {
            AVLDSState s = new AVLDSState();
            s.root = this.root != null ? this.root.copy() : null;
            s.message = this.message;
            s.isBalanced = this.isBalanced;
            return s;
        }

        public AVLNode getRoot() { return root; }
        public void setRoot(AVLNode root) { this.root = root; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public boolean isBalanced() { return isBalanced; }
        public void setBalanced(boolean balanced) { isBalanced = balanced; }
    }

    // --- HEAP ---
    public static class HeapElement {
        private int value;
        private String state; // default, comparing, swapping, violation, placed

        public HeapElement() {}
        public HeapElement(int value, String state) {
            this.value = value;
            this.state = state;
        }
        public HeapElement copy() { return new HeapElement(value, state); }

        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class HeapDSState {
        private String type = "max"; // min, max
        private List<HeapElement> array = new ArrayList<>();
        private List<Integer> activeIndices = new ArrayList<>();
        private List<Integer> comparingIndices = new ArrayList<>();
        private String message;

        public HeapDSState() {}
        public HeapDSState copy() {
            HeapDSState s = new HeapDSState();
            s.type = this.type;
            for (HeapElement el : this.array) s.array.add(el.copy());
            s.activeIndices = new ArrayList<>(this.activeIndices);
            s.comparingIndices = new ArrayList<>(this.comparingIndices);
            s.message = this.message;
            return s;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<HeapElement> getArray() { return array; }
        public void setArray(List<HeapElement> array) { this.array = array; }
        public List<Integer> getActiveIndices() { return activeIndices; }
        public void setActiveIndices(List<Integer> activeIndices) { this.activeIndices = activeIndices; }
        public List<Integer> getComparingIndices() { return comparingIndices; }
        public void setComparingIndices(List<Integer> comparingIndices) { this.comparingIndices = comparingIndices; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- TRIE ---
    public static class TrieNode {
        private String id;
        private String character; // char represented as string
        private boolean isEndOfWord;
        private Map<String, TrieNode> children = new HashMap<>();
        private String highlight; // default, active, found, inserted, deleted
        private Double x;
        private Double y;

        public TrieNode() {}
        public TrieNode(String id, String character) {
            this.id = id;
            this.character = character;
            this.highlight = "default";
        }

        public TrieNode copy() {
            TrieNode n = new TrieNode();
            n.id = this.id;
            n.character = this.character;
            n.isEndOfWord = this.isEndOfWord;
            n.highlight = this.highlight;
            n.x = this.x;
            n.y = this.y;
            for (Map.Entry<String, TrieNode> entry : this.children.entrySet()) {
                n.children.put(entry.getKey(), entry.getValue().copy());
            }
            return n;
        }

        // Getters and setters with getChar / setChar matching frontend 'char' field:
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        @com.fasterxml.jackson.annotation.JsonProperty("char")
        public String getChar() { return character; }
        @com.fasterxml.jackson.annotation.JsonProperty("char")
        public void setChar(String character) { this.character = character; }
        public boolean isEndOfWord() { return isEndOfWord; }
        public void setEndOfWord(boolean endOfWord) { isEndOfWord = endOfWord; }
        public Map<String, TrieNode> getChildren() { return children; }
        public void setChildren(Map<String, TrieNode> children) { this.children = children; }
        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }
        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
    }

    public static class TrieDSState {
        private TrieNode root;
        private int wordCount;
        private List<String> wordsList = new ArrayList<>();
        private String activePrefix = "";
        private List<String> searchResults = new ArrayList<>();
        private String message;

        public TrieDSState() {}
        public TrieDSState copy() {
            TrieDSState s = new TrieDSState();
            s.root = this.root != null ? this.root.copy() : null;
            s.wordCount = this.wordCount;
            s.wordsList = new ArrayList<>(this.wordsList);
            s.activePrefix = this.activePrefix;
            s.searchResults = new ArrayList<>(this.searchResults);
            s.message = this.message;
            return s;
        }

        public TrieNode getRoot() { return root; }
        public void setRoot(TrieNode root) { this.root = root; }
        public int getWordCount() { return wordCount; }
        public void setWordCount(int wordCount) { this.wordCount = wordCount; }
        public List<String> getWordsList() { return wordsList; }
        public void setWordsList(List<String> wordsList) { this.wordsList = wordsList; }
        public String getActivePrefix() { return activePrefix; }
        public void setActivePrefix(String activePrefix) { this.activePrefix = activePrefix; }
        public List<String> getSearchResults() { return searchResults; }
        public void setSearchResults(List<String> searchResults) { this.searchResults = searchResults; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
