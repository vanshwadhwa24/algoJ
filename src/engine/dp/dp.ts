import { ExecutionStep } from '../../types/execution';

// ==========================================
// 1. 0/1 KNAPSACK
// ==========================================
export interface KnapsackItem {
  id: string;
  weight: number;
  value: number;
  name: string;
}

export interface KnapsackState {
  items: KnapsackItem[];
  capacity: number;
  table: number[][]; // (N + 1) x (W + 1)
  currentRow: number;
  currentCol: number;
  activeDependencies: [number, number][]; // [r, c] pairs
  selectedItems: string[];
  optimalValue: number;
  message: string;
}

export function generateKnapsackSteps(
  items: KnapsackItem[] = [
    { id: '1', name: 'Book', weight: 1, value: 3 },
    { id: '2', name: 'Camera', weight: 2, value: 4 },
    { id: '3', name: 'Laptop', weight: 3, value: 5 },
    { id: '4', name: 'Tablet', weight: 4, value: 7 },
  ],
  capacity: number = 5
): ExecutionStep<KnapsackState>[] {
  const steps: ExecutionStep<KnapsackState>[] = [];
  const N = items.length;
  const W = capacity;

  const table: number[][] = Array.from({ length: N + 1 }, () => Array(W + 1).fill(0));

  const state: KnapsackState = {
    items,
    capacity: W,
    table: table.map((r) => [...r]),
    currentRow: 0,
    currentCol: 0,
    activeDependencies: [],
    selectedItems: [],
    optimalValue: 0,
    message: `Initialized 0/1 Knapsack DP table of size (${N + 1}) x (${W + 1}).`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `0/1 Knapsack initialized. Base cases: dp[0][w] = 0 (no items) and dp[i][0] = 0 (capacity 0).`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { optimalValue: 0 },
  });

  for (let i = 1; i <= N; i++) {
    const item = items[i - 1];
    for (let w = 1; w <= W; w++) {
      state.currentRow = i;
      state.currentCol = w;

      if (item.weight > w) {
        // Cannot include item
        state.table[i][w] = state.table[i - 1][w];
        state.activeDependencies = [[i - 1, w]];

        steps.push({
          stepIndex: steps.length,
          operationType: 'skip-item',
          description: `Item "${item.name}" (wt=${item.weight}) exceeds current capacity ${w}. Carry forward dp[${i - 1}][${w}] = ${state.table[i - 1][w]}.`,
          codeLine: 4,
          state: JSON.parse(JSON.stringify(state)),
          counters: { optimalValue: state.table[i][w] },
        });
      } else {
        // Max of excluding and including
        const excludeVal = state.table[i - 1][w];
        const includeVal = item.value + state.table[i - 1][w - item.weight];
        state.activeDependencies = [
          [i - 1, w],
          [i - 1, w - item.weight],
        ];

        state.table[i][w] = Math.max(excludeVal, includeVal);

        steps.push({
          stepIndex: steps.length,
          operationType: 'compare-include-exclude',
          description: `At capacity ${w}, evaluate "${item.name}": Exclude = ${excludeVal}, Include = ${item.value} + dp[${i - 1}][${w - item.weight}] (${state.table[i - 1][w - item.weight]}) = ${includeVal}. Max is ${state.table[i][w]}.`,
          codeLine: 6,
          state: JSON.parse(JSON.stringify(state)),
          counters: { optimalValue: state.table[i][w] },
        });
      }
    }
  }

  // Backtracking to find selected items
  const selected: string[] = [];
  let currW = W;
  for (let i = N; i > 0; i--) {
    if (state.table[i][currW] !== state.table[i - 1][currW]) {
      const item = items[i - 1];
      selected.unshift(item.name);
      currW -= item.weight;
    }
  }

  state.selectedItems = selected;
  state.optimalValue = state.table[N][W];
  state.activeDependencies = [];

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: `0/1 Knapsack solved! Optimal value is $${state.optimalValue}. Items selected via backtracking: [${selected.join(', ')}].`,
    codeLine: 9,
    state: JSON.parse(JSON.stringify(state)),
    counters: { optimalValue: state.optimalValue },
  });

  return steps;
}

// ==========================================
// 2. LONGEST COMMON SUBSEQUENCE (LCS)
// ==========================================
export interface LCSState {
  s1: string;
  s2: string;
  table: number[][]; // (len1 + 1) x (len2 + 1)
  currentRow: number;
  currentCol: number;
  activeDependencies: [number, number][];
  lcsResult: string;
  message: string;
}

export function generateLCSSteps(
  s1: string = 'STONE',
  s2: string = 'LONGEST'
): ExecutionStep<LCSState>[] {
  const steps: ExecutionStep<LCSState>[] = [];
  const str1 = s1.toUpperCase();
  const str2 = s2.toUpperCase();
  const m = str1.length;
  const n = str2.length;

  const table: number[][] = Array.from({ length: m + 1 }, () => Array(n + 1).fill(0));

  const state: LCSState = {
    s1: str1,
    s2: str2,
    table: table.map((r) => [...r]),
    currentRow: 0,
    currentCol: 0,
    activeDependencies: [],
    lcsResult: '',
    message: `Initialized LCS DP table (${m + 1} x ${n + 1}) for "${str1}" and "${str2}".`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `LCS initialized for "${str1}" and "${str2}". Row 0 and Col 0 set to 0.`,
    codeLine: 2,
    state: JSON.parse(JSON.stringify(state)),
    counters: { lcsLength: 0 },
  });

  for (let i = 1; i <= m; i++) {
    for (let j = 1; j <= n; j++) {
      state.currentRow = i;
      state.currentCol = j;

      if (str1[i - 1] === str2[j - 1]) {
        state.table[i][j] = 1 + state.table[i - 1][j - 1];
        state.activeDependencies = [[i - 1, j - 1]];

        steps.push({
          stepIndex: steps.length,
          operationType: 'char-match',
          description: `Characters MATCH: '${str1[i - 1]}' == '${str2[j - 1]}'! dp[${i}][${j}] = 1 + dp[${i - 1}][${j - 1}] (${state.table[i - 1][j - 1]}) = ${state.table[i][j]}. (Diagonal move)`,
          codeLine: 4,
          state: JSON.parse(JSON.stringify(state)),
          counters: { lcsLength: state.table[i][j] },
        });
      } else {
        const top = state.table[i - 1][j];
        const left = state.table[i][j - 1];
        state.table[i][j] = Math.max(top, left);
        state.activeDependencies = [
          [i - 1, j],
          [i, j - 1],
        ];

        steps.push({
          stepIndex: steps.length,
          operationType: 'char-mismatch',
          description: `'${str1[i - 1]}' != '${str2[j - 1]}'. dp[${i}][${j}] = max(top=${top}, left=${left}) = ${state.table[i][j]}.`,
          codeLine: 6,
          state: JSON.parse(JSON.stringify(state)),
          counters: { lcsLength: state.table[i][j] },
        });
      }
    }
  }

  // Backtracking to find LCS string
  let lcsChars: string[] = [];
  let i = m;
  let j = n;
  while (i > 0 && j > 0) {
    if (str1[i - 1] === str2[j - 1]) {
      lcsChars.unshift(str1[i - 1]);
      i--;
      j--;
    } else if (state.table[i - 1][j] > state.table[i][j - 1]) {
      i--;
    } else {
      j--;
    }
  }

  state.lcsResult = lcsChars.join('');
  state.activeDependencies = [];

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: `LCS complete! Longest common subsequence is "${state.lcsResult}" (length ${state.table[m][n]}). Time: O(m * n).`,
    codeLine: 9,
    state: JSON.parse(JSON.stringify(state)),
    counters: { lcsLength: state.table[m][n] },
  });

  return steps;
}
