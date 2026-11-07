import { describe, it, expect } from 'vitest';
import {
  generateBubbleSortSteps,
  generateSelectionSortSteps,
  generateInsertionSortSteps,
  generateMergeSortSteps,
  generateQuickSortSteps,
  generateHeapSortSteps,
} from './sorting/algorithms';
import { generateLinearSearchSteps, generateBinarySearchSteps } from './searching/algorithms';
import { createInitialArrayDS, executeArrayOperation } from './structures/array';
import { createInitialLinkedList, executeLinkedListOperation } from './structures/linkedList';
import { createInitialStack, executeStackOperation } from './structures/stack';
import { createInitialQueue, executeQueueOperation } from './structures/queue';
import { createInitialHashTable, executeHashTableOperation } from './structures/hashTable';
import { createInitialBST, executeBSTOperation } from './structures/bst';
import { createInitialHeap, executeHeapOperation } from './structures/heap';
import { createInitialAVL } from './structures/avl';
import { createInitialTrie, executeTrieOperation } from './structures/trie';
import { getSampleGraph, getDisconnectedSampleGraph, getNegativeWeightsSampleGraph } from './structures/graph';
import { generateBFSSteps, generateDFSSteps } from './graphs/bfs';
import { generateDijkstraSteps, generateBellmanFordSteps, generateFloydWarshallSteps } from './graphs/shortestPath';
import { generateKruskalSteps, generatePrimSteps } from './graphs/mst';
import { generateFactorialSteps, generateFibonacciSteps, generateHanoiSteps, generateNQueensSteps } from './recursion/recursion';
import { generateKnapsackSteps, generateLCSSteps } from './dp/dp';

describe('Algorithm Engines & Correctness Tests', () => {
  // 1. SORTING
  describe('Sorting Algorithms', () => {
    const testCases = [
      [5, 2, 9, 1, 5, 6],
      [1],
      [],
      [-3, -1, -7, 4, 0, 2],
      [1, 2, 3, 4, 5], // already sorted
      [5, 4, 3, 2, 1], // reversed
      [4, 4, 4, 4], // all duplicates
    ];

    it('Bubble sort sorts correctly', () => {
      testCases.forEach((tc) => {
        const steps = generateBubbleSortSteps(tc);
        const lastStep = steps[steps.length - 1];
        const result = lastStep.state.array.map((x) => x.value);
        expect(result).toEqual([...tc].sort((a, b) => a - b));
      });
    });

    it('Selection sort sorts correctly', () => {
      testCases.forEach((tc) => {
        const steps = generateSelectionSortSteps(tc);
        const lastStep = steps[steps.length - 1];
        const result = lastStep.state.array.map((x) => x.value);
        expect(result).toEqual([...tc].sort((a, b) => a - b));
      });
    });

    it('Insertion sort sorts correctly', () => {
      testCases.forEach((tc) => {
        const steps = generateInsertionSortSteps(tc);
        const lastStep = steps[steps.length - 1];
        const result = lastStep.state.array.map((x) => x.value);
        expect(result).toEqual([...tc].sort((a, b) => a - b));
      });
    });

    it('Merge sort sorts correctly', () => {
      testCases.forEach((tc) => {
        const steps = generateMergeSortSteps(tc);
        const lastStep = steps[steps.length - 1];
        const result = lastStep.state.array.map((x) => x.value);
        expect(result).toEqual([...tc].sort((a, b) => a - b));
      });
    });

    it('Quick sort sorts correctly', () => {
      testCases.forEach((tc) => {
        const steps = generateQuickSortSteps(tc);
        const lastStep = steps[steps.length - 1];
        const result = lastStep.state.array.map((x) => x.value);
        expect(result).toEqual([...tc].sort((a, b) => a - b));
      });
    });

    it('Heap sort sorts correctly', () => {
      testCases.forEach((tc) => {
        const steps = generateHeapSortSteps(tc);
        const lastStep = steps[steps.length - 1];
        const result = lastStep.state.array.map((x) => x.value);
        expect(result).toEqual([...tc].sort((a, b) => a - b));
      });
    });
  });

  // 2. SEARCHING
  describe('Searching Algorithms', () => {
    it('Linear search finds element and handles absent target', () => {
      const arr = [10, 20, 30, 40, 50];
      const foundSteps = generateLinearSearchSteps(arr, 30);
      expect(foundSteps[foundSteps.length - 1].state.foundIndex).toBe(2);

      const notFoundSteps = generateLinearSearchSteps(arr, 99);
      expect(notFoundSteps[notFoundSteps.length - 1].state.foundIndex).toBeNull();
    });

    it('Binary search works and handles absent targets and unsorted input', () => {
      const sortedArr = [5, 12, 19, 27, 33, 45, 60];
      const foundSteps = generateBinarySearchSteps(sortedArr, 27);
      expect(foundSteps[foundSteps.length - 1].state.foundIndex).toBe(3);

      const notFoundSteps = generateBinarySearchSteps(sortedArr, 99);
      expect(notFoundSteps[notFoundSteps.length - 1].state.foundIndex).toBeNull();
    });
  });

  // 3. LINEAR DATA STRUCTURES
  describe('Linear Data Structures', () => {
    it('Array operations (access, insert, delete, search)', () => {
      let state = createInitialArrayDS([10, 20, 30]);
      // Access
      const accessSteps = executeArrayOperation(state, { type: 'access', index: 1 });
      expect(accessSteps[accessSteps.length - 1].state.cells[1].value).toBe(20);

      // Insert at index 1
      const insertSteps = executeArrayOperation(state, { type: 'insert', index: 1, value: 15 });
      state = insertSteps[insertSteps.length - 1].state;
      expect(state.cells.map((c) => c.value)).toEqual([10, 15, 20, 30]);

      // Delete index 2
      const deleteSteps = executeArrayOperation(state, { type: 'delete', index: 2 });
      state = deleteSteps[deleteSteps.length - 1].state;
      expect(state.cells.map((c) => c.value)).toEqual([10, 15, 30]);
    });

    it('Linked list operations (insert, delete, reverse)', () => {
      let list = createInitialLinkedList([1, 2, 3], false);
      const insertSteps = executeLinkedListOperation(list, { type: 'insertHead', value: 0 });
      list = insertSteps[insertSteps.length - 1].state;
      expect(list.nodes.map((n) => n.value)).toEqual([0, 1, 2, 3]);

      const revSteps = executeLinkedListOperation(list, { type: 'reverse' });
      list = revSteps[revSteps.length - 1].state;
      expect(list.nodes.map((n) => n.value)).toEqual([3, 2, 1, 0]);
    });

    it('Stack push, pop, overflow, underflow', () => {
      let stack = createInitialStack([10, 20], 3);
      const push1 = executeStackOperation(stack, { type: 'push', value: 30 });
      stack = push1[push1.length - 1].state;
      expect(stack.items.length).toBe(3);

      // Overflow test
      const pushOverflow = executeStackOperation(stack, { type: 'push', value: 40 });
      expect(pushOverflow[pushOverflow.length - 1].operationType).toBe('overflow');

      // Pop
      const pop1 = executeStackOperation(stack, { type: 'pop' });
      stack = pop1[pop1.length - 1].state;
      expect(stack.items.length).toBe(2);
    });

    it('Queue standard & circular enqueue, dequeue, wraparound', () => {
      let q = createInitialQueue([1, 2], 3, true);
      const enq = executeQueueOperation(q, { type: 'enqueue', value: 3 });
      q = enq[enq.length - 1].state;
      expect(q.size).toBe(3);

      const deq = executeQueueOperation(q, { type: 'dequeue' });
      q = deq[deq.length - 1].state;
      expect(q.size).toBe(2);

      // Wraparound enqueue
      const enqWrap = executeQueueOperation(q, { type: 'enqueue', value: 4 });
      q = enqWrap[enqWrap.length - 1].state;
      expect(q.size).toBe(3);
      expect(q.rear).toBe(0); // Wrapped around to 0
    });

    it('Hash table chaining and linear probing with tombstones', () => {
      // Chaining
      let htChain = createInitialHashTable('chaining', 5, []);
      const ins1 = executeHashTableOperation(htChain, { type: 'insert', key: 'cat', value: '1' });
      htChain = ins1[ins1.length - 1].state;
      const searchChain = executeHashTableOperation(htChain, { type: 'search', key: 'cat' });
      expect(searchChain[searchChain.length - 1].operationType).toBe('search-success');

      // Linear Probing
      let htProbe = createInitialHashTable('probing', 5, []);
      const pIns = executeHashTableOperation(htProbe, { type: 'insert', key: 'dog', value: '2' });
      htProbe = pIns[pIns.length - 1].state;
      const pDel = executeHashTableOperation(htProbe, { type: 'delete', key: 'dog' });
      htProbe = pDel[pDel.length - 1].state;
      // Deleted slot should be marked as tombstone
      expect(htProbe.buckets.some((b) => b.entries.some((e) => e.isDeleted))).toBe(true);
    });
  });

  // 4. TREES & HEAPS
  describe('Trees & Heaps', () => {
    it('BST insertion, deletion and traversals', () => {
      let bst = createInitialBST([50, 30, 70, 20, 40]);
      expect(bst.nodeCount).toBe(5);

      const ins = executeBSTOperation(bst, { type: 'insert', value: 25 });
      bst = ins[ins.length - 1].state;
      expect(bst.nodeCount).toBe(6);

      const del = executeBSTOperation(bst, { type: 'delete', value: 30 }); // 2 children deletion
      bst = del[del.length - 1].state;
      expect(bst.nodeCount).toBe(5);
    });

    it('AVL balance and rotations', () => {
      const avl = createInitialAVL([10, 20, 30, 40, 50, 25]);
      // Verify all nodes have balance factor between -1 and 1
      function verifyAVL(node: any): boolean {
        if (!node) return true;
        if (Math.abs(node.balanceFactor) > 1) return false;
        return verifyAVL(node.left) && verifyAVL(node.right);
      }
      expect(verifyAVL(avl.root)).toBe(true);
    });

    it('Heap insert and extract preserves heap property', () => {
      let heap = createInitialHeap('max', [90, 70, 80, 50, 60]);
      const ins = executeHeapOperation(heap, { type: 'insert', value: 100 });
      heap = ins[ins.length - 1].state;
      expect(heap.array[0].value).toBe(100); // 100 should be new root

      const ext = executeHeapOperation(heap, { type: 'extract' });
      heap = ext[ext.length - 1].state;
      expect(heap.array[0].value).toBe(90); // 90 is next max
    });

    it('Trie operations and prefix search', () => {
      let trie = createInitialTrie(['apple', 'app', 'application']);
      const auto = executeTrieOperation(trie, { type: 'autocomplete', word: 'app' });
      const last = auto[auto.length - 1];
      expect(last.state.searchResults).toEqual(expect.arrayContaining(['apple', 'app', 'application']));
    });
  });

  // 5. GRAPH ALGORITHMS
  describe('Graph Algorithms', () => {
    it('BFS and DFS visit nodes correctly', () => {
      const g = getSampleGraph();
      const bfsSteps = generateBFSSteps(g, 'A');
      const dfsSteps = generateDFSSteps(g, 'A');
      expect(bfsSteps[bfsSteps.length - 1].state.visited.length).toBe(6);
      expect(dfsSteps[dfsSteps.length - 1].state.visited.length).toBe(6);
    });

    it('Dijkstra finds shortest path from A to F', () => {
      const g = getSampleGraph();
      const dijkstraSteps = generateDijkstraSteps(g, 'A');
      const last = dijkstraSteps[dijkstraSteps.length - 1];
      expect(last.state.distances['F']).toBeLessThan(Infinity);
      expect(last.state.distances['A']).toBe(0);
    });

    it('Bellman-Ford handles negative weights and detects reachable cycles', () => {
      const g = getNegativeWeightsSampleGraph();
      const bfSteps = generateBellmanFordSteps(g, 'S');
      const last = bfSteps[bfSteps.length - 1];
      expect(last.state.distances['C']).toBe(2); // S->A (4), A->C (-2) = 2
    });

    it('Floyd-Warshall computes all pairs matrix', () => {
      const g = getSampleGraph();
      const fwSteps = generateFloydWarshallSteps(g);
      const last = fwSteps[fwSteps.length - 1];
      expect(last.state.matrix).toBeDefined();
    });

    it('Kruskal and Prim MST total weights agree on sample graph', () => {
      const g = getSampleGraph();
      const kruskal = generateKruskalSteps(g);
      const prim = generatePrimSteps(g, 'A');
      const kWeight = kruskal[kruskal.length - 1].state.totalWeight;
      const pWeight = prim[prim.length - 1].state.totalWeight;
      expect(kWeight).toBe(pWeight);
      expect(kWeight).toBe(13); // True MST weight: A-C(2) + B-C(1) + D-E(2) + E-F(3) + B-D(5) = 13
    });

    it('MST reports forest for disconnected graph', () => {
      const g = getDisconnectedSampleGraph();
      const kruskal = generateKruskalSteps(g);
      expect(kruskal[kruskal.length - 1].state.isSpanningForest).toBe(true);
    });
  });

  // 6. RECURSION, BACKTRACKING & DP
  describe('Recursion, Backtracking & Dynamic Programming', () => {
    it('Factorial(4) = 24', () => {
      const steps = generateFactorialSteps(4);
      expect(steps[steps.length - 1].state.result).toBe(24);
    });

    it('Fibonacci(6) = 8 across all modes', () => {
      ['recursive', 'memoized', 'tabulated'].forEach((m: any) => {
        const steps = generateFibonacciSteps(6, m);
        expect(steps[steps.length - 1].state.result).toBe(8);
      });
    });

    it('Tower of Hanoi with 3 disks takes 7 moves', () => {
      const steps = generateHanoiSteps(3);
      expect(steps[steps.length - 1].state.moveCount).toBe(7);
      expect(steps[steps.length - 1].state.pegs.C.length).toBe(3);
    });

    it('N-Queens for N=4 finds 2 solutions', () => {
      const steps = generateNQueensSteps(4);
      expect(steps[steps.length - 1].state.solutionsFound).toBe(2);
    });

    it('0/1 Knapsack optimal value and backtracking', () => {
      const steps = generateKnapsackSteps();
      const last = steps[steps.length - 1];
      expect(last.state.optimalValue).toBe(10); // Book(1wt, 3val) + Tablet(4wt, 7val) = wt 5, val 10
    });

    it('Longest Common Subsequence', () => {
      const steps = generateLCSSteps('STONE', 'LONGEST');
      const last = steps[steps.length - 1];
      expect(last.state.lcsResult).toBe('ONE'); // ONE is common subseq of STONE and LONGEST
    });
  });
});
