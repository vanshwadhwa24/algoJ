import { ExecutionStep } from '../../types/execution';

export interface ArrayCell {
  index: number;
  value: number;
  state: 'default' | 'active' | 'inserted' | 'deleted' | 'shifted';
}

export interface ArrayDSState {
  cells: ArrayCell[];
  activeIndex: number | null;
  operationCount: number;
  message: string;
}

export function createInitialArrayDS(values: number[]): ArrayDSState {
  return {
    cells: values.map((val, idx) => ({ index: idx, value: val, state: 'default' })),
    activeIndex: null,
    operationCount: 0,
    message: 'Array initialized.',
  };
}

export function cloneArrayDS(state: ArrayDSState): ArrayDSState {
  return {
    cells: state.cells.map((c) => ({ ...c })),
    activeIndex: state.activeIndex,
    operationCount: state.operationCount,
    message: state.message,
  };
}

export function executeArrayOperation(
  currentState: ArrayDSState,
  operation: {
    type: 'access' | 'update' | 'insert' | 'delete' | 'search' | 'traverse';
    index?: number;
    value?: number;
  }
): ExecutionStep<ArrayDSState>[] {
  const steps: ExecutionStep<ArrayDSState>[] = [];
  const state = cloneArrayDS(currentState);
  const currentValues = state.cells.map((c) => c.value);

  if (operation.type === 'access') {
    const idx = operation.index ?? 0;
    if (idx < 0 || idx >= state.cells.length) {
      steps.push({
        stepIndex: 0,
        operationType: 'error',
        description: `Index ${idx} is out of bounds! Array bounds: 0 to ${state.cells.length - 1}.`,
        codeLine: 2,
        state: cloneArrayDS(state),
      });
      return steps;
    }
    state.activeIndex = idx;
    state.cells[idx].state = 'active';
    state.operationCount++;
    steps.push({
      stepIndex: 0,
      operationType: 'access',
      description: `Accessed element at index ${idx}: value is ${state.cells[idx].value}. Direct O(1) memory lookup.`,
      codeLine: 3,
      state: cloneArrayDS(state),
      counters: { operations: state.operationCount },
    });
  } else if (operation.type === 'update') {
    const idx = operation.index ?? 0;
    const val = operation.value ?? 0;
    if (idx < 0 || idx >= state.cells.length) {
      steps.push({
        stepIndex: 0,
        operationType: 'error',
        description: `Index ${idx} is out of bounds!`,
        codeLine: 2,
        state: cloneArrayDS(state),
      });
      return steps;
    }
    const oldVal = state.cells[idx].value;
    state.activeIndex = idx;
    state.cells[idx].value = val;
    state.cells[idx].state = 'active';
    state.operationCount++;
    steps.push({
      stepIndex: 0,
      operationType: 'update',
      description: `Updated array[${idx}] from ${oldVal} to ${val}. O(1) time complexity.`,
      codeLine: 4,
      state: cloneArrayDS(state),
      counters: { operations: state.operationCount },
    });
  } else if (operation.type === 'insert') {
    const idx = Math.max(0, Math.min(state.cells.length, operation.index ?? state.cells.length));
    const val = operation.value ?? 0;

    steps.push({
      stepIndex: 0,
      operationType: 'insert-start',
      description: `Starting insertion of value ${val} at index ${idx}. Must shift elements at [${idx}..${state.cells.length - 1}] to the right.`,
      codeLine: 2,
      state: cloneArrayDS(state),
    });

    const newValues = [...currentValues];
    newValues.splice(idx, 0, val);
    const updatedState = createInitialArrayDS(newValues);
    updatedState.activeIndex = idx;
    updatedState.cells[idx].state = 'inserted';
    updatedState.operationCount = state.operationCount + (state.cells.length - idx) + 1;

    steps.push({
      stepIndex: 1,
      operationType: 'inserted',
      description: `Inserted ${val} at index ${idx}. Shifted ${state.cells.length - idx} elements. O(n) worst-case time complexity.`,
      codeLine: 5,
      state: cloneArrayDS(updatedState),
      counters: { operations: updatedState.operationCount },
    });
  } else if (operation.type === 'delete') {
    const idx = operation.index ?? 0;
    if (idx < 0 || idx >= state.cells.length) {
      steps.push({
        stepIndex: 0,
        operationType: 'error',
        description: `Index ${idx} is out of bounds!`,
        codeLine: 2,
        state: cloneArrayDS(state),
      });
      return steps;
    }

    const val = state.cells[idx].value;
    state.cells[idx].state = 'deleted';
    state.activeIndex = idx;
    steps.push({
      stepIndex: 0,
      operationType: 'delete-mark',
      description: `Deleting element ${val} at index ${idx}. Elements to the right will shift left.`,
      codeLine: 2,
      state: cloneArrayDS(state),
    });

    const newValues = currentValues.filter((_, i) => i !== idx);
    const updatedState = createInitialArrayDS(newValues);
    updatedState.activeIndex = null;
    updatedState.operationCount = state.operationCount + (state.cells.length - 1 - idx) + 1;

    steps.push({
      stepIndex: 1,
      operationType: 'delete-complete',
      description: `Deleted element ${val}. Shifted remaining elements left. O(n) worst-case time complexity.`,
      codeLine: 5,
      state: cloneArrayDS(updatedState),
      counters: { operations: updatedState.operationCount },
    });
  } else if (operation.type === 'search') {
    const target = operation.value ?? 0;
    let found = false;

    steps.push({
      stepIndex: 0,
      operationType: 'search-start',
      description: `Searching for value ${target} in array of ${state.cells.length} elements.`,
      codeLine: 1,
      state: cloneArrayDS(state),
    });

    for (let i = 0; i < state.cells.length; i++) {
      const stepState = cloneArrayDS(state);
      stepState.activeIndex = i;
      stepState.cells[i].state = 'active';
      stepState.operationCount++;

      if (stepState.cells[i].value === target) {
        stepState.cells[i].state = 'inserted'; // highlight found
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-found',
          description: `Found target ${target} at index ${i}!`,
          codeLine: 4,
          state: stepState,
          counters: { operations: stepState.operationCount },
        });
        found = true;
        break;
      } else {
        steps.push({
          stepIndex: steps.length,
          operationType: 'search-step',
          description: `Checked index ${i} (${stepState.cells[i].value} != ${target}). Continuing...`,
          codeLine: 3,
          state: stepState,
          counters: { operations: stepState.operationCount },
        });
      }
    }

    if (!found) {
      const stepState = cloneArrayDS(state);
      steps.push({
        stepIndex: steps.length,
        operationType: 'search-not-found',
        description: `Target ${target} was not found in the array. O(n) linear search completed.`,
        codeLine: 6,
        state: stepState,
      });
    }
  } else if (operation.type === 'traverse') {
    steps.push({
      stepIndex: 0,
      operationType: 'traverse-start',
      description: `Traversing all elements in array from index 0 to ${state.cells.length - 1}.`,
      codeLine: 1,
      state: cloneArrayDS(state),
    });

    for (let i = 0; i < state.cells.length; i++) {
      const stepState = cloneArrayDS(state);
      stepState.activeIndex = i;
      stepState.cells[i].state = 'active';
      steps.push({
        stepIndex: steps.length,
        operationType: 'traverse-step',
        description: `Visiting index ${i}: value = ${state.cells[i].value}.`,
        codeLine: 3,
        state: stepState,
      });
    }
  }

  return steps;
}
