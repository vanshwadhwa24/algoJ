import { describe, it, expect } from 'vitest';
import React from 'react';
import { renderToString } from 'react-dom/server';
import { ALGORITHMS_CATALOG } from '../data/algorithmsCatalog';
import { getAlgorithmFallbackSteps } from './fallbackEngine';
import { createInitialArrayDS } from './structures/array';
import { createInitialLinkedList } from './structures/linkedList';
import { createInitialStack } from './structures/stack';
import { createInitialQueue } from './structures/queue';
import { createInitialHashTable } from './structures/hashTable';
import { createInitialBST } from './structures/bst';
import { createInitialHeap } from './structures/heap';
import { createInitialAVL } from './structures/avl';
import { createInitialTrie } from './structures/trie';
import { getSampleGraph } from './structures/graph';

// Import Visualizers
import { ArrayVisualizer } from '../components/visualizers/ArrayVisualizer';
import { LinkedListVisualizer } from '../components/visualizers/LinkedListVisualizer';
import { StackVisualizer } from '../components/visualizers/StackVisualizer';
import { QueueVisualizer } from '../components/visualizers/QueueVisualizer';
import { HashTableVisualizer } from '../components/visualizers/HashTableVisualizer';
import { TreeVisualizer } from '../components/visualizers/TreeVisualizer';
import { HeapDualVisualizer } from '../components/visualizers/HeapDualVisualizer';
import { TrieVisualizer } from '../components/visualizers/TrieVisualizer';
import { GraphVisualizer } from '../components/visualizers/GraphVisualizer';
import { RecursionVisualizer } from '../components/visualizers/RecursionVisualizer';
import { ChessboardVisualizer } from '../components/visualizers/ChessboardVisualizer';
import { DPTableVisualizer } from '../components/visualizers/DPTableVisualizer';
import { VisualizerView } from '../views/VisualizerView';

describe('Exhaustive Algorithm Step Rendering Suite', () => {
  const ctx = {
    workingArray: [35, 12, 48, 22, 60, 5],
    targetSearchVal: 22,
    graphData: getSampleGraph(),
    factorialN: 4,
    fibN: 6,
    fibMode: 'tabulated' as const,
    hanoiDisks: 3,
    nQueensSize: 4,
    lcsS1: 'STONE',
    lcsS2: 'LONGEST',
    arrayDS: createInitialArrayDS([10, 25, 40, 55, 70]),
    linkedListDS: createInitialLinkedList([15, 30, 45, 60], false),
    stackDS: createInitialStack([10, 20, 30], 8),
    queueDS: createInitialQueue([12, 24, 36], 6, false),
    hashTableDS: createInitialHashTable('chaining', 7),
    bstDS: createInitialBST([50, 30, 70, 20, 40, 60, 80]),
    heapDS: createInitialHeap('max', [85, 60, 70, 45, 50, 65, 30]),
    avlDS: createInitialAVL([30, 20, 40, 10, 25]),
    trieDS: createInitialTrie(['cat', 'car', 'cart', 'dog']),
  };

  for (const algo of ALGORITHMS_CATALOG) {
    it(`renders all steps for algorithm "${algo.id}" (${algo.category}) without errors`, () => {
      const steps = getAlgorithmFallbackSteps(algo.id, ctx);
      expect(steps.length).toBeGreaterThan(0);

      steps.forEach((step, stepIdx) => {
        const vizState: any = step.state;
        expect(vizState, `Step ${stepIdx} of ${algo.id} has no state`).toBeDefined();

        let element: React.ReactElement | null = null;

        if (algo.category === 'sorting' || algo.category === 'searching') {
          element = (
            <ArrayVisualizer
              elements={vizState.array}
              comparingIndices={vizState.comparingIndices}
              swappingIndices={vizState.swappingIndices}
              pivotIndex={vizState.pivotIndex}
              sortedIndices={vizState.sortedIndices}
              lowIndex={vizState.lowIndex}
              highIndex={vizState.highIndex}
              midIndex={vizState.midIndex}
              currentIndex={vizState.currentIndex}
              foundIndex={vizState.foundIndex}
            />
          );
        } else if (algo.id === 'arrays') {
          element = (
            <div>
              {vizState.cells?.map((cell: any) => (
                <div key={cell.index}>{cell.value}</div>
              ))}
            </div>
          );
        } else if (algo.id === 'linked-lists') {
          element = (
            <LinkedListVisualizer
              nodes={vizState.nodes}
              isDoubly={vizState.isDoubly}
              activeNodeId={vizState.activeNodeId}
            />
          );
        } else if (algo.id === 'stacks') {
          element = (
            <StackVisualizer
              items={vizState.items}
              maxCapacity={vizState.maxCapacity}
            />
          );
        } else if (algo.id === 'queues') {
          element = (
            <QueueVisualizer
              slots={vizState.slots}
              front={vizState.front}
              rear={vizState.rear}
              size={vizState.size}
              capacity={vizState.capacity}
              isCircular={vizState.isCircular}
            />
          );
        } else if (algo.id === 'hash-tables') {
          element = (
            <HashTableVisualizer
              buckets={vizState.buckets}
              collisionStrategy={vizState.collisionStrategy}
              activeHash={vizState.activeHash}
            />
          );
        } else if (algo.id === 'binary-search-tree') {
          element = (
            <TreeVisualizer
              root={vizState.root}
              traversalOutput={vizState.traversalOutput}
            />
          );
        } else if (algo.id === 'binary-heaps') {
          element = (
            <HeapDualVisualizer
              array={vizState.array}
              type={vizState.type}
              activeIndices={vizState.activeIndices}
              comparingIndices={vizState.comparingIndices}
            />
          );
        } else if (algo.id === 'avl-trees') {
          element = (
            <TreeVisualizer
              root={vizState.root}
              showAVLStats={true}
            />
          );
        } else if (algo.id === 'tries') {
          element = (
            <TrieVisualizer
              root={vizState.root}
              wordsList={vizState.wordsList}
              searchResults={vizState.searchResults}
            />
          );
        } else if (algo.category === 'graphs') {
          element = (
            <GraphVisualizer
              graph={vizState.graph}
              distances={vizState.distances}
            />
          );
        } else if (algo.id === 'factorial') {
          element = <RecursionVisualizer callStack={vizState.callStack} />;
        } else if (algo.id === 'fibonacci') {
          element = (
            <RecursionVisualizer
              memoTable={vizState.memoTable}
              dpArray={vizState.dpArray}
            />
          );
        } else if (algo.id === 'tower-of-hanoi') {
          element = <RecursionVisualizer hanoiPegs={vizState.pegs} />;
        } else if (algo.id === 'n-queens') {
          element = (
            <ChessboardVisualizer
              n={vizState.n}
              board={vizState.board}
              currentRow={vizState.currentRow}
              currentCol={vizState.currentCol}
              isConflict={vizState.isConflict}
              solutionsFound={vizState.solutionsFound}
            />
          );
        } else if (algo.id === 'knapsack') {
          element = (
            <DPTableVisualizer
              table={vizState.table}
              rowLabels={['0', 'Book(1)', 'Camera(2)', 'Laptop(3)', 'Tablet(4)']}
              colLabels={Array.from({ length: (vizState.capacity ?? 4) + 1 }, (_, i) => String(i))}
              currentRow={vizState.currentRow}
              currentCol={vizState.currentCol}
              activeDependencies={vizState.activeDependencies}
              optimalValue={vizState.optimalValue}
              selectedItems={vizState.selectedItems}
            />
          );
        } else if (algo.id === 'lcs') {
          element = (
            <DPTableVisualizer
              table={vizState.table}
              rowLabels={['Ø', ...(vizState.s1 || '').split('')]}
              colLabels={['Ø', ...(vizState.s2 || '').split('')]}
              currentRow={vizState.currentRow}
              currentCol={vizState.currentCol}
              activeDependencies={vizState.activeDependencies}
              reconstructedResult={vizState.lcsResult}
            />
          );
        }

        if (element) {
          const html = renderToString(element);
          expect(html.length, `Step ${stepIdx} of ${algo.id} rendered empty`).toBeGreaterThan(10);
        }
      });
    });
  }

  describe('Full VisualizerView Mounting Suite', () => {
    for (const algo of ALGORITHMS_CATALOG) {
      it(`mounts VisualizerView for algorithm "${algo.id}" without blank page or crash`, () => {
        const html = renderToString(
          <VisualizerView
            algorithmId={algo.id}
            onSelectAlgorithm={() => {}}
            userScore={0}
            onScoreUpdate={() => {}}
          />
        );
        expect(html.length).toBeGreaterThan(500);
        expect(html).toContain('Execution Canvas');
        // Ensure no error boundary fallback was triggered
        expect(html).not.toContain('Canvas Rendering Interrupted');
      });
    }
  });
});
