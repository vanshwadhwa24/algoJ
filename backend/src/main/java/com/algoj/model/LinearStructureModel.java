package com.algoj.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinearStructureModel {

    // --- ARRAY ---
    public static class ArrayCell {
        private int index;
        private int value;
        private String state; // default, active, inserted, deleted, shifted

        public ArrayCell() {}
        public ArrayCell(int index, int value, String state) {
            this.index = index;
            this.value = value;
            this.state = state;
        }
        public ArrayCell copy() { return new ArrayCell(index, value, state); }

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class ArrayDSState {
        private List<ArrayCell> cells = new ArrayList<>();
        private Integer activeIndex;
        private int operationCount;
        private String message;

        public ArrayDSState() {}
        public ArrayDSState copy() {
            ArrayDSState s = new ArrayDSState();
            for (ArrayCell c : this.cells) s.cells.add(c.copy());
            s.activeIndex = this.activeIndex;
            s.operationCount = this.operationCount;
            s.message = this.message;
            return s;
        }

        public List<ArrayCell> getCells() { return cells; }
        public void setCells(List<ArrayCell> cells) { this.cells = cells; }
        public Integer getActiveIndex() { return activeIndex; }
        public void setActiveIndex(Integer activeIndex) { this.activeIndex = activeIndex; }
        public int getOperationCount() { return operationCount; }
        public void setOperationCount(int operationCount) { this.operationCount = operationCount; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- LINKED LIST ---
    public static class ListNode {
        private String id;
        private int value;
        private String highlight; // default, active, inserted, deleted, new

        public ListNode() {}
        public ListNode(String id, int value, String highlight) {
            this.id = id;
            this.value = value;
            this.highlight = highlight;
        }
        public ListNode copy() { return new ListNode(id, value, highlight); }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }
    }

    public static class LinkedListDSState {
        private boolean isDoubly;
        private List<ListNode> nodes = new ArrayList<>();
        private String activeNodeId;
        private String message;

        public LinkedListDSState() {}
        public LinkedListDSState copy() {
            LinkedListDSState s = new LinkedListDSState();
            s.isDoubly = this.isDoubly;
            for (ListNode n : this.nodes) s.nodes.add(n.copy());
            s.activeNodeId = this.activeNodeId;
            s.message = this.message;
            return s;
        }

        public boolean isDoubly() { return isDoubly; }
        public void setDoubly(boolean doubly) { isDoubly = doubly; }
        public List<ListNode> getNodes() { return nodes; }
        public void setNodes(List<ListNode> nodes) { this.nodes = nodes; }
        public String getActiveNodeId() { return activeNodeId; }
        public void setActiveNodeId(String activeNodeId) { this.activeNodeId = activeNodeId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- STACK ---
    public static class StackItem {
        private String id;
        private int value;
        private String highlight; // default, top, pushing, popping

        public StackItem() {}
        public StackItem(String id, int value, String highlight) {
            this.id = id;
            this.value = value;
            this.highlight = highlight;
        }
        public StackItem copy() { return new StackItem(id, value, highlight); }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }
    }

    public static class StackDSState {
        private List<StackItem> items = new ArrayList<>();
        private int maxCapacity = 8;
        private String message;

        public StackDSState() {}
        public StackDSState copy() {
            StackDSState s = new StackDSState();
            for (StackItem it : this.items) s.items.add(it.copy());
            s.maxCapacity = this.maxCapacity;
            s.message = this.message;
            return s;
        }

        public List<StackItem> getItems() { return items; }
        public void setItems(List<StackItem> items) { this.items = items; }
        public int getMaxCapacity() { return maxCapacity; }
        public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- QUEUE ---
    public static class QueueSlot {
        private int index;
        private Integer value;
        private String state; // empty, occupied, front, rear, enqueueing, dequeueing

        public QueueSlot() {}
        public QueueSlot(int index, Integer value, String state) {
            this.index = index;
            this.value = value;
            this.state = state;
        }
        public QueueSlot copy() { return new QueueSlot(index, value, state); }

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class QueueDSState {
        private boolean isCircular;
        private int capacity = 6;
        private List<QueueSlot> slots = new ArrayList<>();
        private int front = -1;
        private int rear = -1;
        private int size = 0;
        private String message;

        public QueueDSState() {}
        public QueueDSState copy() {
            QueueDSState s = new QueueDSState();
            s.isCircular = this.isCircular;
            s.capacity = this.capacity;
            for (QueueSlot sl : this.slots) s.slots.add(sl.copy());
            s.front = this.front;
            s.rear = this.rear;
            s.size = this.size;
            s.message = this.message;
            return s;
        }

        public boolean isCircular() { return isCircular; }
        public void setCircular(boolean circular) { isCircular = circular; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public List<QueueSlot> getSlots() { return slots; }
        public void setSlots(List<QueueSlot> slots) { this.slots = slots; }
        public int getFront() { return front; }
        public void setFront(int front) { this.front = front; }
        public int getRear() { return rear; }
        public void setRear(int rear) { this.rear = rear; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // --- HASH TABLE ---
    public static class HashEntry {
        private String key;
        private String value;
        private Boolean isDeleted;

        public HashEntry() {}
        public HashEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
        public HashEntry(String key, String value, Boolean isDeleted) {
            this.key = key;
            this.value = value;
            this.isDeleted = isDeleted;
        }
        public HashEntry copy() { return new HashEntry(key, value, isDeleted); }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public Boolean getIsDeleted() { return isDeleted; }
        public void setIsDeleted(Boolean deleted) { isDeleted = deleted; }
    }

    public static class HashBucket {
        private int index;
        private List<HashEntry> entries = new ArrayList<>();
        private String state; // default, active, collision, found, probed

        public HashBucket() {}
        public HashBucket(int index, List<HashEntry> entries, String state) {
            this.index = index;
            this.entries = entries;
            this.state = state;
        }
        public HashBucket copy() {
            HashBucket b = new HashBucket();
            b.index = this.index;
            b.state = this.state;
            for (HashEntry e : this.entries) b.entries.add(e.copy());
            return b;
        }

        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public List<HashEntry> getEntries() { return entries; }
        public void setEntries(List<HashEntry> entries) { this.entries = entries; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }

    public static class HashTableDSState {
        private String collisionStrategy = "chaining"; // chaining, probing
        private int capacity = 7;
        private List<HashBucket> buckets = new ArrayList<>();
        private Integer activeHash;
        private String message;

        public HashTableDSState() {}
        public HashTableDSState copy() {
            HashTableDSState s = new HashTableDSState();
            s.collisionStrategy = this.collisionStrategy;
            s.capacity = this.capacity;
            for (HashBucket b : this.buckets) s.buckets.add(b.copy());
            s.activeHash = this.activeHash;
            s.message = this.message;
            return s;
        }

        public String getCollisionStrategy() { return collisionStrategy; }
        public void setCollisionStrategy(String collisionStrategy) { this.collisionStrategy = collisionStrategy; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public List<HashBucket> getBuckets() { return buckets; }
        public void setBuckets(List<HashBucket> buckets) { this.buckets = buckets; }
        public Integer getActiveHash() { return activeHash; }
        public void setActiveHash(Integer activeHash) { this.activeHash = activeHash; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
