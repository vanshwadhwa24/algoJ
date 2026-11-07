import { ExecutionStep } from '../../types/execution';

export interface HeapElement {
  value: number;
  state: 'default' | 'comparing' | 'swapping' | 'violation' | 'placed';
}

export interface HeapDSState {
  type: 'min' | 'max';
  array: HeapElement[];
  activeIndices: number[];
  comparingIndices: number[];
  message: string;
}

export function createInitialHeap(
  type: 'min' | 'max' = 'max',
  values: number[] = [85, 60, 70, 45, 50, 65, 30]
): HeapDSState {
  return {
    type,
    array: values.map((v) => ({ value: v, state: 'default' })),
    activeIndices: [],
    comparingIndices: [],
    message: `${type === 'min' ? 'Min' : 'Max'} Heap initialized with ${values.length} elements.`,
  };
}

export function cloneHeapDS(state: HeapDSState): HeapDSState {
  return {
    type: state.type,
    array: state.array.map((el) => ({ ...el })),
    activeIndices: [...state.activeIndices],
    comparingIndices: [...state.comparingIndices],
    message: state.message,
  };
}

export function executeHeapOperation(
  currentState: HeapDSState,
  operation: {
    type: 'insert' | 'extract' | 'peek' | 'build';
    value?: number;
    initialArray?: number[];
  }
): ExecutionStep<HeapDSState>[] {
  const steps: ExecutionStep<HeapDSState>[] = [];
  const state = cloneHeapDS(currentState);
  const isMin = state.type === 'min';

  if (operation.type === 'insert') {
    const val = operation.value ?? Math.floor(Math.random() * 90 + 10);
    const newIdx = state.array.length;
    state.array.push({ value: val, state: 'placed' });
    state.activeIndices = [newIdx];

    steps.push({
      stepIndex: 0,
      operationType: 'insert-end',
      description: `Appended ${val} at index ${newIdx} (end of array / leaf of tree). Now performing sift-up to repair heap property.`,
      codeLine: 2,
      state: cloneHeapDS(state),
    });

    let current = newIdx;
    while (current > 0) {
      const parent = Math.floor((current - 1) / 2);
      state.comparingIndices = [current, parent];
      state.array[current].state = 'comparing';
      state.array[parent].state = 'comparing';

      const violation = isMin
        ? state.array[current].value < state.array[parent].value
        : state.array[current].value > state.array[parent].value;

      steps.push({
        stepIndex: steps.length,
        operationType: 'compare-parent',
        description: `Comparing child at index ${current} (${state.array[current].value}) with parent at index ${parent} (${state.array[parent].value}).`,
        codeLine: 4,
        state: cloneHeapDS(state),
      });

      if (violation) {
        state.array[current].state = 'violation';
        state.array[parent].state = 'violation';

        steps.push({
          stepIndex: steps.length,
          operationType: 'heap-violation',
          description: `${isMin ? 'Min' : 'Max'}-heap property violated! Swapping child (${state.array[current].value}) and parent (${state.array[parent].value}).`,
          codeLine: 5,
          state: cloneHeapDS(state),
        });

        // Swap
        const temp = state.array[current];
        state.array[current] = state.array[parent];
        state.array[parent] = temp;
        current = parent;
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'heap-valid',
          description: `Heap property satisfied between child and parent. Sift-up complete!`,
          codeLine: 7,
          state: cloneHeapDS(state),
        });
        break;
      }
    }

    state.array.forEach((el) => (el.state = 'default'));
    state.activeIndices = [];
    state.comparingIndices = [];

    steps.push({
      stepIndex: steps.length,
      operationType: 'insert-complete',
      description: `Successfully inserted ${val} into ${isMin ? 'Min' : 'Max'} Heap. O(log n) time.`,
      codeLine: 8,
      state: cloneHeapDS(state),
    });
  } else if (operation.type === 'extract') {
    if (state.array.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'underflow',
        description: 'Cannot extract from an empty heap!',
        codeLine: 1,
        state: cloneHeapDS(state),
      });
      return steps;
    }

    const extremum = state.array[0].value;
    state.array[0].state = 'violation';

    steps.push({
      stepIndex: 0,
      operationType: 'extract-root',
      description: `Extracting root element ${extremum} (${isMin ? 'Minimum' : 'Maximum'}). Replacing root with last leaf element.`,
      codeLine: 2,
      state: cloneHeapDS(state),
    });

    if (state.array.length === 1) {
      state.array = [];
      steps.push({
        stepIndex: 1,
        operationType: 'extract-complete',
        description: `Extracted only element ${extremum}. Heap is now empty.`,
        codeLine: 3,
        state: cloneHeapDS(state),
      });
      return steps;
    }

    // Move last element to root
    const last = state.array.pop()!;
    state.array[0] = { ...last, state: 'placed' };

    steps.push({
      stepIndex: steps.length,
      operationType: 'moved-last-to-root',
      description: `Moved leaf element ${last.value} to root index 0. Now sifting down to restore heap property.`,
      codeLine: 4,
      state: cloneHeapDS(state),
    });

    // Sift down
    let curr = 0;
    const n = state.array.length;

    while (curr < n) {
      const left = 2 * curr + 1;
      const right = 2 * curr + 2;
      let target = curr;

      if (left < n) {
        if (
          isMin
            ? state.array[left].value < state.array[target].value
            : state.array[left].value > state.array[target].value
        ) {
          target = left;
        }
      }

      if (right < n) {
        if (
          isMin
            ? state.array[right].value < state.array[target].value
            : state.array[right].value > state.array[target].value
        ) {
          target = right;
        }
      }

      if (target !== curr) {
        state.comparingIndices = [curr, target];
        steps.push({
          stepIndex: steps.length,
          operationType: 'sift-down-swap',
          description: `Swapping index ${curr} (${state.array[curr].value}) with child at index ${target} (${state.array[target].value}).`,
          codeLine: 6,
          state: cloneHeapDS(state),
        });

        const temp = state.array[curr];
        state.array[curr] = state.array[target];
        state.array[target] = temp;
        curr = target;
      } else {
        break;
      }
    }

    state.array.forEach((el) => (el.state = 'default'));
    state.activeIndices = [];
    state.comparingIndices = [];

    steps.push({
      stepIndex: steps.length,
      operationType: 'extract-complete',
      description: `Extracted ${extremum}. Heap restored in O(log n) time.`,
      codeLine: 8,
      state: cloneHeapDS(state),
    });
  } else if (operation.type === 'peek') {
    if (state.array.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'peek-empty',
        description: 'Heap is empty. Peek returned null.',
        codeLine: 1,
        state: cloneHeapDS(state),
      });
      return steps;
    }

    state.array[0].state = 'placed';
    steps.push({
      stepIndex: 0,
      operationType: 'peek',
      description: `Peek root: ${isMin ? 'Minimum' : 'Maximum'} is ${state.array[0].value} at index 0. O(1) time.`,
      codeLine: 2,
      state: cloneHeapDS(state),
    });
  } else if (operation.type === 'build') {
    const raw = operation.initialArray ?? [40, 20, 60, 10, 50, 30, 80];
    state.array = raw.map((v) => ({ value: v, state: 'default' }));

    steps.push({
      stepIndex: 0,
      operationType: 'build-start',
      description: `Building ${isMin ? 'Min' : 'Max'} Heap from unordered array: [${raw.join(', ')}]. Bottom-up heapify takes O(n) linear time.`,
      codeLine: 1,
      state: cloneHeapDS(state),
    });

    const len = state.array.length;
    for (let i = Math.floor(len / 2) - 1; i >= 0; i--) {
      // sift down from i
      let c = i;
      while (c < len) {
        const left = 2 * c + 1;
        const right = 2 * c + 2;
        let t = c;

        if (left < len) {
          if (
            isMin
              ? state.array[left].value < state.array[t].value
              : state.array[left].value > state.array[t].value
          ) {
            t = left;
          }
        }
        if (right < len) {
          if (
            isMin
              ? state.array[right].value < state.array[t].value
              : state.array[right].value > state.array[t].value
          ) {
            t = right;
          }
        }

        if (t !== c) {
          const temp = state.array[c];
          state.array[c] = state.array[t];
          state.array[t] = temp;
          c = t;
        } else {
          break;
        }
      }
    }

    steps.push({
      stepIndex: 1,
      operationType: 'build-complete',
      description: `Heapify finished. Heap property satisfied for all subtrees. Root is ${state.array[0]?.value}.`,
      codeLine: 5,
      state: cloneHeapDS(state),
    });
  }

  return steps;
}
