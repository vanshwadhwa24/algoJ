import { ExecutionStep } from '../../types/execution';

export interface HashEntry {
  key: string;
  value: string;
  isDeleted?: boolean; // tombstone for linear probing
}

export interface HashBucket {
  index: number;
  entries: HashEntry[];
  state: 'default' | 'active' | 'collision' | 'found' | 'probed';
}

export interface HashTableDSState {
  collisionStrategy: 'chaining' | 'probing';
  capacity: number;
  buckets: HashBucket[];
  activeHash: number | null;
  message: string;
}

export function computeHash(key: string, capacity: number): number {
  let hash = 0;
  for (let i = 0; i < key.length; i++) {
    hash = (hash * 31 + key.charCodeAt(i)) >>> 0;
  }
  return hash % capacity;
}

export function createInitialHashTable(
  strategy: 'chaining' | 'probing' = 'chaining',
  capacity: number = 7,
  initialData: [string, string][] = [
    ['apple', '100'],
    ['banana', '200'],
    ['cherry', '300'],
  ]
): HashTableDSState {
  const buckets: HashBucket[] = Array.from({ length: capacity }, (_, i) => ({
    index: i,
    entries: [],
    state: 'default',
  }));

  const state: HashTableDSState = {
    collisionStrategy: strategy,
    capacity,
    buckets,
    activeHash: null,
    message: `Hash Table initialized with ${strategy} strategy (capacity ${capacity}).`,
  };

  initialData.forEach(([k, v]) => {
    const h = computeHash(k, capacity);
    if (strategy === 'chaining') {
      state.buckets[h].entries.push({ key: k, value: v });
    } else {
      let probe = h;
      while (
        state.buckets[probe].entries.length > 0 &&
        !state.buckets[probe].entries[0].isDeleted
      ) {
        probe = (probe + 1) % capacity;
      }
      state.buckets[probe].entries = [{ key: k, value: v }];
    }
  });

  return state;
}

export function cloneHashTableDS(state: HashTableDSState): HashTableDSState {
  return {
    collisionStrategy: state.collisionStrategy,
    capacity: state.capacity,
    buckets: state.buckets.map((b) => ({
      index: b.index,
      state: b.state,
      entries: b.entries.map((e) => ({ ...e })),
    })),
    activeHash: state.activeHash,
    message: state.message,
  };
}

export function executeHashTableOperation(
  currentState: HashTableDSState,
  operation: {
    type: 'insert' | 'search' | 'delete';
    key: string;
    value?: string;
  }
): ExecutionStep<HashTableDSState>[] {
  const steps: ExecutionStep<HashTableDSState>[] = [];
  const state = cloneHashTableDS(currentState);
  const key = operation.key.trim();
  const value = operation.value?.trim() || 'val';

  if (!key) {
    steps.push({
      stepIndex: 0,
      operationType: 'error',
      description: 'Key cannot be empty!',
      codeLine: 1,
      state: cloneHashTableDS(state),
    });
    return steps;
  }

  const initialHash = computeHash(key, state.capacity);

  if (operation.type === 'insert') {
    state.activeHash = initialHash;
    state.buckets[initialHash].state = 'active';

    steps.push({
      stepIndex: 0,
      operationType: 'compute-hash',
      description: `Computed hash("${key}") % ${state.capacity} = ${initialHash}. Checking bucket ${initialHash}.`,
      codeLine: 2,
      state: cloneHashTableDS(state),
    });

    if (state.collisionStrategy === 'chaining') {
      const bucket = state.buckets[initialHash];
      const existing = bucket.entries.find((e) => e.key === key);

      if (existing) {
        existing.value = value;
        bucket.state = 'found';
        steps.push({
          stepIndex: 1,
          operationType: 'updated',
          description: `Key "${key}" already exists in bucket ${initialHash}. Updated value to "${value}".`,
          codeLine: 4,
          state: cloneHashTableDS(state),
        });
      } else {
        const hadCollision = bucket.entries.length > 0;
        bucket.entries.push({ key, value });
        bucket.state = hadCollision ? 'collision' : 'found';

        steps.push({
          stepIndex: 1,
          operationType: 'inserted',
          description: hadCollision
            ? `Collision at bucket ${initialHash}! Appended ("${key}", "${value}") to chain. Chain length is now ${bucket.entries.length}.`
            : `Inserted ("${key}", "${value}") cleanly into empty bucket ${initialHash}.`,
          codeLine: 5,
          state: cloneHashTableDS(state),
        });
      }
    } else {
      // Linear probing
      let probe = initialHash;
      let placed = false;
      let attempts = 0;

      while (attempts < state.capacity) {
        const currentBucket = state.buckets[probe];
        if (
          currentBucket.entries.length === 0 ||
          currentBucket.entries[0].isDeleted ||
          currentBucket.entries[0].key === key
        ) {
          currentBucket.entries = [{ key, value, isDeleted: false }];
          currentBucket.state = 'found';
          placed = true;

          steps.push({
            stepIndex: steps.length,
            operationType: 'probe-placed',
            description:
              attempts === 0
                ? `Inserted ("${key}", "${value}") at initial bucket ${probe}.`
                : `Collision resolved! Placed at probe index ${probe} after ${attempts} step(s) of linear probing.`,
            codeLine: 6,
            state: cloneHashTableDS(state),
          });
          break;
        } else {
          currentBucket.state = 'collision';
          steps.push({
            stepIndex: steps.length,
            operationType: 'probe-collision',
            description: `Bucket ${probe} already occupied by key "${currentBucket.entries[0].key}". Probing next index (${probe} + 1) % ${state.capacity}.`,
            codeLine: 7,
            state: cloneHashTableDS(state),
          });
          probe = (probe + 1) % state.capacity;
          attempts++;
        }
      }

      if (!placed) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'error',
          description: 'Hash table is full! Linear probing failed to find an empty slot.',
          codeLine: 8,
          state: cloneHashTableDS(state),
        });
      }
    }
  } else if (operation.type === 'search') {
    state.activeHash = initialHash;
    state.buckets[initialHash].state = 'active';

    steps.push({
      stepIndex: 0,
      operationType: 'compute-hash',
      description: `Searching for key "${key}". Hash index is ${initialHash}.`,
      codeLine: 2,
      state: cloneHashTableDS(state),
    });

    if (state.collisionStrategy === 'chaining') {
      const bucket = state.buckets[initialHash];
      const match = bucket.entries.find((e) => e.key === key);

      if (match) {
        bucket.state = 'found';
        steps.push({
          stepIndex: 1,
          operationType: 'search-success',
          description: `Key "${key}" found in bucket ${initialHash}! Value: "${match.value}".`,
          codeLine: 4,
          state: cloneHashTableDS(state),
        });
      } else {
        steps.push({
          stepIndex: 1,
          operationType: 'search-fail',
          description: `Key "${key}" not found in chain at bucket ${initialHash}.`,
          codeLine: 6,
          state: cloneHashTableDS(state),
        });
      }
    } else {
      let probe = initialHash;
      let found = false;
      let attempts = 0;

      while (attempts < state.capacity) {
        const currentBucket = state.buckets[probe];
        if (currentBucket.entries.length === 0) {
          // Empty slot without tombstone ends probe sequence
          break;
        }

        if (
          !currentBucket.entries[0].isDeleted &&
          currentBucket.entries[0].key === key
        ) {
          currentBucket.state = 'found';
          found = true;
          steps.push({
            stepIndex: steps.length,
            operationType: 'search-success',
            description: `Found key "${key}" at probe index ${probe}! Value: "${currentBucket.entries[0].value}".`,
            codeLine: 5,
            state: cloneHashTableDS(state),
          });
          break;
        }

        currentBucket.state = 'probed';
        steps.push({
          stepIndex: steps.length,
          operationType: 'probe-skip',
          description: `Bucket ${probe} has ${currentBucket.entries[0].isDeleted ? 'tombstone [DELETED]' : `key "${currentBucket.entries[0].key}"`}. Continuing probe...`,
          codeLine: 7,
          state: cloneHashTableDS(state),
        });

        probe = (probe + 1) % state.capacity;
        attempts++;
      }

      if (!found) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-fail',
          description: `Key "${key}" not found after examining probe sequence.`,
          codeLine: 8,
          state: cloneHashTableDS(state),
        });
      }
    }
  } else if (operation.type === 'delete') {
    state.activeHash = initialHash;
    state.buckets[initialHash].state = 'active';

    steps.push({
      stepIndex: 0,
      operationType: 'compute-hash',
      description: `Deleting key "${key}". Hash index is ${initialHash}.`,
      codeLine: 2,
      state: cloneHashTableDS(state),
    });

    if (state.collisionStrategy === 'chaining') {
      const bucket = state.buckets[initialHash];
      const initialLength = bucket.entries.length;
      bucket.entries = bucket.entries.filter((e) => e.key !== key);

      if (bucket.entries.length < initialLength) {
        bucket.state = 'found';
        steps.push({
          stepIndex: 1,
          operationType: 'deleted',
          description: `Successfully deleted key "${key}" from bucket ${initialHash}.`,
          codeLine: 4,
          state: cloneHashTableDS(state),
        });
      } else {
        steps.push({
          stepIndex: 1,
          operationType: 'delete-fail',
          description: `Key "${key}" does not exist in hash table. Nothing removed.`,
          codeLine: 6,
          state: cloneHashTableDS(state),
        });
      }
    } else {
      let probe = initialHash;
      let deleted = false;
      let attempts = 0;

      while (attempts < state.capacity) {
        const currentBucket = state.buckets[probe];
        if (currentBucket.entries.length === 0) break;

        if (
          !currentBucket.entries[0].isDeleted &&
          currentBucket.entries[0].key === key
        ) {
          currentBucket.entries[0].isDeleted = true; // tombstone
          currentBucket.state = 'found';
          deleted = true;

          steps.push({
            stepIndex: steps.length,
            operationType: 'deleted-tombstone',
            description: `Deleted key "${key}" at index ${probe}. Marked as TOMBSTONE to preserve probe chain continuity.`,
            codeLine: 5,
            state: cloneHashTableDS(state),
          });
          break;
        }

        probe = (probe + 1) % state.capacity;
        attempts++;
      }

      if (!deleted) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'delete-fail',
          description: `Key "${key}" was not found to delete.`,
          codeLine: 7,
          state: cloneHashTableDS(state),
        });
      }
    }
  }

  return steps;
}
