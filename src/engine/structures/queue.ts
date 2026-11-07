import { ExecutionStep } from '../../types/execution';

export interface QueueSlot {
  index: number;
  value: number | null;
  state: 'empty' | 'occupied' | 'front' | 'rear' | 'enqueueing' | 'dequeueing';
}

export interface QueueDSState {
  isCircular: boolean;
  capacity: number;
  slots: QueueSlot[];
  front: number; // index of front
  rear: number; // index of rear
  size: number;
  message: string;
}

export function createInitialQueue(
  initialValues: number[] = [12, 24, 36],
  capacity: number = 6,
  isCircular: boolean = false
): QueueDSState {
  const slots: QueueSlot[] = Array.from({ length: capacity }, (_, i) => ({
    index: i,
    value: i < initialValues.length ? initialValues[i] : null,
    state: i < initialValues.length ? 'occupied' : 'empty',
  }));

  const size = initialValues.length;
  const front = size > 0 ? 0 : -1;
  const rear = size > 0 ? size - 1 : -1;

  if (size > 0) {
    if (slots[front]) slots[front].state = 'front';
    if (slots[rear]) slots[rear].state = rear === front ? 'front' : 'rear';
  }

  return {
    isCircular,
    capacity,
    slots,
    front,
    rear,
    size,
    message: `${isCircular ? 'Circular' : 'Standard'} Queue initialized with capacity ${capacity}.`,
  };
}

export function cloneQueueDS(state: QueueDSState): QueueDSState {
  return {
    isCircular: state.isCircular,
    capacity: state.capacity,
    slots: state.slots.map((s) => ({ ...s })),
    front: state.front,
    rear: state.rear,
    size: state.size,
    message: state.message,
  };
}

export function executeQueueOperation(
  currentState: QueueDSState,
  operation: {
    type: 'enqueue' | 'dequeue' | 'peekFront' | 'inspectRear' | 'isEmpty' | 'isFull';
    value?: number;
  }
): ExecutionStep<QueueDSState>[] {
  const steps: ExecutionStep<QueueDSState>[] = [];
  const state = cloneQueueDS(currentState);

  if (operation.type === 'enqueue') {
    const val = operation.value ?? Math.floor(Math.random() * 90 + 10);
    if (state.size >= state.capacity) {
      steps.push({
        stepIndex: 0,
        operationType: 'overflow',
        description: `Queue Overflow! Cannot enqueue ${val}. Queue is full (${state.size}/${state.capacity}).`,
        codeLine: 2,
        state: cloneQueueDS(state),
      });
      return steps;
    }

    let nextRear = (state.rear + 1) % state.capacity;
    if (state.size === 0) {
      state.front = 0;
      nextRear = 0;
    }

    state.slots[nextRear].value = val;
    state.slots[nextRear].state = 'enqueueing';
    state.rear = nextRear;
    state.size++;

    const isWrapped = state.isCircular && nextRear < state.front && state.size > 1;

    steps.push({
      stepIndex: 0,
      operationType: 'enqueue-start',
      description: state.isCircular
        ? `Enqueued ${val} at circular slot (${(state.rear - 1 + state.capacity) % state.capacity} + 1) % ${state.capacity} = ${nextRear}.${isWrapped ? ' [Wraparound occurred]' : ''}`
        : `Enqueued ${val} at rear index ${nextRear}.`,
      codeLine: 4,
      state: cloneQueueDS(state),
      counters: { size: state.size, front: state.front, rear: state.rear },
    });

    state.slots.forEach((s, idx) => {
      if (s.value === null) s.state = 'empty';
      else if (idx === state.front) s.state = 'front';
      else if (idx === state.rear) s.state = 'rear';
      else s.state = 'occupied';
    });

    steps.push({
      stepIndex: 1,
      operationType: 'enqueued',
      description: `Enqueue complete. Size is now ${state.size}/${state.capacity}. O(1) time.`,
      codeLine: 5,
      state: cloneQueueDS(state),
      counters: { size: state.size, front: state.front, rear: state.rear },
    });
  } else if (operation.type === 'dequeue') {
    if (state.size === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'underflow',
        description: 'Queue Underflow! Cannot dequeue from empty queue.',
        codeLine: 2,
        state: cloneQueueDS(state),
      });
      return steps;
    }

    const removedVal = state.slots[state.front].value;
    state.slots[state.front].state = 'dequeueing';

    steps.push({
      stepIndex: 0,
      operationType: 'dequeue-start',
      description: `Dequeuing value ${removedVal} from front index ${state.front}.`,
      codeLine: 3,
      state: cloneQueueDS(state),
      counters: { size: state.size, front: state.front, rear: state.rear },
    });

    state.slots[state.front].value = null;
    state.slots[state.front].state = 'empty';
    state.size--;

    if (state.size === 0) {
      state.front = -1;
      state.rear = -1;
    } else {
      state.front = (state.front + 1) % state.capacity;
    }

    state.slots.forEach((s, idx) => {
      if (s.value === null) s.state = 'empty';
      else if (idx === state.front) s.state = 'front';
      else if (idx === state.rear) s.state = 'rear';
      else s.state = 'occupied';
    });

    steps.push({
      stepIndex: 1,
      operationType: 'dequeued',
      description: `Dequeued ${removedVal}. Front advanced to index ${state.front}. Size is now ${state.size}/${state.capacity}. O(1) time.`,
      codeLine: 5,
      state: cloneQueueDS(state),
      counters: { size: state.size, front: state.front, rear: state.rear },
    });
  } else if (operation.type === 'peekFront') {
    if (state.size === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'peek-empty',
        description: 'Queue is empty. Front is null.',
        codeLine: 2,
        state: cloneQueueDS(state),
      });
      return steps;
    }

    steps.push({
      stepIndex: 0,
      operationType: 'peek-front',
      description: `Peek Front: value is ${state.slots[state.front].value} at index ${state.front}. O(1) inspect.`,
      codeLine: 3,
      state: cloneQueueDS(state),
      counters: { front: state.front, rear: state.rear, size: state.size },
    });
  } else if (operation.type === 'inspectRear') {
    if (state.size === 0) {
      steps.push({
        stepIndex: 0,
        operationType: 'rear-empty',
        description: 'Queue is empty. Rear is null.',
        codeLine: 2,
        state: cloneQueueDS(state),
      });
      return steps;
    }

    steps.push({
      stepIndex: 0,
      operationType: 'inspect-rear',
      description: `Inspect Rear: value is ${state.slots[state.rear].value} at index ${state.rear}.`,
      codeLine: 3,
      state: cloneQueueDS(state),
      counters: { front: state.front, rear: state.rear, size: state.size },
    });
  } else if (operation.type === 'isEmpty') {
    const empty = state.size === 0;
    steps.push({
      stepIndex: 0,
      operationType: 'is-empty',
      description: `isEmpty: ${empty ? 'TRUE (0 items in queue)' : `FALSE (${state.size} items in queue)`}.`,
      codeLine: 1,
      state: cloneQueueDS(state),
    });
  } else if (operation.type === 'isFull') {
    const full = state.size === state.capacity;
    steps.push({
      stepIndex: 0,
      operationType: 'is-full',
      description: `isFull: ${full ? `TRUE (all ${state.capacity} slots filled)` : `FALSE (${state.size}/${state.capacity} filled)`}.`,
      codeLine: 1,
      state: cloneQueueDS(state),
    });
  }

  return steps;
}
