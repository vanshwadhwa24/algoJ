package com.algoj.service;

import com.algoj.dto.DataStructureOpRequest;
import com.algoj.dto.ExecutionStep;
import com.algoj.dto.ExecutionTraceResponse;
import com.algoj.engine.structures.*;
import com.algoj.model.LinearStructureModel.*;
import com.algoj.model.TreeStructureModel.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service orchestrating authoritative execution of interactive data structure operations in Java.
 * Encapsulates state deserialization, type coercion, and initial state generation.
 */
@Service
public class DataStructureExecutionService {

    private final ArrayStructureEngine arrayEngine;
    private final LinkedListStructureEngine linkedListEngine;
    private final StackStructureEngine stackEngine;
    private final QueueStructureEngine queueEngine;
    private final HashTableStructureEngine hashTableEngine;
    private final BSTStructureEngine bstEngine;
    private final AVLStructureEngine avlEngine;
    private final HeapStructureEngine heapEngine;
    private final TrieStructureEngine trieEngine;
    private final ObjectMapper objectMapper;

    public DataStructureExecutionService(ArrayStructureEngine arrayEngine,
                                         LinkedListStructureEngine linkedListEngine,
                                         StackStructureEngine stackEngine,
                                         QueueStructureEngine queueEngine,
                                         HashTableStructureEngine hashTableEngine,
                                         BSTStructureEngine bstEngine,
                                         AVLStructureEngine avlEngine,
                                         HeapStructureEngine heapEngine,
                                         TrieStructureEngine trieEngine,
                                         ObjectMapper objectMapper) {
        this.arrayEngine = arrayEngine;
        this.linkedListEngine = linkedListEngine;
        this.stackEngine = stackEngine;
        this.queueEngine = queueEngine;
        this.hashTableEngine = hashTableEngine;
        this.bstEngine = bstEngine;
        this.avlEngine = avlEngine;
        this.heapEngine = heapEngine;
        this.trieEngine = trieEngine;
        this.objectMapper = objectMapper;
    }

    public ExecutionTraceResponse operate(DataStructureOpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("DataStructureOpRequest must not be null.");
        }

        String structId = request.getStructureId();
        String op = request.getOperation();
        if (structId == null || op == null) {
            throw new IllegalArgumentException("structureId and operation must not be null.");
        }

        List<ExecutionStep> steps;

        switch (structId) {
            case "arrays": {
                ArrayDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), ArrayDSState.class)
                        : arrayEngine.createInitial(List.of(10, 25, 40, 55, 70));
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = arrayEngine.executeOperation(state, op, request.getIndex(), val);
                break;
            }
            case "linked-lists": {
                LinkedListDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), LinkedListDSState.class)
                        : linkedListEngine.createInitial(List.of(15, 30, 45, 60), false);
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = linkedListEngine.executeOperation(state, op, val, request.getPosition());
                break;
            }
            case "stacks": {
                StackDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), StackDSState.class)
                        : stackEngine.createInitial(List.of(10, 20, 30), 8);
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = stackEngine.executeOperation(state, op, val);
                break;
            }
            case "queues": {
                QueueDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), QueueDSState.class)
                        : queueEngine.createInitial(List.of(12, 24, 36), 6, false);
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = queueEngine.executeOperation(state, op, val);
                break;
            }
            case "hash-tables": {
                HashTableDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), HashTableDSState.class)
                        : hashTableEngine.createInitial("chaining", 7, List.of(
                                new String[]{"apple", "100"},
                                new String[]{"banana", "200"},
                                new String[]{"cherry", "300"}
                          ));
                String v = request.getValue() != null ? String.valueOf(request.getValue()) : null;
                steps = hashTableEngine.executeOperation(state, op, request.getKey(), v);
                break;
            }
            case "binary-search-tree": {
                BSTDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), BSTDSState.class)
                        : bstEngine.createInitial(List.of(50, 30, 70, 20, 40, 60, 80));
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = bstEngine.executeOperation(state, op, val);
                break;
            }
            case "avl-trees": {
                AVLDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), AVLDSState.class)
                        : avlEngine.createInitial(List.of(30, 20, 40, 10, 25));
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = avlEngine.executeOperation(state, op, val);
                break;
            }
            case "binary-heaps": {
                HeapDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), HeapDSState.class)
                        : heapEngine.createInitial("max", List.of(85, 60, 70, 45, 50, 65, 30));
                Integer val = request.getValue() instanceof Number ? ((Number) request.getValue()).intValue() : null;
                steps = heapEngine.executeOperation(state, op, val);
                break;
            }
            case "tries": {
                TrieDSState state = (request.getState() != null)
                        ? objectMapper.convertValue(request.getState(), TrieDSState.class)
                        : trieEngine.createInitial(List.of("cat", "car", "cart", "dog"));
                String word = request.getWord() != null ? request.getWord()
                        : (request.getValue() != null ? String.valueOf(request.getValue()) : "cat");
                steps = trieEngine.executeOperation(state, op, word);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported data structure ID: " + structId);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("structureId", structId);
        summary.put("operation", op);
        summary.put("totalSteps", steps.size());

        return new ExecutionTraceResponse(structId, steps, summary);
    }

    public Object getInitialState(String structureId) {
        switch (structureId) {
            case "arrays":
                return arrayEngine.createInitial(List.of(10, 25, 40, 55, 70));
            case "linked-lists":
                return linkedListEngine.createInitial(List.of(15, 30, 45, 60), false);
            case "stacks":
                return stackEngine.createInitial(List.of(10, 20, 30), 8);
            case "queues":
                return queueEngine.createInitial(List.of(12, 24, 36), 6, false);
            case "hash-tables":
                return hashTableEngine.createInitial("chaining", 7, List.of(
                        new String[]{"apple", "100"},
                        new String[]{"banana", "200"},
                        new String[]{"cherry", "300"}
                ));
            case "binary-search-tree":
                return bstEngine.createInitial(List.of(50, 30, 70, 20, 40, 60, 80));
            case "avl-trees":
                return avlEngine.createInitial(List.of(30, 20, 40, 10, 25));
            case "binary-heaps":
                return heapEngine.createInitial("max", List.of(85, 60, 70, 45, 50, 65, 30));
            case "tries":
                return trieEngine.createInitial(List.of("cat", "car", "cart", "dog"));
            default:
                throw new IllegalArgumentException("Unsupported structure ID: " + structureId);
        }
    }
}
