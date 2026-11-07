package com.algoj;

import com.algoj.dto.ExecutionStep;
import com.algoj.engine.structures.*;
import com.algoj.model.LinearStructureModel.*;
import com.algoj.model.TreeStructureModel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataStructureEnginesTest {

    private ArrayStructureEngine arrayEngine;
    private LinkedListStructureEngine linkedListEngine;
    private StackStructureEngine stackEngine;
    private QueueStructureEngine queueEngine;
    private HashTableStructureEngine hashTableEngine;
    private BSTStructureEngine bstEngine;
    private AVLStructureEngine avlEngine;
    private HeapStructureEngine heapEngine;
    private TrieStructureEngine trieEngine;

    @BeforeEach
    void setUp() {
        arrayEngine = new ArrayStructureEngine();
        linkedListEngine = new LinkedListStructureEngine();
        stackEngine = new StackStructureEngine();
        queueEngine = new QueueStructureEngine();
        hashTableEngine = new HashTableStructureEngine();
        bstEngine = new BSTStructureEngine();
        avlEngine = new AVLStructureEngine();
        heapEngine = new HeapStructureEngine();
        trieEngine = new TrieStructureEngine();
    }

    private void assertTraceOrderAndValidity(List<ExecutionStep> steps) {
        assertNotNull(steps);
        assertFalse(steps.isEmpty());
        for (int i = 0; i < steps.size(); i++) {
            assertEquals(i, steps.get(i).getStepIndex());
            assertNotNull(steps.get(i).getOperationType());
            assertNotNull(steps.get(i).getDescription());
            assertNotNull(steps.get(i).getState());
        }
    }

    @Test
    @DisplayName("Array DS: access, insert with right shifts, delete with left shifts")
    void testArrayDS() {
        ArrayDSState state = arrayEngine.createInitial(List.of(10, 20, 30));
        assertEquals(3, state.getCells().size());

        List<ExecutionStep> accessSteps = arrayEngine.executeOperation(state, "access", 1, null);
        assertTraceOrderAndValidity(accessSteps);

        List<ExecutionStep> insertSteps = arrayEngine.executeOperation(state, "insert", 1, 99);
        assertTraceOrderAndValidity(insertSteps);
        ArrayDSState afterInsert = (ArrayDSState) insertSteps.get(insertSteps.size() - 1).getState();
        assertEquals(4, afterInsert.getCells().size());
        assertEquals(99, afterInsert.getCells().get(1).getValue());

        List<ExecutionStep> deleteSteps = arrayEngine.executeOperation(afterInsert, "delete", 1, null);
        assertTraceOrderAndValidity(deleteSteps);
        ArrayDSState afterDelete = (ArrayDSState) deleteSteps.get(deleteSteps.size() - 1).getState();
        assertEquals(3, afterDelete.getCells().size());
        assertEquals(20, afterDelete.getCells().get(1).getValue());
    }

    @Test
    @DisplayName("Linked List DS: insertHead, insertTail, reverse pointers")
    void testLinkedListDS() {
        LinkedListDSState state = linkedListEngine.createInitial(List.of(10, 20), false);
        assertEquals(2, state.getNodes().size());

        List<ExecutionStep> headSteps = linkedListEngine.executeOperation(state, "insertHead", 5, 0);
        assertTraceOrderAndValidity(headSteps);
        LinkedListDSState afterHead = (LinkedListDSState) headSteps.get(headSteps.size() - 1).getState();
        assertEquals(5, afterHead.getNodes().get(0).getValue());

        List<ExecutionStep> revSteps = linkedListEngine.executeOperation(afterHead, "reverse", null, null);
        assertTraceOrderAndValidity(revSteps);
        LinkedListDSState afterRev = (LinkedListDSState) revSteps.get(revSteps.size() - 1).getState();
        assertEquals(20, afterRev.getNodes().get(0).getValue());
        assertEquals(5, afterRev.getNodes().get(afterRev.getNodes().size() - 1).getValue());
    }

    @Test
    @DisplayName("Stack DS: push, pop, overflow, underflow invariants")
    void testStackDS() {
        StackDSState state = stackEngine.createInitial(List.of(1, 2), 3);
        List<ExecutionStep> pushSteps = stackEngine.executeOperation(state, "push", 3);
        assertTraceOrderAndValidity(pushSteps);
        StackDSState afterPush = (StackDSState) pushSteps.get(pushSteps.size() - 1).getState();
        assertEquals(3, afterPush.getItems().size());

        // Overflow
        List<ExecutionStep> overflowSteps = stackEngine.executeOperation(afterPush, "push", 4);
        assertEquals("overflow", overflowSteps.get(0).getOperationType());

        // Pop
        List<ExecutionStep> popSteps = stackEngine.executeOperation(afterPush, "pop", null);
        assertTraceOrderAndValidity(popSteps);
        StackDSState afterPop = (StackDSState) popSteps.get(popSteps.size() - 1).getState();
        assertEquals(2, afterPop.getItems().size());
    }

    @Test
    @DisplayName("Queue DS: FIFO ordering, circular capacity, peek")
    void testQueueDS() {
        QueueDSState state = queueEngine.createInitial(List.of(10, 20), 4, false);
        assertEquals(2, state.getSize());

        List<ExecutionStep> enqSteps = queueEngine.executeOperation(state, "enqueue", 30);
        assertTraceOrderAndValidity(enqSteps);
        QueueDSState afterEnq = (QueueDSState) enqSteps.get(enqSteps.size() - 1).getState();
        assertEquals(3, afterEnq.getSize());

        List<ExecutionStep> deqSteps = queueEngine.executeOperation(afterEnq, "dequeue", null);
        assertTraceOrderAndValidity(deqSteps);
        QueueDSState afterDeq = (QueueDSState) deqSteps.get(deqSteps.size() - 1).getState();
        assertEquals(2, afterDeq.getSize());
    }

    @Test
    @DisplayName("Hash Table DS: Chaining and Linear Probing with collisions")
    void testHashTableDS() {
        HashTableDSState chainingState = hashTableEngine.createInitial("chaining", 5, List.of(
                new String[]{"apple", "1"},
                new String[]{"banana", "2"}
        ));
        List<ExecutionStep> cSteps = hashTableEngine.executeOperation(chainingState, "insert", "cherry", "3");
        assertTraceOrderAndValidity(cSteps);

        HashTableDSState probingState = hashTableEngine.createInitial("probing", 5, List.<String[]>of(
                new String[]{"k1", "10"}
        ));
        List<ExecutionStep> pSteps = hashTableEngine.executeOperation(probingState, "insert", "k2", "20");
        assertTraceOrderAndValidity(pSteps);
    }

    @Test
    @DisplayName("BST DS: Inorder traversal maintains strict sorting invariant")
    void testBSTDS() {
        BSTDSState state = bstEngine.createInitial(List.of(50, 30, 70, 20, 40, 60, 80));
        assertEquals(7, state.getNodeCount());

        List<ExecutionStep> inorderSteps = bstEngine.executeOperation(state, "inorder", null);
        assertTraceOrderAndValidity(inorderSteps);
        BSTDSState finalState = (BSTDSState) inorderSteps.get(inorderSteps.size() - 1).getState();
        List<Integer> out = finalState.getTraversalOutput();
        assertNotNull(out);
        assertEquals(7, out.size());
        for (int i = 0; i < out.size() - 1; i++) {
            assertTrue(out.get(i) < out.get(i + 1), "Inorder traversal of BST must produce strictly ascending values");
        }
    }

    @Test
    @DisplayName("AVL DS: Maintains balance invariant (-1 <= BF <= 1)")
    void testAVLDS() {
        AVLDSState state = avlEngine.createInitial(List.of(10, 20, 30, 40, 50, 25));
        assertTrue(state.isBalanced());
        assertNotNull(state.getRoot());
        assertTrue(Math.abs(state.getRoot().getBalanceFactor()) <= 1, "AVL root balance factor must be in {-1, 0, 1}");
    }

    @Test
    @DisplayName("Heap DS: Max heap invariant (parent >= children)")
    void testHeapDS() {
        HeapDSState state = heapEngine.createInitial("max", List.of(80, 50, 70, 30, 40, 60));
        List<ExecutionStep> insertSteps = heapEngine.executeOperation(state, "insert", 95);
        assertTraceOrderAndValidity(insertSteps);
        HeapDSState afterInsert = (HeapDSState) insertSteps.get(insertSteps.size() - 1).getState();
        assertEquals(95, afterInsert.getArray().get(0).getValue(), "New max 95 must bubble up to root index 0");
    }

    @Test
    @DisplayName("Trie DS: Insert word, prefix search, and exact match")
    void testTrieDS() {
        TrieDSState state = trieEngine.createInitial(List.of("car", "cart", "cat", "dog"));
        List<ExecutionStep> exactMatch = trieEngine.executeOperation(state, "searchWord", "cart");
        assertTraceOrderAndValidity(exactMatch);
        assertEquals("found", exactMatch.get(exactMatch.size() - 1).getOperationType());

        List<ExecutionStep> prefixSteps = trieEngine.executeOperation(state, "startsWith", "ca");
        assertTraceOrderAndValidity(prefixSteps);
        TrieDSState prefixState = (TrieDSState) prefixSteps.get(prefixSteps.size() - 1).getState();
        assertEquals(3, prefixState.getSearchResults().size(), "Prefix 'ca' must match car, cart, cat");
    }
}
