import { ExecutionStep } from '../../types/execution';
import { SortState, createSortElements, cloneSortState } from './types';

// ==========================================
// 1. BUBBLE SORT
// ==========================================
export function generateBubbleSortSteps(initialArray: number[]): ExecutionStep<SortState>[] {
  const steps: ExecutionStep<SortState>[] = [];
  const state: SortState = {
    array: createSortElements(initialArray),
    comparingIndices: [],
    swappingIndices: [],
    pivotIndex: null,
    sortedIndices: [],
    comparisons: 0,
    swaps: 0,
    writes: 0,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized array with ${initialArray.length} elements.`,
    codeLine: 1,
    state: cloneSortState(state),
    counters: { comparisons: 0, swaps: 0, writes: 0 },
  });

  const n = state.array.length;
  if (n <= 1) {
    state.sortedIndices = n === 1 ? [0] : [];
    if (n === 1) state.array[0].state = 'sorted';
    steps.push({
      stepIndex: steps.length,
      operationType: 'complete',
      description: 'Array is trivially sorted.',
      codeLine: 10,
      state: cloneSortState(state),
      counters: { comparisons: 0, swaps: 0, writes: 0 },
    });
    return steps;
  }

  for (let i = 0; i < n - 1; i++) {
    let swapped = false;
    for (let j = 0; j < n - i - 1; j++) {
      // Step: Compare adjacent elements
      state.comparingIndices = [j, j + 1];
      state.swappingIndices = [];
      state.array.forEach((el, idx) => {
        if (!state.sortedIndices.includes(idx)) el.state = 'default';
      });
      state.array[j].state = 'comparing';
      state.array[j + 1].state = 'comparing';
      state.comparisons++;

      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing elements at index ${j} (${state.array[j].value}) and index ${j + 1} (${state.array[j + 1].value}).`,
        codeLine: 4,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      if (state.array[j].value > state.array[j + 1].value) {
        // Step: Swap
        state.swaps++;
        state.writes += 2;
        state.swappingIndices = [j, j + 1];
        state.array[j].state = 'swapping';
        state.array[j + 1].state = 'swapping';

        const temp = state.array[j];
        state.array[j] = state.array[j + 1];
        state.array[j + 1] = temp;
        swapped = true;

        steps.push({
          stepIndex: steps.length,
          operationType: 'swap',
          description: `Swapped ${state.array[j + 1].value} and ${state.array[j].value} because ${state.array[j + 1].value} > ${state.array[j].value}.`,
          codeLine: 5,
          state: cloneSortState(state),
          counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
        });
      }
    }

    const sortedIdx = n - 1 - i;
    state.sortedIndices.push(sortedIdx);
    state.array[sortedIdx].state = 'sorted';
    state.comparingIndices = [];
    state.swappingIndices = [];

    steps.push({
      stepIndex: steps.length,
      operationType: 'sorted-element',
      description: `Element at index ${sortedIdx} (${state.array[sortedIdx].value}) is now in its final sorted position.`,
      codeLine: 7,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    if (!swapped) break;
  }

  // Mark all as sorted
  state.sortedIndices = Array.from({ length: n }, (_, idx) => idx);
  state.array.forEach((el) => (el.state = 'sorted'));
  state.comparingIndices = [];
  state.swappingIndices = [];

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: 'Bubble sort complete! The array is fully sorted.',
    codeLine: 10,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  return steps;
}

// ==========================================
// 2. SELECTION SORT
// ==========================================
export function generateSelectionSortSteps(initialArray: number[]): ExecutionStep<SortState>[] {
  const steps: ExecutionStep<SortState>[] = [];
  const state: SortState = {
    array: createSortElements(initialArray),
    comparingIndices: [],
    swappingIndices: [],
    pivotIndex: null,
    sortedIndices: [],
    comparisons: 0,
    swaps: 0,
    writes: 0,
  };

  const n = state.array.length;
  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized array with ${n} elements.`,
    codeLine: 1,
    state: cloneSortState(state),
    counters: { comparisons: 0, swaps: 0, writes: 0 },
  });

  for (let i = 0; i < n; i++) {
    let minIdx = i;
    state.pivotIndex = minIdx; // used to display current minimum
    state.array.forEach((el, idx) => {
      if (state.sortedIndices.includes(idx)) el.state = 'sorted';
      else if (idx === minIdx) el.state = 'pivot';
      else el.state = 'default';
    });

    steps.push({
      stepIndex: steps.length,
      operationType: 'mark-min',
      description: `Starting pass ${i + 1}. Assume minimum is at index ${minIdx} (${state.array[minIdx].value}).`,
      codeLine: 3,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    for (let j = i + 1; j < n; j++) {
      state.comparingIndices = [minIdx, j];
      state.array.forEach((el, idx) => {
        if (state.sortedIndices.includes(idx)) el.state = 'sorted';
        else if (idx === minIdx) el.state = 'pivot';
        else if (idx === j) el.state = 'comparing';
        else el.state = 'default';
      });
      state.comparisons++;

      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing current min at ${minIdx} (${state.array[minIdx].value}) with element at ${j} (${state.array[j].value}).`,
        codeLine: 5,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      if (state.array[j].value < state.array[minIdx].value) {
        minIdx = j;
        state.pivotIndex = minIdx;
        steps.push({
          stepIndex: steps.length,
          operationType: 'update-min',
          description: `Found new minimum at index ${minIdx} (${state.array[minIdx].value}).`,
          codeLine: 6,
          state: cloneSortState(state),
          counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
        });
      }
    }

    if (minIdx !== i) {
      state.swappingIndices = [i, minIdx];
      state.swaps++;
      state.writes += 2;
      const temp = state.array[i];
      state.array[i] = state.array[minIdx];
      state.array[minIdx] = temp;

      steps.push({
        stepIndex: steps.length,
        operationType: 'swap',
        description: `Swapped minimum element ${state.array[i].value} into index ${i}.`,
        codeLine: 8,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });
    }

    state.sortedIndices.push(i);
    state.array[i].state = 'sorted';
    state.pivotIndex = null;
    state.comparingIndices = [];
    state.swappingIndices = [];
  }

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: 'Selection sort complete! Array is sorted.',
    codeLine: 10,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  return steps;
}

// ==========================================
// 3. INSERTION SORT
// ==========================================
export function generateInsertionSortSteps(initialArray: number[]): ExecutionStep<SortState>[] {
  const steps: ExecutionStep<SortState>[] = [];
  const state: SortState = {
    array: createSortElements(initialArray),
    comparingIndices: [],
    swappingIndices: [],
    pivotIndex: null,
    sortedIndices: [0],
    comparisons: 0,
    swaps: 0,
    writes: 0,
  };

  const n = state.array.length;
  if (n > 0) state.array[0].state = 'sorted';

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized array. First element ${state.array[0]?.value} is considered a sorted subarray of size 1.`,
    codeLine: 1,
    state: cloneSortState(state),
    counters: { comparisons: 0, swaps: 0, writes: 0 },
  });

  for (let i = 1; i < n; i++) {
    const keyEl = state.array[i];
    const keyValue = keyEl.value;
    state.pivotIndex = i;
    keyEl.state = 'pivot';

    steps.push({
      stepIndex: steps.length,
      operationType: 'select-key',
      description: `Selected key = ${keyValue} at index ${i} to insert into sorted left portion.`,
      codeLine: 3,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    let j = i - 1;
    while (j >= 0) {
      state.comparisons++;
      state.comparingIndices = [j, j + 1];

      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing key (${keyValue}) with sorted element at index ${j} (${state.array[j].value}).`,
        codeLine: 5,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      if (state.array[j].value > keyValue) {
        state.writes++;
        state.swaps++;
        state.array[j + 1] = state.array[j];
        state.swappingIndices = [j, j + 1];

        steps.push({
          stepIndex: steps.length,
          operationType: 'shift',
          description: `${state.array[j].value} > ${keyValue}, shifting ${state.array[j].value} to index ${j + 1}.`,
          codeLine: 6,
          state: cloneSortState(state),
          counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
        });

        j--;
      } else {
        break;
      }
    }

    state.writes++;
    state.array[j + 1] = keyEl;
    state.sortedIndices = Array.from({ length: i + 1 }, (_, idx) => idx);
    state.array.forEach((el, idx) => {
      if (idx <= i) el.state = 'sorted';
      else el.state = 'default';
    });
    state.pivotIndex = null;
    state.comparingIndices = [];
    state.swappingIndices = [];

    steps.push({
      stepIndex: steps.length,
      operationType: 'insert',
      description: `Inserted key ${keyValue} at position ${j + 1}. Subarray [0..${i}] is now sorted.`,
      codeLine: 8,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });
  }

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: 'Insertion sort complete! All elements are sorted.',
    codeLine: 10,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  return steps;
}

// ==========================================
// 4. MERGE SORT
// ==========================================
export function generateMergeSortSteps(initialArray: number[]): ExecutionStep<SortState>[] {
  const steps: ExecutionStep<SortState>[] = [];
  const state: SortState = {
    array: createSortElements(initialArray),
    comparingIndices: [],
    swappingIndices: [],
    pivotIndex: null,
    sortedIndices: [],
    comparisons: 0,
    swaps: 0,
    writes: 0,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized array with ${state.array.length} elements for Merge Sort.`,
    codeLine: 1,
    state: cloneSortState(state),
    counters: { comparisons: 0, swaps: 0, writes: 0 },
  });

  function merge(left: number, mid: number, right: number) {
    const leftPart = state.array.slice(left, mid + 1);
    const rightPart = state.array.slice(mid + 1, right + 1);

    steps.push({
      stepIndex: steps.length,
      operationType: 'merge-start',
      description: `Merging subarrays: Left [${left}..${mid}] and Right [${mid + 1}..${right}].`,
      codeLine: 5,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    let i = 0;
    let j = 0;
    let k = left;

    while (i < leftPart.length && j < rightPart.length) {
      state.comparisons++;
      state.comparingIndices = [left + i, mid + 1 + j];

      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing left element ${leftPart[i].value} with right element ${rightPart[j].value}.`,
        codeLine: 7,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      if (leftPart[i].value <= rightPart[j].value) {
        state.array[k] = { ...leftPart[i], state: 'auxiliary' };
        state.writes++;
        i++;
      } else {
        state.array[k] = { ...rightPart[j], state: 'auxiliary' };
        state.writes++;
        j++;
      }

      steps.push({
        stepIndex: steps.length,
        operationType: 'place',
        description: `Placed ${state.array[k].value} at position ${k}.`,
        codeLine: 8,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      k++;
    }

    while (i < leftPart.length) {
      state.array[k] = { ...leftPart[i], state: 'auxiliary' };
      state.writes++;
      steps.push({
        stepIndex: steps.length,
        operationType: 'place-remaining',
        description: `Copying remaining left element ${leftPart[i].value} into position ${k}.`,
        codeLine: 9,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });
      i++;
      k++;
    }

    while (j < rightPart.length) {
      state.array[k] = { ...rightPart[j], state: 'auxiliary' };
      state.writes++;
      steps.push({
        stepIndex: steps.length,
        operationType: 'place-remaining',
        description: `Copying remaining right element ${rightPart[j].value} into position ${k}.`,
        codeLine: 10,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });
      j++;
      k++;
    }

    // Subarray is merged
    for (let idx = left; idx <= right; idx++) {
      if (left === 0 && right === state.array.length - 1) {
        state.sortedIndices.push(idx);
        state.array[idx].state = 'sorted';
      } else {
        state.array[idx].state = 'default';
      }
    }
  }

  function mergeSortRec(left: number, right: number) {
    if (left >= right) return;
    const mid = Math.floor((left + right) / 2);

    steps.push({
      stepIndex: steps.length,
      operationType: 'divide',
      description: `Dividing range [${left}..${right}] into [${left}..${mid}] and [${mid + 1}..${right}].`,
      codeLine: 3,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    mergeSortRec(left, mid);
    mergeSortRec(mid + 1, right);
    merge(left, mid, right);
  }

  if (state.array.length > 0) {
    mergeSortRec(0, state.array.length - 1);
  }

  state.sortedIndices = Array.from({ length: state.array.length }, (_, idx) => idx);
  state.array.forEach((el) => (el.state = 'sorted'));
  state.comparingIndices = [];

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: 'Merge sort complete! Array is sorted.',
    codeLine: 12,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  return steps;
}

// ==========================================
// 5. QUICK SORT
// ==========================================
export function generateQuickSortSteps(initialArray: number[]): ExecutionStep<SortState>[] {
  const steps: ExecutionStep<SortState>[] = [];
  const state: SortState = {
    array: createSortElements(initialArray),
    comparingIndices: [],
    swappingIndices: [],
    pivotIndex: null,
    sortedIndices: [],
    comparisons: 0,
    swaps: 0,
    writes: 0,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized array for Quick Sort.`,
    codeLine: 1,
    state: cloneSortState(state),
    counters: { comparisons: 0, swaps: 0, writes: 0 },
  });

  function partition(low: number, high: number): number {
    const pivot = state.array[high].value;
    state.pivotIndex = high;
    state.array[high].state = 'pivot';

    steps.push({
      stepIndex: steps.length,
      operationType: 'choose-pivot',
      description: `Selected pivot = ${pivot} at index ${high} for range [${low}..${high}].`,
      codeLine: 4,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    let i = low - 1;

    for (let j = low; j < high; j++) {
      state.comparisons++;
      state.comparingIndices = [j, high];

      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing element at index ${j} (${state.array[j].value}) with pivot (${pivot}).`,
        codeLine: 6,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      if (state.array[j].value <= pivot) {
        i++;
        if (i !== j) {
          state.swaps++;
          state.writes += 2;
          state.swappingIndices = [i, j];
          const temp = state.array[i];
          state.array[i] = state.array[j];
          state.array[j] = temp;

          steps.push({
            stepIndex: steps.length,
            operationType: 'swap',
            description: `${state.array[i].value} <= pivot, swapped index ${i} and ${j}.`,
            codeLine: 8,
            state: cloneSortState(state),
            counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
          });
        }
      }
    }

    // Place pivot in correct place (i + 1)
    state.swaps++;
    state.writes += 2;
    state.swappingIndices = [i + 1, high];
    const temp = state.array[i + 1];
    state.array[i + 1] = state.array[high];
    state.array[high] = temp;

    const pivotPos = i + 1;
    state.sortedIndices.push(pivotPos);
    state.array[pivotPos].state = 'sorted';
    state.pivotIndex = null;
    state.comparingIndices = [];
    state.swappingIndices = [];

    steps.push({
      stepIndex: steps.length,
      operationType: 'place-pivot',
      description: `Placed pivot ${pivot} into its sorted position at index ${pivotPos}.`,
      codeLine: 10,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    return pivotPos;
  }

  function quickSortRec(low: number, high: number) {
    if (low < high) {
      const pi = partition(low, high);
      quickSortRec(low, pi - 1);
      quickSortRec(pi + 1, high);
    } else if (low === high) {
      state.sortedIndices.push(low);
      state.array[low].state = 'sorted';
    }
  }

  if (state.array.length > 0) {
    quickSortRec(0, state.array.length - 1);
  }

  state.sortedIndices = Array.from({ length: state.array.length }, (_, idx) => idx);
  state.array.forEach((el) => (el.state = 'sorted'));

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: 'Quick sort complete! Array is sorted.',
    codeLine: 12,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  return steps;
}

// ==========================================
// 6. HEAP SORT
// ==========================================
export function generateHeapSortSteps(initialArray: number[]): ExecutionStep<SortState>[] {
  const steps: ExecutionStep<SortState>[] = [];
  const state: SortState = {
    array: createSortElements(initialArray),
    comparingIndices: [],
    swappingIndices: [],
    pivotIndex: null,
    sortedIndices: [],
    comparisons: 0,
    swaps: 0,
    writes: 0,
  };

  const n = state.array.length;
  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized array for Heap Sort. Starting max-heap construction.`,
    codeLine: 1,
    state: cloneSortState(state),
    counters: { comparisons: 0, swaps: 0, writes: 0 },
  });

  function heapify(size: number, i: number) {
    let largest = i;
    const left = 2 * i + 1;
    const right = 2 * i + 2;

    if (left < size) {
      state.comparisons++;
      state.comparingIndices = [left, largest];
      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing left child at index ${left} (${state.array[left].value}) with current largest at ${largest} (${state.array[largest].value}).`,
        codeLine: 4,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });
      if (state.array[left].value > state.array[largest].value) {
        largest = left;
      }
    }

    if (right < size) {
      state.comparisons++;
      state.comparingIndices = [right, largest];
      steps.push({
        stepIndex: steps.length,
        operationType: 'compare',
        description: `Comparing right child at index ${right} (${state.array[right].value}) with largest at ${largest} (${state.array[largest].value}).`,
        codeLine: 5,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });
      if (state.array[right].value > state.array[largest].value) {
        largest = right;
      }
    }

    if (largest !== i) {
      state.swaps++;
      state.writes += 2;
      state.swappingIndices = [i, largest];
      const temp = state.array[i];
      state.array[i] = state.array[largest];
      state.array[largest] = temp;

      steps.push({
        stepIndex: steps.length,
        operationType: 'swap',
        description: `Swapped index ${i} with ${largest} to maintain max-heap property. Sifting down.`,
        codeLine: 7,
        state: cloneSortState(state),
        counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
      });

      heapify(size, largest);
    }
  }

  // Build max heap
  for (let i = Math.floor(n / 2) - 1; i >= 0; i--) {
    heapify(n, i);
  }

  steps.push({
    stepIndex: steps.length,
    operationType: 'heap-built',
    description: `Max heap built successfully. Now extracting elements from root one by one.`,
    codeLine: 8,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  // Extract from heap
  for (let i = n - 1; i > 0; i--) {
    state.swaps++;
    state.writes += 2;
    state.swappingIndices = [0, i];
    const temp = state.array[0];
    state.array[0] = state.array[i];
    state.array[i] = temp;

    state.sortedIndices.push(i);
    state.array[i].state = 'sorted';

    steps.push({
      stepIndex: steps.length,
      operationType: 'extract-max',
      description: `Moved maximum element (${temp.value}) to sorted position at index ${i}.`,
      codeLine: 10,
      state: cloneSortState(state),
      counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
    });

    heapify(i, 0);
  }

  if (n > 0) {
    state.sortedIndices.push(0);
    state.array[0].state = 'sorted';
  }
  state.comparingIndices = [];
  state.swappingIndices = [];

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: 'Heap sort complete! Array is sorted.',
    codeLine: 12,
    state: cloneSortState(state),
    counters: { comparisons: state.comparisons, swaps: state.swaps, writes: state.writes },
  });

  return steps;
}
