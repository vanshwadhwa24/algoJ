import { ExecutionStep } from '../../types/execution';

export interface StackItem {
  id: string;
  value: number;
  highlight?: 'default' | 'top' | 'pushing' | 'popping';
}

export interface StackDSState {
  items: StackItem[];
  maxCapacity: number;
  message: string;
}

export function createInitialStack(initialValues: number[] = [10, 20, 30], capacity: number = 8): StackDSState {
  return {
    items: initialValues.map((val, idx) => ({
      id: `stack-${idx}-${val}`,
      value: val,
      highlight: idx === initialValues.length - 1 ? 'top' : 'default',
    })),
    maxCapacity: capacity,
    message: 'Stack initialized with capacity ' + capacity + '.',
  };
}

export function cloneStackDS(state: StackDSState): StackDSState {
  return {
    items: state.items.map((it) => ({ ...it })),
    maxCapacity: state.maxCapacity,
    message: state.message,
  };
}

export function executeStackOperation(
  currentState: StackDSState,
  operation: {
    type: 'push' | 'pop' | 'peek' | 'isEmpty';
    value?: number;
  }
): ExecutionStep<StackDSState>[] {
  const steps: ExecutionStep<StackDSState>[] = [];
  const state = cloneStackDS(currentState);

  if (operation.type === 'push') {
    const val = operation.value ?? Math.floor(Math.random() * 90 + 10);
    if (state.items.length >= state.maxCapacity) {
      steps.push({
        stepIndex: 0,
        operationType: 'overflow',
        description: `Stack Overflow! Cannot push ${val}. Maximum capacity of ${state.maxCapacity} reached.`,
        codeLine: 2,
        state: cloneStackDS(state),
      });
      return steps;
    }

    const newItem: StackItem = {
      id: `stack-${Date.now()}-${val}`,
      value: val,
      highlight: 'pushing',
    };

    steps.push({
      stepIndex: 0,
      operationType: 'push-start',
      description: `Pushing ${val} onto top of stack. Incrementing top pointer to index ${state.items.length}.`,
      codeLine: 3,
      state: {
        ...cloneStackDS(state),
        items: [...state.items.map((it) => ({ ...it, highlight: 'default' as const })), newItem],
      },
    });

    const finalItems = [
      ...state.items.map((it) => ({ ...it, highlight: 'default' as const })),
      { ...newItem, highlight: 'top' as const },
    ];

    steps.push({
      stepIndex: 1,
      operationType: 'pushed',
      description: `Successfully pushed ${val}. TOP now points to ${val}. Current size: ${finalItems.length}/${state.maxCapacity}. O(1) time.`,
      codeLine: 4,
      state: {
        ...state,
        items: finalItems,
      },
      counters: { size: finalItems.length },
    });
  } else if (operation.type === 'pop') {
    if (state.items.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'underflow',
        description: 'Stack Underflow! Cannot pop from empty stack.',
        codeLine: 2,
        state: cloneStackDS(state),
      });
      return steps;
    }

    const popped = state.items[state.items.length - 1];
    popped.highlight = 'popping';

    steps.push({
      stepIndex: 0,
      operationType: 'pop-start',
      description: `Popping top item (${popped.value}) from stack.`,
      codeLine: 3,
      state: cloneStackDS(state),
    });

    const remaining = state.items.slice(0, state.items.length - 1);
    if (remaining.length > 0) {
      remaining[remaining.length - 1].highlight = 'top';
    }

    steps.push({
      stepIndex: 1,
      operationType: 'popped',
      description: `Popped ${popped.value}. New top is ${remaining.length > 0 ? remaining[remaining.length - 1].value : 'None (Stack is Empty)'}. O(1) time.`,
      codeLine: 5,
      state: {
        ...state,
        items: remaining,
      },
      counters: { size: remaining.length },
    });
  } else if (operation.type === 'peek') {
    if (state.items.length === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'peek-empty',
        description: 'Stack is empty. Peek returns undefined / null.',
        codeLine: 2,
        state: cloneStackDS(state),
      });
      return steps;
    }

    const topItem = state.items[state.items.length - 1];
    topItem.highlight = 'top';

    steps.push({
      stepIndex: 0,
      operationType: 'peek',
      description: `Peeked top element: ${topItem.value}. Stack remains unmodified. O(1) time.`,
      codeLine: 3,
      state: cloneStackDS(state),
      counters: { size: state.items.length },
    });
  } else if (operation.type === 'isEmpty') {
    const empty = state.items.length === 0;
    steps.push({
      stepIndex: 0,
      operationType: 'is-empty',
      description: `isEmpty check: ${empty ? 'TRUE (size == 0)' : `FALSE (current size = ${state.items.length})`}.`,
      codeLine: 1,
      state: cloneStackDS(state),
      counters: { size: state.items.length },
    });
  }

  return steps;
}
