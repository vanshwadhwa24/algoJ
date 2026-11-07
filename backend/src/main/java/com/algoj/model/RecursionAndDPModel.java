package com.algoj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecursionAndDPModel {

    // --- FACTORIAL ---
    public static class StackFrame {
        private String id;
        private String name;
        private int arg;
        private Integer returnValue;
        private String state; // calling, base-case, returned

        public StackFrame() {}
        public StackFrame(String id, String name, int arg, String state) {
            this.id = id;
            this.name = name;
            this.arg = arg;
            this.state = state;
        }
        public StackFrame copy() {
            StackFrame f = new StackFrame(id, name, arg, state);
            f.returnValue = this.returnValue;
            return f;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getArg() { return arg; }
        public void setArg(int arg) { this.arg = arg; }
        public Integer getReturnValue() { return returnValue; }
        public void setReturnValue(Integer returnValue) { this.returnValue = returnValue; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class FactorialState {
        private int n;
        private List<StackFrame> callStack = new ArrayList<>();
        private Integer result;
        private String message;

        public FactorialState() {}
        public FactorialState copy() {
            FactorialState s = new FactorialState();
            s.n = this.n;
            for (StackFrame f : this.callStack) s.callStack.add(f.copy());
            s.result = this.result;
            s.message = this.message;
            return s;
        }

        public int getN() { return n; }
        public void setN(int n) { this.n = n; }
        public List<StackFrame> getCallStack() { return callStack; }
        public void setCallStack(List<StackFrame> callStack) { this.callStack = callStack; }
        public Integer getResult() { return result; }
        public void setResult(Integer result) { this.result = result; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- FIBONACCI ---
    public static class FibonacciState {
        private int n;
        private String mode; // recursive, memoized, tabulated
        private List<String> callStack = new ArrayList<>();
        private Map<Integer, Integer> memoTable = new HashMap<>();
        private List<Integer> dpArray;
        private Integer currentIndex;
        private int totalCalls;
        private Integer result;
        private String message;

        public FibonacciState() {}
        public FibonacciState copy() {
            FibonacciState s = new FibonacciState();
            s.n = this.n;
            s.mode = this.mode;
            s.callStack = new ArrayList<>(this.callStack);
            s.memoTable = new HashMap<>(this.memoTable);
            s.dpArray = this.dpArray != null ? new ArrayList<>(this.dpArray) : null;
            s.currentIndex = this.currentIndex;
            s.totalCalls = this.totalCalls;
            s.result = this.result;
            s.message = this.message;
            return s;
        }

        public int getN() { return n; }
        public void setN(int n) { this.n = n; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public List<String> getCallStack() { return callStack; }
        public void setCallStack(List<String> callStack) { this.callStack = callStack; }
        public Map<Integer, Integer> getMemoTable() { return memoTable; }
        public void setMemoTable(Map<Integer, Integer> memoTable) { this.memoTable = memoTable; }
        public List<Integer> getDpArray() { return dpArray; }
        public void setDpArray(List<Integer> dpArray) { this.dpArray = dpArray; }
        public Integer getCurrentIndex() { return currentIndex; }
        public void setCurrentIndex(Integer currentIndex) { this.currentIndex = currentIndex; }
        public int getTotalCalls() { return totalCalls; }
        public void setTotalCalls(int totalCalls) { this.totalCalls = totalCalls; }
        public Integer getResult() { return result; }
        public void setResult(Integer result) { this.result = result; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- TOWER OF HANOI ---
    public static class HanoiState {
        private int disksCount;
        private Map<String, List<Integer>> pegs = new HashMap<>();
        private int moveCount;
        private Map<String, Object> lastMove;
        private String message;

        public HanoiState() {}
        public HanoiState copy() {
            HanoiState s = new HanoiState();
            s.disksCount = this.disksCount;
            for (Map.Entry<String, List<Integer>> e : this.pegs.entrySet()) {
                s.pegs.put(e.getKey(), new ArrayList<>(e.getValue()));
            }
            s.moveCount = this.moveCount;
            s.lastMove = this.lastMove != null ? new HashMap<>(this.lastMove) : null;
            s.message = this.message;
            return s;
        }

        public int getDisksCount() { return disksCount; }
        public void setDisksCount(int disksCount) { this.disksCount = disksCount; }
        public Map<String, List<Integer>> getPegs() { return pegs; }
        public void setPegs(Map<String, List<Integer>> pegs) { this.pegs = pegs; }
        public int getMoveCount() { return moveCount; }
        public void setMoveCount(int moveCount) { this.moveCount = moveCount; }
        public Map<String, Object> getLastMove() { return lastMove; }
        public void setLastMove(Map<String, Object> lastMove) { this.lastMove = lastMove; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- N QUEENS ---
    public static class NQueensState {
        private int n;
        private List<Integer> board = new ArrayList<>();
        private int currentRow;
        private int currentCol;
        private boolean isConflict;
        private int solutionsFound;
        private String message;

        public NQueensState() {}
        public NQueensState copy() {
            NQueensState s = new NQueensState();
            s.n = this.n;
            s.board = new ArrayList<>(this.board);
            s.currentRow = this.currentRow;
            s.currentCol = this.currentCol;
            s.isConflict = this.isConflict;
            s.solutionsFound = this.solutionsFound;
            s.message = this.message;
            return s;
        }

        public int getN() { return n; }
        public void setN(int n) { this.n = n; }
        public List<Integer> getBoard() { return board; }
        public void setBoard(List<Integer> board) { this.board = board; }
        public int getCurrentRow() { return currentRow; }
        public void setCurrentRow(int currentRow) { this.currentRow = currentRow; }
        public int getCurrentCol() { return currentCol; }
        public void setCurrentCol(int currentCol) { this.currentCol = currentCol; }
        public boolean isConflict() { return isConflict; }
        public void setConflict(boolean conflict) { isConflict = conflict; }
        public int getSolutionsFound() { return solutionsFound; }
        public void setSolutionsFound(int solutionsFound) { this.solutionsFound = solutionsFound; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- 0/1 KNAPSACK ---
    public static class KnapsackItem {
        private String id;
        private int weight;
        private int value;
        private String name;

        public KnapsackItem() {}
        public KnapsackItem(String id, String name, int weight, int value) {
            this.id = id;
            this.name = name;
            this.weight = weight;
            this.value = value;
        }
        public KnapsackItem copy() { return new KnapsackItem(id, name, weight, value); }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class KnapsackState {
        private List<KnapsackItem> items = new ArrayList<>();
        private int capacity;
        private List<List<Integer>> table = new ArrayList<>();
        private int currentRow;
        private int currentCol;
        private List<List<Integer>> activeDependencies = new ArrayList<>();
        private List<String> selectedItems = new ArrayList<>();
        private int optimalValue;
        private String message;

        public KnapsackState() {}
        public KnapsackState copy() {
            KnapsackState s = new KnapsackState();
            for (KnapsackItem it : this.items) s.items.add(it.copy());
            s.capacity = this.capacity;
            for (List<Integer> row : this.table) s.table.add(new ArrayList<>(row));
            s.currentRow = this.currentRow;
            s.currentCol = this.currentCol;
            for (List<Integer> dep : this.activeDependencies) s.activeDependencies.add(new ArrayList<>(dep));
            s.selectedItems = new ArrayList<>(this.selectedItems);
            s.optimalValue = this.optimalValue;
            s.message = this.message;
            return s;
        }

        public List<KnapsackItem> getItems() { return items; }
        public void setItems(List<KnapsackItem> items) { this.items = items; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public List<List<Integer>> getTable() { return table; }
        public void setTable(List<List<Integer>> table) { this.table = table; }
        public int getCurrentRow() { return currentRow; }
        public void setCurrentRow(int currentRow) { this.currentRow = currentRow; }
        public int getCurrentCol() { return currentCol; }
        public void setCurrentCol(int currentCol) { this.currentCol = currentCol; }
        public List<List<Integer>> getActiveDependencies() { return activeDependencies; }
        public void setActiveDependencies(List<List<Integer>> activeDependencies) { this.activeDependencies = activeDependencies; }
        public List<String> getSelectedItems() { return selectedItems; }
        public void setSelectedItems(List<String> selectedItems) { this.selectedItems = selectedItems; }
        public int getOptimalValue() { return optimalValue; }
        public void setOptimalValue(int optimalValue) { this.optimalValue = optimalValue; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- LCS ---
    public static class LCSState {
        private String s1;
        private String s2;
        private List<List<Integer>> table = new ArrayList<>();
        private int currentRow;
        private int currentCol;
        private List<List<Integer>> activeDependencies = new ArrayList<>();
        private String lcsResult = "";
        private String message;

        public LCSState() {}
        public LCSState copy() {
            LCSState s = new LCSState();
            s.s1 = this.s1;
            s.s2 = this.s2;
            for (List<Integer> row : this.table) s.table.add(new ArrayList<>(row));
            s.currentRow = this.currentRow;
            s.currentCol = this.currentCol;
            for (List<Integer> dep : this.activeDependencies) s.activeDependencies.add(new ArrayList<>(dep));
            s.lcsResult = this.lcsResult;
            s.message = this.message;
            return s;
        }

        public String getS1() { return s1; }
        public void setS1(String s1) { this.s1 = s1; }
        public String getS2() { return s2; }
        public void setS2(String s2) { this.s2 = s2; }
        public List<List<Integer>> getTable() { return table; }
        public void setTable(List<List<Integer>> table) { this.table = table; }
        public int getCurrentRow() { return currentRow; }
        public void setCurrentRow(int currentRow) { this.currentRow = currentRow; }
        public int getCurrentCol() { return currentCol; }
        public void setCurrentCol(int currentCol) { this.currentCol = currentCol; }
        public List<List<Integer>> getActiveDependencies() { return activeDependencies; }
        public void setActiveDependencies(List<List<Integer>> activeDependencies) { this.activeDependencies = activeDependencies; }
        public String getLcsResult() { return lcsResult; }
        public void setLcsResult(String lcsResult) { this.lcsResult = lcsResult; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
