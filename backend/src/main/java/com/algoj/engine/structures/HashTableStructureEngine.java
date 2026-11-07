package com.algoj.engine.structures;

import com.algoj.dto.ExecutionStep;
import com.algoj.model.LinearStructureModel.HashBucket;
import com.algoj.model.LinearStructureModel.HashEntry;
import com.algoj.model.LinearStructureModel.HashTableDSState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HashTableStructureEngine {

    public int computeHash(String key, int capacity) {
        long hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * 31 + key.charAt(i)) & 0xFFFFFFFFL;
        }
        return (int) (hash % capacity);
    }

    public HashTableDSState createInitial(String strategy, int capacity, List<String[]> initialPairs) {
        HashTableDSState state = new HashTableDSState();
        state.setCollisionStrategy(strategy != null ? strategy : "chaining");
        state.setCapacity(capacity > 0 ? capacity : 7);

        List<HashBucket> buckets = new ArrayList<>();
        for (int i = 0; i < state.getCapacity(); i++) {
            buckets.add(new HashBucket(i, new ArrayList<>(), "default"));
        }
        state.setBuckets(buckets);

        if (initialPairs != null) {
            for (String[] pair : initialPairs) {
                if (pair.length >= 2) {
                    insertInternal(state, pair[0], pair[1]);
                }
            }
        }
        state.setMessage(String.format("Hash Table initialized (%s strategy, capacity %d).",
                state.getCollisionStrategy(), state.getCapacity()));
        return state;
    }

    private void insertInternal(HashTableDSState state, String key, String value) {
        int hash = computeHash(key, state.getCapacity());
        if ("chaining".equalsIgnoreCase(state.getCollisionStrategy())) {
            HashBucket bucket = state.getBuckets().get(hash);
            for (HashEntry e : bucket.getEntries()) {
                if (e.getKey().equals(key)) {
                    e.setValue(value);
                    return;
                }
            }
            bucket.getEntries().add(new HashEntry(key, value));
        } else {
            // Probing
            for (int i = 0; i < state.getCapacity(); i++) {
                int idx = (hash + i) % state.getCapacity();
                HashBucket bucket = state.getBuckets().get(idx);
                if (bucket.getEntries().isEmpty() || Boolean.TRUE.equals(bucket.getEntries().get(0).getIsDeleted())) {
                    bucket.getEntries().clear();
                    bucket.getEntries().add(new HashEntry(key, value, false));
                    return;
                } else if (bucket.getEntries().get(0).getKey().equals(key)) {
                    bucket.getEntries().get(0).setValue(value);
                    return;
                }
            }
        }
    }

    public List<ExecutionStep> executeOperation(HashTableDSState currentState, String type, String key, String value) {
        List<ExecutionStep> steps = new ArrayList<>();
        HashTableDSState state = currentState.copy();
        String k = (key != null && !key.trim().isEmpty()) ? key.trim() : "grape";
        String v = (value != null && !value.trim().isEmpty()) ? value.trim() : "500";

        int hash = computeHash(k, state.getCapacity());
        state.setActiveHash(hash);

        steps.add(new ExecutionStep(
                steps.size(), "hash-calc",
                String.format("Computed hash for key '%s': hash = %d (mod %d).", k, hash, state.getCapacity()),
                2, state.copy()
        ));

        if ("chaining".equalsIgnoreCase(state.getCollisionStrategy())) {
            // SEPARATE CHAINING
            HashBucket bucket = state.getBuckets().get(hash);
            bucket.setState("active");

            if ("insert".equalsIgnoreCase(type)) {
                boolean keyExists = false;
                for (HashEntry entry : bucket.getEntries()) {
                    if (entry.getKey().equals(k)) {
                        entry.setValue(v);
                        keyExists = true;
                        state.setMessage(String.format("Key '%s' already exists in bucket %d. Updated value to '%s'.", k, hash, v));
                        steps.add(new ExecutionStep(steps.size(), "update", state.getMessage(), 4, state.copy()));
                        break;
                    }
                }
                if (!keyExists) {
                    if (!bucket.getEntries().isEmpty()) {
                        bucket.setState("collision");
                        steps.add(new ExecutionStep(
                                steps.size(), "collision",
                                String.format("Collision at bucket %d! Bucket contains %d existing entries. Chaining new node to linked list.",
                                        hash, bucket.getEntries().size()),
                                5, state.copy()
                        ));
                    }
                    bucket.getEntries().add(new HashEntry(k, v));
                    bucket.setState("found");
                    state.setMessage(String.format("Inserted entry ('%s', '%s') into bucket %d.", k, v, hash));
                    steps.add(new ExecutionStep(steps.size(), "insert", state.getMessage(), 6, state.copy()));
                }

            } else if ("search".equalsIgnoreCase(type)) {
                boolean found = false;
                for (HashEntry entry : bucket.getEntries()) {
                    if (entry.getKey().equals(k)) {
                        found = true;
                        bucket.setState("found");
                        state.setMessage(String.format("Found entry for key '%s': value = '%s' in bucket %d.", k, entry.getValue(), hash));
                        steps.add(new ExecutionStep(steps.size(), "found", state.getMessage(), 4, state.copy()));
                        break;
                    }
                }
                if (!found) {
                    state.setMessage(String.format("Key '%s' not found in bucket %d.", k, hash));
                    steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 6, state.copy()));
                }

            } else if ("delete".equalsIgnoreCase(type)) {
                boolean removed = bucket.getEntries().removeIf(e -> e.getKey().equals(k));
                if (removed) {
                    bucket.setState("found");
                    state.setMessage(String.format("Deleted entry with key '%s' from bucket %d.", k, hash));
                    steps.add(new ExecutionStep(steps.size(), "delete", state.getMessage(), 4, state.copy()));
                } else {
                    state.setMessage(String.format("Key '%s' not found in bucket %d to delete.", k, hash));
                    steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 5, state.copy()));
                }
            }

        } else {
            // LINEAR PROBING
            if ("insert".equalsIgnoreCase(type)) {
                boolean placed = false;
                for (int probe = 0; probe < state.getCapacity(); probe++) {
                    int idx = (hash + probe) % state.getCapacity();
                    HashBucket bucket = state.getBuckets().get(idx);
                    bucket.setState("probed");

                    steps.add(new ExecutionStep(
                            steps.size(), "probe",
                            String.format("Probing index %d (probe count: %d)...", idx, probe),
                            4, state.copy()
                    ));

                    if (bucket.getEntries().isEmpty() || Boolean.TRUE.equals(bucket.getEntries().get(0).getIsDeleted())) {
                        bucket.getEntries().clear();
                        bucket.getEntries().add(new HashEntry(k, v, false));
                        bucket.setState("found");
                        state.setMessage(String.format("Placed ('%s', '%s') at index %d after %d probes.", k, v, idx, probe));
                        steps.add(new ExecutionStep(steps.size(), "insert", state.getMessage(), 6, state.copy()));
                        placed = true;
                        break;
                    } else if (bucket.getEntries().get(0).getKey().equals(k)) {
                        bucket.getEntries().get(0).setValue(v);
                        bucket.setState("found");
                        state.setMessage(String.format("Key '%s' already exists at index %d. Updated value to '%s'.", k, idx, v));
                        steps.add(new ExecutionStep(steps.size(), "update", state.getMessage(), 5, state.copy()));
                        placed = true;
                        break;
                    }
                    bucket.setState("collision");
                }
                if (!placed) {
                    state.setMessage("Hash table is full! Linear probing failed to find free slot.");
                    steps.add(new ExecutionStep(steps.size(), "overflow", state.getMessage(), 8, state.copy()));
                }

            } else if ("search".equalsIgnoreCase(type)) {
                boolean found = false;
                for (int probe = 0; probe < state.getCapacity(); probe++) {
                    int idx = (hash + probe) % state.getCapacity();
                    HashBucket bucket = state.getBuckets().get(idx);
                    bucket.setState("probed");

                    steps.add(new ExecutionStep(
                            steps.size(), "probe",
                            String.format("Probing index %d for key '%s'...", idx, k),
                            3, state.copy()
                    ));

                    if (bucket.getEntries().isEmpty()) {
                        // Empty slot terminates linear probing
                        break;
                    }

                    HashEntry entry = bucket.getEntries().get(0);
                    if (!Boolean.TRUE.equals(entry.getIsDeleted()) && entry.getKey().equals(k)) {
                        found = true;
                        bucket.setState("found");
                        state.setMessage(String.format("Found key '%s' at index %d with value '%s'.", k, idx, entry.getValue()));
                        steps.add(new ExecutionStep(steps.size(), "found", state.getMessage(), 5, state.copy()));
                        break;
                    }
                }
                if (!found) {
                    state.setMessage(String.format("Key '%s' not found after linear probe search.", k));
                    steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 7, state.copy()));
                }

            } else if ("delete".equalsIgnoreCase(type)) {
                boolean found = false;
                for (int probe = 0; probe < state.getCapacity(); probe++) {
                    int idx = (hash + probe) % state.getCapacity();
                    HashBucket bucket = state.getBuckets().get(idx);
                    if (bucket.getEntries().isEmpty()) break;

                    HashEntry entry = bucket.getEntries().get(0);
                    if (!Boolean.TRUE.equals(entry.getIsDeleted()) && entry.getKey().equals(k)) {
                        entry.setIsDeleted(true);
                        bucket.setState("found");
                        found = true;
                        state.setMessage(String.format("Deleted key '%s' at index %d. Placed tombstone for search continuity.", k, idx));
                        steps.add(new ExecutionStep(steps.size(), "delete", state.getMessage(), 4, state.copy()));
                        break;
                    }
                }
                if (!found) {
                    state.setMessage(String.format("Key '%s' not found to delete.", k));
                    steps.add(new ExecutionStep(steps.size(), "not-found", state.getMessage(), 6, state.copy()));
                }
            }
        }

        // Clean up bucket highlights for final state
        for (HashBucket b : state.getBuckets()) {
            b.setState("default");
        }

        return steps;
    }
}
