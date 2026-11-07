import { ExecutionStep } from '../../types/execution';

// ==========================================
// 1. FACTORIAL
// ==========================================
export interface StackFrame {
  id: string;
  name: string;
  arg: number;
  returnValue?: number;
  state: 'calling' | 'base-case' | 'returned';
}

export interface FactorialState {
  n: number;
  callStack: StackFrame[];
  result: number | null;
  message: string;
}

export function generateFactorialSteps(n: number = 4): ExecutionStep<FactorialState>[] {
  const steps: ExecutionStep<FactorialState>[] = [];
  const clampedN = Math.max(0, Math.min(7, n));
  const callStack: StackFrame[] = [];

  const state: FactorialState = {
    n: clampedN,
    callStack: [],
    result: null,
    message: `Computing Factorial(${clampedN}).`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Calling Factorial(${clampedN}). Base case is n <= 1.`,
    codeLine: 1,
    state: JSON.parse(JSON.stringify(state)),
    counters: { callStackDepth: 0 },
  });

  function fact(val: number): number {
    const frame: StackFrame = {
      id: `frame-${val}`,
      name: `fact(${val})`,
      arg: val,
      state: val <= 1 ? 'base-case' : 'calling',
    };
    callStack.push(frame);
    state.callStack = [...callStack];

    steps.push({
      stepIndex: steps.length,
      operationType: val <= 1 ? 'base-case' : 'call',
      description:
        val <= 1
          ? `Reached base case: fact(${val}) returns 1 immediately.`
          : `Pushed fact(${val}) onto call stack. Awaiting fact(${val - 1}).`,
      codeLine: val <= 1 ? 2 : 4,
      state: JSON.parse(JSON.stringify(state)),
      counters: { callStackDepth: callStack.length },
    });

    if (val <= 1) {
      frame.returnValue = 1;
      frame.state = 'returned';
      return 1;
    }

    const sub = fact(val - 1);
    const res = val * sub;
    frame.returnValue = res;
    frame.state = 'returned';

    steps.push({
      stepIndex: steps.length,
      operationType: 'return-multiply',
      description: `fact(${val}) computes ${val} * fact(${val - 1}) (${sub}) = ${res}. Returning ${res}.`,
      codeLine: 5,
      state: JSON.parse(JSON.stringify(state)),
      counters: { callStackDepth: callStack.length },
    });

    callStack.pop();
    state.callStack = [...callStack];
    return res;
  }

  const finalRes = fact(clampedN);
  state.result = finalRes;

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: `Factorial(${clampedN}) = ${finalRes} completed. Call stack unwound completely.`,
    codeLine: 6,
    state: JSON.parse(JSON.stringify(state)),
    counters: { callStackDepth: 0 },
  });

  return steps;
}

// ==========================================
// 2. FIBONACCI (RECURSIVE, MEMOIZED, TABULATED)
// ==========================================
export interface FibonacciState {
  n: number;
  mode: 'recursive' | 'memoized' | 'tabulated';
  callStack: string[];
  memoTable: Record<number, number>;
  dpArray?: number[];
  currentIndex?: number;
  totalCalls: number;
  result: number | null;
  message: string;
}

export function generateFibonacciSteps(
  n: number = 5,
  mode: 'recursive' | 'memoized' | 'tabulated' = 'tabulated'
): ExecutionStep<FibonacciState>[] {
  const steps: ExecutionStep<FibonacciState>[] = [];
  const clampedN = Math.max(0, Math.min(mode === 'recursive' ? 6 : 10, n));

  const state: FibonacciState = {
    n: clampedN,
    mode,
    callStack: [],
    memoTable: {},
    dpArray: mode === 'tabulated' ? Array(clampedN + 1).fill(0) : undefined,
    totalCalls: 0,
    result: null,
    message: `Computing Fibonacci(${clampedN}) using ${mode}.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `Initialized Fibonacci(${clampedN}) using ${mode.toUpperCase()} approach.`,
    codeLine: 1,
    state: JSON.parse(JSON.stringify(state)),
    counters: { totalCalls: 0 },
  });

  if (mode === 'tabulated') {
    state.dpArray![0] = 0;
    if (clampedN >= 1) state.dpArray![1] = 1;

    steps.push({
      stepIndex: 1,
      operationType: 'base-cases',
      description: 'Initialized base cases: dp[0] = 0, dp[1] = 1.',
      codeLine: 2,
      state: JSON.parse(JSON.stringify(state)),
      counters: { totalCalls: 1 },
    });

    for (let i = 2; i <= clampedN; i++) {
      state.currentIndex = i;
      const v = state.dpArray![i - 1] + state.dpArray![i - 2];
      state.dpArray![i] = v;

      steps.push({
        stepIndex: steps.length,
        operationType: 'compute-cell',
        description: `dp[${i}] = dp[${i - 1}] (${state.dpArray![i - 1]}) + dp[${i - 2}] (${state.dpArray![i - 2]}) = ${v}. O(1) step.`,
        codeLine: 4,
        state: JSON.parse(JSON.stringify(state)),
        counters: { totalCalls: i },
      });
    }

    state.result = state.dpArray![clampedN];
    steps.push({
      stepIndex: steps.length,
      operationType: 'complete',
      description: `Tabulation complete! Fib(${clampedN}) = ${state.result}. Time: O(n), Space: O(n).`,
      codeLine: 6,
      state: JSON.parse(JSON.stringify(state)),
      counters: { totalCalls: clampedN },
    });
  } else if (mode === 'memoized') {
    const memo: Record<number, number> = {};
    const stack: string[] = [];
    let callCount = 0;

    function fibMemo(val: number): number {
      callCount++;
      state.totalCalls = callCount;
      stack.push(`fib(${val})`);
      state.callStack = [...stack];

      steps.push({
        stepIndex: steps.length,
        operationType: 'call',
        description: `Calling fib(${val}). Call stack: [${stack.join(' -> ')}].`,
        codeLine: 2,
        state: JSON.parse(JSON.stringify(state)),
        counters: { totalCalls: callCount },
      });

      if (val in memo) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'cache-hit',
          description: `CACHE HIT! fib(${val}) already computed in memo table = ${memo[val]}. Skipping subproblem recomputation!`,
          codeLine: 3,
          state: JSON.parse(JSON.stringify(state)),
          counters: { totalCalls: callCount },
        });
        stack.pop();
        state.callStack = [...stack];
        return memo[val];
      }

      if (val <= 1) {
        memo[val] = val;
        state.memoTable = { ...memo };
        steps.push({
          stepIndex: steps.length,
          operationType: 'base-case',
          description: `Base case: fib(${val}) = ${val}. Cached in memo table.`,
          codeLine: 5,
          state: JSON.parse(JSON.stringify(state)),
          counters: { totalCalls: callCount },
        });
        stack.pop();
        state.callStack = [...stack];
        return val;
      }

      const res = fibMemo(val - 1) + fibMemo(val - 2);
      memo[val] = res;
      state.memoTable = { ...memo };

      steps.push({
        stepIndex: steps.length,
        operationType: 'cache-store',
        description: `Stored fib(${val}) = ${res} in memo table.`,
        codeLine: 7,
        state: JSON.parse(JSON.stringify(state)),
        counters: { totalCalls: callCount },
      });

      stack.pop();
      state.callStack = [...stack];
      return res;
    }

    const res = fibMemo(clampedN);
    state.result = res;

    steps.push({
      stepIndex: steps.length,
      operationType: 'complete',
      description: `Memoized Fibonacci completed! Result: ${res}. Total calls: ${callCount} (vs ${Math.round(Math.pow(1.618, clampedN))} in naive recursion).`,
      codeLine: 9,
      state: JSON.parse(JSON.stringify(state)),
      counters: { totalCalls: callCount },
    });
  } else {
    // Naive recursive
    const stack: string[] = [];
    let callCount = 0;

    function fibRec(val: number): number {
      callCount++;
      state.totalCalls = callCount;
      stack.push(`fib(${val})`);
      state.callStack = [...stack];

      steps.push({
        stepIndex: steps.length,
        operationType: 'call',
        description: `Calling fib(${val}). Call stack: [${stack.join(' -> ')}].`,
        codeLine: 2,
        state: JSON.parse(JSON.stringify(state)),
        counters: { totalCalls: callCount },
      });

      if (val <= 1) {
        steps.push({
          stepIndex: steps.length,
          operationType: 'base-case',
          description: `Base case: fib(${val}) returns ${val}.`,
          codeLine: 3,
          state: JSON.parse(JSON.stringify(state)),
          counters: { totalCalls: callCount },
        });
        stack.pop();
        state.callStack = [...stack];
        return val;
      }

      const res = fibRec(val - 1) + fibRec(val - 2);
      steps.push({
        stepIndex: steps.length,
        operationType: 'return',
        description: `fib(${val}) returns ${res}.`,
        codeLine: 5,
        state: JSON.parse(JSON.stringify(state)),
        counters: { totalCalls: callCount },
      });

      stack.pop();
      state.callStack = [...stack];
      return res;
    }

    const res = fibRec(clampedN);
    state.result = res;

    steps.push({
      stepIndex: steps.length,
      operationType: 'complete',
      description: `Naive recursive Fib(${clampedN}) = ${res}. Total recursive calls: ${callCount}. Exponential O(2^n) time!`,
      codeLine: 7,
      state: JSON.parse(JSON.stringify(state)),
      counters: { totalCalls: callCount },
    });
  }

  return steps;
}

// ==========================================
// 3. TOWER OF HANOI
// ==========================================
export interface HanoiState {
  disksCount: number;
  pegs: Record<'A' | 'B' | 'C', number[]>;
  moveCount: number;
  lastMove?: { disk: number; from: 'A' | 'B' | 'C'; to: 'A' | 'B' | 'C' };
  message: string;
}

export function generateHanoiSteps(disks: number = 3): ExecutionStep<HanoiState>[] {
  const steps: ExecutionStep<HanoiState>[] = [];
  const clampedDisks = Math.max(1, Math.min(5, disks));

  const pegs: Record<'A' | 'B' | 'C', number[]> = {
    A: Array.from({ length: clampedDisks }, (_, i) => clampedDisks - i), // [3, 2, 1] (bottom to top)
    B: [],
    C: [],
  };

  const state: HanoiState = {
    disksCount: clampedDisks,
    pegs: {
      A: [...pegs.A],
      B: [...pegs.B],
      C: [...pegs.C],
    },
    moveCount: 0,
    message: `Tower of Hanoi with ${clampedDisks} disks initialized. Minimum moves: 2^${clampedDisks} - 1 = ${Math.pow(2, clampedDisks) - 1}.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `All ${clampedDisks} disks placed on Peg A. Goal: Move all disks to Peg C using Peg B as auxiliary.`,
    codeLine: 1,
    state: JSON.parse(JSON.stringify(state)),
    counters: { moves: 0 },
  });

  function hanoiRec(n: number, from: 'A' | 'B' | 'C', to: 'A' | 'B' | 'C', aux: 'A' | 'B' | 'C') {
    if (n === 1) {
      const disk = state.pegs[from].pop()!;
      state.pegs[to].push(disk);
      state.moveCount++;
      state.lastMove = { disk, from, to };

      steps.push({
        stepIndex: steps.length,
        operationType: 'move-disk',
        description: `Move disk ${disk} from Peg ${from} to Peg ${to}. Total moves: ${state.moveCount}.`,
        codeLine: 4,
        state: JSON.parse(JSON.stringify(state)),
        counters: { moves: state.moveCount },
      });
      return;
    }

    hanoiRec(n - 1, from, aux, to);

    const disk = state.pegs[from].pop()!;
    state.pegs[to].push(disk);
    state.moveCount++;
    state.lastMove = { disk, from, to };

    steps.push({
      stepIndex: steps.length,
      operationType: 'move-disk',
      description: `Move disk ${disk} from Peg ${from} to Peg ${to}. Total moves: ${state.moveCount}.`,
      codeLine: 6,
      state: JSON.parse(JSON.stringify(state)),
      counters: { moves: state.moveCount },
    });

    hanoiRec(n - 1, aux, to, from);
  }

  hanoiRec(clampedDisks, 'A', 'C', 'B');

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: `Tower of Hanoi solved in exactly ${state.moveCount} moves! Disks are correctly ordered on Peg C.`,
    codeLine: 8,
    state: JSON.parse(JSON.stringify(state)),
    counters: { moves: state.moveCount },
  });

  return steps;
}

// ==========================================
// 4. N-QUEENS BACKTRACKING
// ==========================================
export interface NQueensState {
  n: number;
  board: number[]; // row -> col (-1 if no queen)
  currentRow: number;
  currentCol: number;
  isConflict: boolean;
  solutionsFound: number;
  message: string;
}

export function generateNQueensSteps(n: number = 4): ExecutionStep<NQueensState>[] {
  const steps: ExecutionStep<NQueensState>[] = [];
  const clampedN = Math.max(4, Math.min(6, n));
  const board = Array(clampedN).fill(-1);

  const state: NQueensState = {
    n: clampedN,
    board: [...board],
    currentRow: 0,
    currentCol: 0,
    isConflict: false,
    solutionsFound: 0,
    message: `Initialized ${clampedN}-Queens puzzle on a ${clampedN}x${clampedN} board.`,
  };

  steps.push({
    stepIndex: 0,
    operationType: 'init',
    description: `N-Queens backtracking initialized for N = ${clampedN}. Trying placements row by row.`,
    codeLine: 1,
    state: JSON.parse(JSON.stringify(state)),
    counters: { solutions: 0 },
  });

  function isSafe(row: number, col: number): boolean {
    for (let prevRow = 0; prevRow < row; prevRow++) {
      const prevCol = state.board[prevRow];
      // same column
      if (prevCol === col) return false;
      // diagonal
      if (Math.abs(prevCol - col) === Math.abs(prevRow - row)) return false;
    }
    return true;
  }

  function solve(row: number) {
    if (row === clampedN) {
      state.solutionsFound++;
      steps.push({
        stepIndex: steps.length,
        operationType: 'solution-found',
        description: `VALID SOLUTION #${state.solutionsFound} FOUND! All ${clampedN} queens placed peacefully without conflicts.`,
        codeLine: 3,
        state: JSON.parse(JSON.stringify(state)),
        counters: { solutions: state.solutionsFound },
      });
      return;
    }

    for (let col = 0; col < clampedN; col++) {
      state.currentRow = row;
      state.currentCol = col;
      state.board[row] = col;

      const safe = isSafe(row, col);
      state.isConflict = !safe;

      steps.push({
        stepIndex: steps.length,
        operationType: safe ? 'place-queen' : 'conflict',
        description: safe
          ? `Placed Queen at (row ${row}, col ${col}). No conflicts with previous rows. Recursing to row ${row + 1}.`
          : `Conflict at (row ${row}, col ${col}) with existing queen! Backtracking...`,
        codeLine: safe ? 6 : 7,
        state: JSON.parse(JSON.stringify(state)),
        counters: { solutions: state.solutionsFound },
      });

      if (safe) {
        solve(row + 1);
      }

      // Backtrack
      state.board[row] = -1;
      steps.push({
        stepIndex: steps.length,
        operationType: 'backtrack',
        description: `Backtracked: Removed Queen from (row ${row}, col ${col}). Trying next column.`,
        codeLine: 9,
        state: JSON.parse(JSON.stringify(state)),
        counters: { solutions: state.solutionsFound },
      });
    }
  }

  solve(0);

  steps.push({
    stepIndex: steps.length,
    operationType: 'complete',
    description: `N-Queens search complete! Found ${state.solutionsFound} total valid configuration(s) for ${clampedN}x${clampedN} board.`,
    codeLine: 11,
    state: JSON.parse(JSON.stringify(state)),
    counters: { solutions: state.solutionsFound },
  });

  return steps;
}
