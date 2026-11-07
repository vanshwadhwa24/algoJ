import { ExecutionStep } from '../types/execution';
import {
  generateBubbleSortSteps,
  generateSelectionSortSteps,
  generateInsertionSortSteps,
  generateMergeSortSteps,
  generateQuickSortSteps,
  generateHeapSortSteps,
} from './sorting/algorithms';
import { generateLinearSearchSteps, generateBinarySearchSteps } from './searching/algorithms';
import { executeArrayOperation, ArrayDSState } from './structures/array';
import { executeLinkedListOperation, LinkedListDSState } from './structures/linkedList';
import { executeStackOperation, StackDSState } from './structures/stack';
import { executeQueueOperation, QueueDSState } from './structures/queue';
import { executeHashTableOperation, HashTableDSState } from './structures/hashTable';
import { executeBSTOperation, BSTDSState } from './structures/bst';
import { executeHeapOperation, HeapDSState } from './structures/heap';
import { executeAVLOperation, AVLDSState } from './structures/avl';
import { executeTrieOperation, TrieDSState } from './structures/trie';
import { GraphData } from './structures/graph';
import { generateBFSSteps, generateDFSSteps } from './graphs/bfs';
import { generateDijkstraSteps, generateBellmanFordSteps, generateFloydWarshallSteps } from './graphs/shortestPath';
import { generateKruskalSteps, generatePrimSteps } from './graphs/mst';
import { generateFactorialSteps, generateFibonacciSteps, generateHanoiSteps, generateNQueensSteps } from './recursion/recursion';
import { generateKnapsackSteps, generateLCSSteps } from './dp/dp';

/**
 * Context configuration for generating fallback execution steps when the
 * authoritative Java Spring Boot backend is temporarily unreachable or offline.
 */
export interface FallbackAlgorithmContext {
  workingArray: number[];
  targetSearchVal?: number;
  graphData?: GraphData;
  factorialN?: number;
  fibN?: number;
  fibMode?: 'recursive' | 'memoized' | 'tabulated';
  hanoiDisks?: number;
  nQueensSize?: number;
  lcsS1?: string;
  lcsS2?: string;
  arrayDS?: ArrayDSState;
  linkedListDS?: LinkedListDSState;
  stackDS?: StackDSState;
  queueDS?: QueueDSState;
  hashTableDS?: HashTableDSState;
  bstDS?: BSTDSState;
  heapDS?: HeapDSState;
  avlDS?: AVLDSState;
  trieDS?: TrieDSState;
}

/**
 * Returns execution steps for any algorithm when running in offline fallback mode.
 * The Java Spring Boot backend remains the authoritative execution engine;
 * this function guarantees resilient client-side rendering if disconnected.
 */
export function getAlgorithmFallbackSteps(
  algorithmId: string,
  ctx: FallbackAlgorithmContext
): ExecutionStep[] {
  const arr = ctx.workingArray;
  const searchTarget = ctx.targetSearchVal ?? 22;

  switch (algorithmId) {
    // Sorting
    case 'bubble-sort': return generateBubbleSortSteps(arr);
    case 'selection-sort': return generateSelectionSortSteps(arr);
    case 'insertion-sort': return generateInsertionSortSteps(arr);
    case 'merge-sort': return generateMergeSortSteps(arr);
    case 'quick-sort': return generateQuickSortSteps(arr);
    case 'heap-sort': return generateHeapSortSteps(arr);

    // Searching
    case 'linear-search': return generateLinearSearchSteps(arr, searchTarget);
    case 'binary-search': return generateBinarySearchSteps(arr, searchTarget);

    // Linear Data Structures (Initial Inspection Fallbacks)
    case 'arrays':
      return ctx.arrayDS ? executeArrayOperation(ctx.arrayDS, { type: 'access', index: 0 }) : generateBubbleSortSteps(arr);
    case 'linked-lists':
      return ctx.linkedListDS ? executeLinkedListOperation(ctx.linkedListDS, { type: 'traverse' }) : generateBubbleSortSteps(arr);
    case 'stacks':
      return ctx.stackDS ? executeStackOperation(ctx.stackDS, { type: 'peek' }) : generateBubbleSortSteps(arr);
    case 'queues':
      return ctx.queueDS ? executeQueueOperation(ctx.queueDS, { type: 'peekFront' }) : generateBubbleSortSteps(arr);
    case 'hash-tables':
      return ctx.hashTableDS ? executeHashTableOperation(ctx.hashTableDS, { type: 'search', key: 'apple' }) : generateBubbleSortSteps(arr);

    // Trees & Heaps
    case 'binary-search-tree':
      return ctx.bstDS ? executeBSTOperation(ctx.bstDS, { type: 'inorder' }) : generateBubbleSortSteps(arr);
    case 'binary-heaps':
      return ctx.heapDS ? executeHeapOperation(ctx.heapDS, { type: 'peek' }) : generateBubbleSortSteps(arr);
    case 'avl-trees':
      return ctx.avlDS ? executeAVLOperation(ctx.avlDS, { type: 'search', value: 30 }) : generateBubbleSortSteps(arr);
    case 'tries':
      return ctx.trieDS ? executeTrieOperation(ctx.trieDS, { type: 'searchWord', word: 'cat' }) : generateBubbleSortSteps(arr);

    // Graphs
    case 'bfs': return ctx.graphData ? generateBFSSteps(ctx.graphData) : [];
    case 'dfs': return ctx.graphData ? generateDFSSteps(ctx.graphData) : [];
    case 'dijkstra': return ctx.graphData ? generateDijkstraSteps(ctx.graphData) : [];
    case 'bellman-ford': return ctx.graphData ? generateBellmanFordSteps(ctx.graphData) : [];
    case 'floyd-warshall': return ctx.graphData ? generateFloydWarshallSteps(ctx.graphData) : [];
    case 'kruskal': return ctx.graphData ? generateKruskalSteps(ctx.graphData) : [];
    case 'prim': return ctx.graphData ? generatePrimSteps(ctx.graphData) : [];

    // Recursion & DP
    case 'factorial': return generateFactorialSteps(ctx.factorialN ?? 4);
    case 'fibonacci': return generateFibonacciSteps(ctx.fibN ?? 6, ctx.fibMode ?? 'tabulated');
    case 'tower-of-hanoi': return generateHanoiSteps(ctx.hanoiDisks ?? 3);
    case 'n-queens': return generateNQueensSteps(ctx.nQueensSize ?? 4);
    case 'knapsack': return generateKnapsackSteps();
    case 'lcs': return generateLCSSteps(ctx.lcsS1 ?? 'STONE', ctx.lcsS2 ?? 'LONGEST');

    default: return generateBubbleSortSteps(arr);
  }
}

/**
 * Helper specifically for sorting algorithms (used in ComparisonWorkspace).
 */
export function getSortingFallbackSteps(algorithmId: string, arr: number[]): ExecutionStep[] {
  switch (algorithmId) {
    case 'bubble-sort': return generateBubbleSortSteps(arr);
    case 'selection-sort': return generateSelectionSortSteps(arr);
    case 'insertion-sort': return generateInsertionSortSteps(arr);
    case 'merge-sort': return generateMergeSortSteps(arr);
    case 'quick-sort': return generateQuickSortSteps(arr);
    case 'heap-sort': return generateHeapSortSteps(arr);
    default: return generateBubbleSortSteps(arr);
  }
}
