import { ExecutionStep } from '../../types/execution';
import { SearchState, createSearchElements, cloneSearchState } from './types';

// ==========================================
// 1. LINEAR SEARCH
// ==========================================
export function generateLinearSearchSteps(
  initialArray: number[],
  target: number
): ExecutionStep<SearchState>[] {
  const steps: ExecutionStep<SearchState>[] = [];
  const state: SearchState = {
    array: createSearchElements(initialArray),
    target,
    currentIndex: null,
    comparisons: 0,
    foundIndex: null,
    isSorted: true,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Target is ${target}. Starting linear scan from index 0.`,
    codeLine: 1,
    state: cloneSearchState(state),
    counters: { comparisons: 0 },
  });

  let found = false;
  for (let i = 0; i < state.array.length; i++) {
    state.currentIndex = i;
    state.comparisons++;
    state.array.forEach((el, idx) => {
      if (idx < i) el.state = 'eliminated';
      else if (idx === i) el.state = 'active';
      else el.state = 'default';
    });

    steps.push({
      stepIndex: steps.length,
      operationType: 'inspect',
      description: `Inspecting index ${i}: Element is ${state.array[i].value}. Compare with target ${target}.`,
      codeLine: 3,
      state: cloneSearchState(state),
      counters: { comparisons: state.comparisons },
    });

    if (state.array[i].value === target) {
      state.foundIndex = i;
      state.array[i].state = 'found';
      found = true;

      steps.push({
        stepIndex: steps.length,
        operationType: 'found',
        description: `Target ${target} found at index ${i}! Search successful after ${state.comparisons} comparisons.`,
        codeLine: 4,
        state: cloneSearchState(state),
        counters: { comparisons: state.comparisons },
      });
      break;
    }
  }

  if (!found) {
    state.currentIndex = null;
    state.array.forEach((el) => (el.state = 'eliminated'));
    steps.push({
      stepIndex: steps.length,
      operationType: 'not-found',
      description: `Target ${target} not found in array. Scanned all ${state.array.length} elements.`,
      codeLine: 6,
      state: cloneSearchState(state),
      counters: { comparisons: state.comparisons },
    });
  }

  return steps;
}

// ==========================================
// 2. BINARY SEARCH
// ==========================================
export function generateBinarySearchSteps(
  initialArray: number[],
  target: number
): ExecutionStep<SearchState>[] {
  const steps: ExecutionStep<SearchState>[] = [];
  
  // Verify if sorted
  let isSorted = true;
  for (let i = 0; i < initialArray.length - 1; i++) {
    if (initialArray[i] > initialArray[i + 1]) {
      isSorted = false;
      break;
    }
  }

  const workingArray = isSorted ? [...initialArray] : [...initialArray].sort((a, b) => a - b);
  const state: SearchState = {
    array: createSearchElements(workingArray),
    target,
    currentIndex: null,
    lowIndex: 0,
    highIndex: workingArray.length - 1,
    midIndex: null,
    comparisons: 0,
    foundIndex: null,
    isSorted,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: isSorted
      ? `Array is sorted. Target is ${target}. Initial bounds: low = 0, high = ${workingArray.length - 1}.`
      : `Note: Input array was automatically sorted for Binary Search. Target = ${target}.`,
    codeLine: 1,
    state: cloneSearchState(state),
    counters: { comparisons: 0 },
  });

  let low = 0;
  let high = workingArray.length - 1;
  let found = false;

  while (low <= high) {
    const mid = Math.floor((low + high) / 2);
    state.lowIndex = low;
    state.highIndex = high;
    state.midIndex = mid;
    state.currentIndex = mid;
    state.comparisons++;

    // Mark states
    state.array.forEach((el, idx) => {
      if (idx < low || idx > high) el.state = 'eliminated';
      else if (idx === mid) el.state = 'mid';
      else el.state = 'default';
    });

    steps.push({
      stepIndex: steps.length,
      operationType: 'calculate-mid',
      description: `Calculated mid = floor((${low} + ${high}) / 2) = ${mid}. Element at mid is ${state.array[mid].value}.`,
      codeLine: 4,
      state: cloneSearchState(state),
      counters: { comparisons: state.comparisons },
    });

    if (state.array[mid].value === target) {
      state.foundIndex = mid;
      state.array[mid].state = 'found';
      found = true;

      steps.push({
        stepIndex: steps.length,
        operationType: 'found',
        description: `Target ${target} matches element at index ${mid}! Search succeeded in ${state.comparisons} comparisons.`,
        codeLine: 5,
        state: cloneSearchState(state),
        counters: { comparisons: state.comparisons },
      });
      break;
    } else if (state.array[mid].value < target) {
      steps.push({
        stepIndex: steps.length,
        operationType: 'eliminate-left',
        description: `Since ${state.array[mid].value} < ${target}, target must be in right half. Eliminating range [${low}..${mid}]. Updating low = ${mid + 1}.`,
        codeLine: 7,
        state: cloneSearchState(state),
        counters: { comparisons: state.comparisons },
      });
      low = mid + 1;
    } else {
      steps.push({
        stepIndex: steps.length,
        operationType: 'eliminate-right',
        description: `Since ${state.array[mid].value} > ${target}, target must be in left half. Eliminating range [${mid}..${high}]. Updating high = ${mid - 1}.`,
        codeLine: 9,
        state: cloneSearchState(state),
        counters: { comparisons: state.comparisons },
      });
      high = mid - 1;
    }
  }

  if (!found) {
    state.array.forEach((el) => (el.state = 'eliminated'));
    state.lowIndex = low;
    state.highIndex = high;
    state.midIndex = null;
    state.currentIndex = null;

    steps.push({
      stepIndex: steps.length,
      operationType: 'not-found',
      description: `Search interval exhausted (low > high). Target ${target} is not in the array.`,
      codeLine: 11,
      state: cloneSearchState(state),
      counters: { comparisons: state.comparisons },
    });
  }

  return steps;
}
