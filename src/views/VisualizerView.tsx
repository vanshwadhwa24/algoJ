import React, { useState, useMemo, useEffect, useCallback } from 'react';
import {
  Sliders,
  Code2,
  BookOpen,
  HelpCircle,
} from 'lucide-react';
import { ALGORITHMS_CATALOG } from '../data/algorithmsCatalog';
import { ExecutionStep } from '../types/execution';
import { PRACTICE_QUESTIONS } from '../data/practiceQuestions';

// Java Spring Boot API Client
import {
  checkBackendHealth,
  runAlgorithmOnJavaBackend,
  operateStructureOnJavaBackend,
} from '../api/algoService';

// Consolidated Fallback Engine
import { getAlgorithmFallbackSteps } from '../engine/fallbackEngine';

// Playback & Step Generators
import { usePlayback } from '../engine/playback';
import { createInitialArrayDS, executeArrayOperation, ArrayDSState } from '../engine/structures/array';
import { createInitialLinkedList, executeLinkedListOperation, LinkedListDSState } from '../engine/structures/linkedList';
import { createInitialStack, executeStackOperation, StackDSState } from '../engine/structures/stack';
import { createInitialQueue, executeQueueOperation, QueueDSState } from '../engine/structures/queue';
import { createInitialHashTable, executeHashTableOperation, HashTableDSState } from '../engine/structures/hashTable';
import { createInitialBST, executeBSTOperation, BSTDSState } from '../engine/structures/bst';
import { createInitialHeap, executeHeapOperation, HeapDSState } from '../engine/structures/heap';
import { createInitialAVL, executeAVLOperation, AVLDSState } from '../engine/structures/avl';
import { createInitialTrie, executeTrieOperation, TrieDSState } from '../engine/structures/trie';
import { getSampleGraph, getDisconnectedSampleGraph, getNegativeWeightsSampleGraph, GraphData } from '../engine/structures/graph';

// Visualizers
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

// Workspace supporting components
import { PlaybackControls } from '../components/workspace/PlaybackControls';
import { ExecutionTimeline } from '../components/workspace/ExecutionTimeline';
import { CodePanel } from '../components/workspace/CodePanel';
import { ComplexityPanel } from '../components/workspace/ComplexityPanel';
import { OperationCounter } from '../components/workspace/OperationCounter';
import { LearningPanel } from '../components/learning/LearningPanel';
import { PracticeMode } from '../components/learning/PracticeMode';
import { ErrorBoundary } from '../components/common/ErrorBoundary';

interface VisualizerViewProps {
  algorithmId: string;
  onSelectAlgorithm: (id: string) => void;
  userScore: number;
  onScoreUpdate: (score: number) => void;
}

export const VisualizerView: React.FC<VisualizerViewProps> = ({
  algorithmId,
  onSelectAlgorithm,
  userScore,
  onScoreUpdate,
}) => {
  const [activeTab, setActiveTab] = useState<'visual' | 'learn' | 'practice'>('visual');

  // Algorithm info
  const algorithm =
    ALGORITHMS_CATALOG.find((a) => a.id === algorithmId) || ALGORITHMS_CATALOG[0];

  // Specific state controllers for custom inputs
  const [arrayInputStr, setArrayInputStr] = useState('35, 12, 48, 22, 60, 5');
  const [targetSearchVal, setTargetSearchVal] = useState<number>(22);

  // Linear data structures internal persistent instances
  const [arrayDS, setArrayDS] = useState<ArrayDSState>(() => createInitialArrayDS([10, 25, 40, 55, 70]));
  const [linkedListDS, setLinkedListDS] = useState<LinkedListDSState>(() => createInitialLinkedList([15, 30, 45, 60], false));
  const [stackDS, setStackDS] = useState<StackDSState>(() => createInitialStack([10, 20, 30], 8));
  const [queueDS, setQueueDS] = useState<QueueDSState>(() => createInitialQueue([12, 24, 36], 6, false));
  const [hashTableDS, setHashTableDS] = useState<HashTableDSState>(() => createInitialHashTable('chaining', 7));
  const [bstDS, setBstDS] = useState<BSTDSState>(() => createInitialBST([50, 30, 70, 20, 40, 60, 80]));
  const [heapDS, setHeapDS] = useState<HeapDSState>(() => createInitialHeap('max', [85, 60, 70, 45, 50, 65, 30]));
  const [avlDS, setAvlDS] = useState<AVLDSState>(() => createInitialAVL([30, 20, 40, 10, 25]));
  const [trieDS, setTrieDS] = useState<TrieDSState>(() => createInitialTrie(['cat', 'car', 'cart', 'dog']));
  const [graphData, setGraphData] = useState<GraphData>(() => getSampleGraph());

  // Input states for operations
  const [dsInputValue, setDsInputValue] = useState<string>('50');
  const [dsInputIndex, setDsInputIndex] = useState<string>('2');
  const [hashKey, setHashKey] = useState<string>('grape');
  const [hashVal, setHashVal] = useState<string>('500');

  // Recursion / Backtracking / DP inputs
  const [factorialN, setFactorialN] = useState<number>(4);
  const [fibMode, setFibMode] = useState<'recursive' | 'memoized' | 'tabulated'>('tabulated');
  const [fibN, setFibN] = useState<number>(6);
  const [hanoiDisks, setHanoiDisks] = useState<number>(3);
  const [nQueensSize, setNQueensSize] = useState<number>(4);
  const [lcsS1, setLcsS1] = useState<string>('STONE');
  const [lcsS2, setLcsS2] = useState<string>('LONGEST');

  // Trigger generator recalculation
  const [actionCounter, setActionCounter] = useState(0);

  const [backendStatus, setBackendStatus] = useState<'checking' | 'connected' | 'offline'>('checking');
  const [backendInfo, setBackendInfo] = useState<string>('Spring Boot 3.2 · Java 17');

  // Verify Java backend health on mount
  useEffect(() => {
    checkBackendHealth().then((health) => {
      if (health && health.status === 'UP') {
        setBackendStatus('connected');
        if (health.javaVersion) {
          setBackendInfo(`Spring Boot · OpenJDK ${health.javaVersion}`);
        }
      } else {
        setBackendStatus('offline');
      }
    });
  }, []);

  // Local fallback step generator if backend is unavailable
  const getFallback = useCallback(
    (workingArr: number[]) => {
      return getAlgorithmFallbackSteps(algorithm.id, {
        workingArray: workingArr,
        targetSearchVal,
        graphData,
        factorialN,
        fibN,
        fibMode,
        hanoiDisks,
        nQueensSize,
        lcsS1,
        lcsS2,
        arrayDS,
        linkedListDS,
        stackDS,
        queueDS,
        hashTableDS,
        bstDS,
        heapDS,
        avlDS,
        trieDS,
      });
    },
    [
      algorithm.id,
      targetSearchVal,
      graphData,
      factorialN,
      fibN,
      fibMode,
      hanoiDisks,
      nQueensSize,
      lcsS1,
      lcsS2,
      arrayDS,
      linkedListDS,
      stackDS,
      queueDS,
      hashTableDS,
      bstDS,
      heapDS,
      avlDS,
      trieDS,
    ]
  );

  const getWorkingArray = useCallback(() => {
    const rawArray = arrayInputStr
      .split(',')
      .map((s) => Number(s.trim()))
      .filter((n) => !isNaN(n));
    return rawArray.length > 0 ? rawArray : [35, 12, 48, 22, 60, 5];
  }, [arrayInputStr]);

  // Track currently active algorithm to immediately align state during transitions
  const [currentAlgoId, setCurrentAlgoId] = useState(algorithm.id);

  // Execution steps state (authoritative from Java backend, fallback to local if offline)
  const [steps, setSteps] = useState<ExecutionStep[]>(() => {
    const initialArr = [35, 12, 48, 22, 60, 5];
    return getAlgorithmFallbackSteps(algorithm.id, {
      workingArray: initialArr,
      targetSearchVal: 22,
      graphData: getSampleGraph(),
      factorialN: 4,
      fibN: 6,
      fibMode: 'tabulated',
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
    });
  });

  // If algorithm.id changes, synchronously regenerate steps for the new algorithm during render
  if (currentAlgoId !== algorithm.id) {
    setCurrentAlgoId(algorithm.id);
    const workingArr = getWorkingArray();
    const immediateSteps = getFallback(workingArr);
    setSteps(immediateSteps);
  }

  // Asynchronously query Java Spring Boot backend for execution trace
  useEffect(() => {
    let isCancelled = false;

    async function loadJavaTrace() {
      const rawArray = arrayInputStr
        .split(',')
        .map((s) => Number(s.trim()))
        .filter((n) => !isNaN(n));
      const workingArr = rawArray.length > 0 ? rawArray : [35, 12, 48, 22, 60, 5];

      // Request authoritative algorithm execution from Java
      const javaTrace = await runAlgorithmOnJavaBackend({
        algorithmId: algorithm.id,
        array: workingArr,
        target: targetSearchVal,
        n:
          algorithm.id === 'factorial'
            ? factorialN
            : algorithm.id === 'fibonacci'
            ? fibN
            : algorithm.id === 'n-queens'
            ? nQueensSize
            : 4,
        mode: fibMode,
        disks: hanoiDisks,
        text1: lcsS1,
        text2: lcsS2,
        graph: graphData,
      });

      if (!isCancelled) {
        if (javaTrace && javaTrace.length > 0) {
          setSteps(javaTrace);
          setBackendStatus('connected');
        } else {
          // Gracefully fallback to client generator if offline
          const fallback = getFallback(workingArr);
          setSteps(fallback);
          setBackendStatus('offline');
        }
      }
    }

    loadJavaTrace();
    return () => {
      isCancelled = true;
    };
  }, [
    algorithm.id,
    arrayInputStr,
    targetSearchVal,
    actionCounter,
    factorialN,
    fibMode,
    fibN,
    hanoiDisks,
    nQueensSize,
    lcsS1,
    lcsS2,
    graphData,
    getFallback,
  ]);

  const playback = usePlayback({ steps, initialSpeed: 1 });
  const currentStep = playback.currentStep;

  // Questions for current algorithm
  const currentQuestions = useMemo(() => {
    return PRACTICE_QUESTIONS.filter((q) => q.algorithmId === algorithm.id);
  }, [algorithm.id]);

  // Unified executor for interactive Data Structure operations
  const performStructureOp = async <T,>(
    structureId: string,
    operation: string,
    payload: Record<string, any>,
    currentState: T,
    setState: React.Dispatch<React.SetStateAction<T>>,
    fallbackFn: () => ExecutionStep[]
  ) => {
    const javaSteps = await operateStructureOnJavaBackend({
      structureId,
      operation,
      ...payload,
      state: currentState,
    });

    if (javaSteps && javaSteps.length > 0) {
      setSteps(javaSteps);
      setState(javaSteps[javaSteps.length - 1].state as T);
      setBackendStatus('connected');
    } else {
      const fallbackSteps = fallbackFn();
      setSteps(fallbackSteps);
      setState(fallbackSteps[fallbackSteps.length - 1].state as T);
      setBackendStatus('offline');
    }
    setActionCounter((c) => c + 1);
  };

  // Handlers for Data Structure Operations
  const handleArrayAction = (type: 'access' | 'update' | 'insert' | 'delete' | 'search' | 'traverse') => {
    const idx = Number(dsInputIndex);
    const val = Number(dsInputValue);
    performStructureOp('arrays', type, { index: idx, value: val }, arrayDS, setArrayDS, () =>
      executeArrayOperation(arrayDS, { type, index: idx, value: val })
    );
  };

  const handleLinkedListAction = (
    type: 'insertHead' | 'insertTail' | 'insertAt' | 'deleteHead' | 'deleteTail' | 'deleteAt' | 'reverse' | 'search' | 'traverse'
  ) => {
    const val = Number(dsInputValue);
    const pos = Number(dsInputIndex);
    performStructureOp('linked-lists', type, { value: val, position: pos }, linkedListDS, setLinkedListDS, () =>
      executeLinkedListOperation(linkedListDS, { type, value: val, position: pos })
    );
  };

  const handleStackAction = (type: 'push' | 'pop' | 'peek' | 'isEmpty') => {
    const val = Number(dsInputValue);
    performStructureOp('stacks', type, { value: val }, stackDS, setStackDS, () =>
      executeStackOperation(stackDS, { type, value: val })
    );
  };

  const handleQueueAction = (type: 'enqueue' | 'dequeue' | 'peekFront' | 'inspectRear' | 'isEmpty' | 'isFull') => {
    const val = Number(dsInputValue);
    performStructureOp('queues', type, { value: val }, queueDS, setQueueDS, () =>
      executeQueueOperation(queueDS, { type, value: val })
    );
  };

  const handleHashTableAction = (type: 'insert' | 'search' | 'delete') => {
    performStructureOp('hash-tables', type, { key: hashKey, value: hashVal }, hashTableDS, setHashTableDS, () =>
      executeHashTableOperation(hashTableDS, { type, key: hashKey, value: hashVal })
    );
  };

  const handleBSTAction = (type: 'insert' | 'delete' | 'search' | 'findMin' | 'findMax' | 'preorder' | 'inorder' | 'postorder' | 'levelorder') => {
    const val = Number(dsInputValue);
    performStructureOp('binary-search-tree', type, { value: val }, bstDS, setBstDS, () =>
      executeBSTOperation(bstDS, { type, value: val })
    );
  };

  const handleHeapAction = (type: 'insert' | 'extract' | 'peek' | 'build') => {
    const val = Number(dsInputValue);
    performStructureOp('binary-heaps', type, { value: val }, heapDS, setHeapDS, () =>
      executeHeapOperation(heapDS, { type, value: val })
    );
  };

  const handleAVLAction = (type: 'insert' | 'search' | 'delete') => {
    const val = Number(dsInputValue);
    performStructureOp('avl-trees', type, { value: val }, avlDS, setAvlDS, () =>
      executeAVLOperation(avlDS, { type, value: val })
    );
  };

  const handleTrieAction = (type: 'insert' | 'searchWord' | 'searchPrefix' | 'delete' | 'autocomplete') => {
    performStructureOp('tries', type, { word: hashKey }, trieDS, setTrieDS, () =>
      executeTrieOperation(trieDS, { type, word: hashKey })
    );
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto px-4 md:px-8 py-4">
      {/* Top Workspace Header Bar */}
      <div className="bg-white border border-[#E8DCC4] rounded-[28px] p-5 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] flex flex-wrap items-center justify-between gap-4">
        {/* Selector & Title */}
        <div className="flex items-center gap-3">
          <select
            value={algorithm.id}
            onChange={(e) => onSelectAlgorithm(e.target.value)}
            className="font-black text-base sm:text-lg bg-[#F7EFE1] border border-[#E8DCC4] rounded-2xl px-4 py-2 text-[#2B2620] focus:outline-none shadow-[0_3px_10px_rgba(43,38,32,0.16)] cursor-pointer"
          >
            {ALGORITHMS_CATALOG.map((a) => (
              <option key={a.id} value={a.id}>
                {a.name} ({a.category})
              </option>
            ))}
          </select>

          <span className="hidden sm:inline-block bg-[#9CCBA2] border border-[#2B2620] px-3 py-1 rounded-full text-xs font-black uppercase text-[#2B2620]">
            {algorithm.difficulty}
          </span>

          {backendStatus === 'connected' ? (
            <span className="inline-flex items-center gap-1.5 bg-[#9CCBA2] border-[1.5px] border-[#2B2620] px-3 py-1 rounded-full text-xs font-bold text-[#2B2620] shadow-[0_2px_6px_rgba(43,38,32,0.18)]">
              <span className="w-2 h-2 rounded-full bg-[#107C41] animate-pulse"></span>
              <span>Java Backend ({backendInfo})</span>
            </span>
          ) : (
            <span className="inline-flex items-center gap-1.5 bg-[#FFDE85] border-[1.5px] border-[#2B2620] px-3 py-1 rounded-full text-xs font-bold text-[#2B2620] shadow-[0_2px_6px_rgba(43,38,32,0.18)]">
              <span className="w-2 h-2 rounded-full bg-[#E65100]"></span>
              <span>Local Fallback (Offline)</span>
            </span>
          )}
        </div>

        {/* View Switcher Pills */}
        <div className="flex items-center gap-1.5 bg-[#F7EFE1] p-1 rounded-full border border-[#E8DCC4]">
          <button
            onClick={() => setActiveTab('visual')}
            className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-xs font-black transition-all cursor-pointer ${
              activeTab === 'visual'
                ? 'bg-[#2B2620] text-white shadow-[0_3px_10px_rgba(43,38,32,0.16)]'
                : 'text-[#2B2620] hover:bg-white'
            }`}
          >
            <Code2 className="w-3.5 h-3.5" />
            <span>Interactive Canvas</span>
          </button>

          <button
            onClick={() => setActiveTab('learn')}
            className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-xs font-black transition-all cursor-pointer ${
              activeTab === 'learn'
                ? 'bg-[#2B2620] text-white shadow-[0_3px_10px_rgba(43,38,32,0.16)]'
                : 'text-[#2B2620] hover:bg-white'
            }`}
          >
            <BookOpen className="w-3.5 h-3.5" />
            <span>Theory & Notes</span>
          </button>

          <button
            onClick={() => setActiveTab('practice')}
            className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-xs font-black transition-all cursor-pointer ${
              activeTab === 'practice'
                ? 'bg-[#2B2620] text-white shadow-[0_3px_10px_rgba(43,38,32,0.16)]'
                : 'text-[#2B2620] hover:bg-white'
            }`}
          >
            <HelpCircle className="w-3.5 h-3.5" />
            <span>Practice ({currentQuestions.length})</span>
          </button>
        </div>
      </div>

      {/* Main Workspace Body */}
      {activeTab === 'visual' && (
        <div className="space-y-6">
          {/* Custom Input Controls Strip */}
          <div className="bg-white border border-[#E8DCC4] rounded-[24px] p-4 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] flex flex-wrap items-center gap-3">
            <span className="text-xs font-black uppercase text-gray-500 flex items-center gap-1">
              <Sliders className="w-3.5 h-3.5" /> Input Controls:
            </span>

            {/* Sorting / Searching inputs */}
            {(algorithm.category === 'sorting' || algorithm.category === 'searching') && (
              <>
                <input
                  type="text"
                  value={arrayInputStr}
                  onChange={(e) => setArrayInputStr(e.target.value)}
                  placeholder="e.g. 10, 20, 5, 40"
                  className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl px-3 py-1 text-xs font-mono font-bold text-[#2B2620] min-w-[220px]"
                />

                {algorithm.category === 'searching' && (
                  <div className="flex items-center gap-1.5">
                    <span className="text-xs font-bold text-gray-600">Target:</span>
                    <input
                      type="number"
                      value={targetSearchVal}
                      onChange={(e) => setTargetSearchVal(Number(e.target.value))}
                      className="w-16 bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl px-2 py-1 text-xs font-mono font-bold text-[#2B2620]"
                    />
                  </div>
                )}

                <button
                  onClick={() => {
                    const rnd = Array.from({ length: 8 }, () => Math.floor(Math.random() * 90 + 10));
                    setArrayInputStr(rnd.join(', '));
                    if (algorithm.category === 'searching') {
                      setTargetSearchVal(rnd[Math.floor(Math.random() * rnd.length)]);
                    }
                  }}
                  className="px-3 py-1 bg-[#8FB5AD] text-[#2B2620] text-xs font-black rounded-xl border border-[#E8DCC4] shadow-[0_3px_10px_rgba(43,38,32,0.16)] cursor-pointer"
                >
                  Randomize
                </button>
              </>
            )}

            {/* Array DS Operations */}
            {algorithm.id === 'arrays' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Index"
                  value={dsInputIndex}
                  onChange={(e) => setDsInputIndex(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <input
                  type="number"
                  placeholder="Value"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleArrayAction('access')} className="px-2.5 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Access</button>
                <button onClick={() => handleArrayAction('update')} className="px-2.5 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Update</button>
                <button onClick={() => handleArrayAction('insert')} className="px-2.5 py-1 bg-[#8FB5AD] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Insert</button>
                <button onClick={() => handleArrayAction('delete')} className="px-2.5 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Delete</button>
                <button onClick={() => handleArrayAction('traverse')} className="px-2.5 py-1 bg-white border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Traverse</button>
              </div>
            )}

            {/* Linked Lists Operations */}
            {algorithm.id === 'linked-lists' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Value"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleLinkedListAction('insertHead')} className="px-2 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">+ Head</button>
                <button onClick={() => handleLinkedListAction('insertTail')} className="px-2 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">+ Tail</button>
                <button onClick={() => handleLinkedListAction('deleteHead')} className="px-2 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">- Head</button>
                <button onClick={() => handleLinkedListAction('deleteTail')} className="px-2 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">- Tail</button>
                <button onClick={() => handleLinkedListAction('reverse')} className="px-2 py-1 bg-[#8FB5AD] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Reverse</button>
                <button
                  onClick={() => {
                    setLinkedListDS(createInitialLinkedList([10, 20, 30, 40], !linkedListDS.isDoubly));
                    setActionCounter((c) => c + 1);
                  }}
                  className="px-2.5 py-1 bg-[#2B2620] text-white rounded-lg text-xs font-bold cursor-pointer"
                >
                  Mode: {linkedListDS.isDoubly ? 'Doubly' : 'Singly'}
                </button>
              </div>
            )}

            {/* Stack Operations */}
            {algorithm.id === 'stacks' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Value"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleStackAction('push')} className="px-3 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Push</button>
                <button onClick={() => handleStackAction('pop')} className="px-3 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Pop</button>
                <button onClick={() => handleStackAction('peek')} className="px-3 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Peek</button>
                <button onClick={() => handleStackAction('isEmpty')} className="px-3 py-1 bg-[#F7EFE1] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">isEmpty</button>
              </div>
            )}

            {/* Queue Operations */}
            {algorithm.id === 'queues' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Value"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleQueueAction('enqueue')} className="px-3 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Enqueue</button>
                <button onClick={() => handleQueueAction('dequeue')} className="px-3 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Dequeue</button>
                <button onClick={() => handleQueueAction('peekFront')} className="px-3 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Peek Front</button>
                <button
                  onClick={() => {
                    setQueueDS(createInitialQueue([15, 30, 45], 6, !queueDS.isCircular));
                    setActionCounter((c) => c + 1);
                  }}
                  className="px-2.5 py-1 bg-[#2B2620] text-white rounded-lg text-xs font-bold cursor-pointer"
                >
                  Type: {queueDS.isCircular ? 'Circular' : 'Standard'}
                </button>
              </div>
            )}

            {/* Hash Table Operations */}
            {algorithm.id === 'hash-tables' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="text"
                  placeholder="Key"
                  value={hashKey}
                  onChange={(e) => setHashKey(e.target.value)}
                  className="w-20 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <input
                  type="text"
                  placeholder="Val"
                  value={hashVal}
                  onChange={(e) => setHashVal(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleHashTableAction('insert')} className="px-2.5 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Insert</button>
                <button onClick={() => handleHashTableAction('search')} className="px-2.5 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Search</button>
                <button onClick={() => handleHashTableAction('delete')} className="px-2.5 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Delete</button>
                <button
                  onClick={() => {
                    setHashTableDS(createInitialHashTable(hashTableDS.collisionStrategy === 'chaining' ? 'probing' : 'chaining', 7));
                    setActionCounter((c) => c + 1);
                  }}
                  className="px-2.5 py-1 bg-[#2B2620] text-white rounded-lg text-xs font-bold cursor-pointer"
                >
                  Strategy: {hashTableDS.collisionStrategy === 'chaining' ? 'Chaining' : 'Probing'}
                </button>
              </div>
            )}

            {/* BST Operations */}
            {algorithm.id === 'binary-search-tree' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Value"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleBSTAction('insert')} className="px-2.5 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Insert</button>
                <button onClick={() => handleBSTAction('delete')} className="px-2.5 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Delete</button>
                <button onClick={() => handleBSTAction('search')} className="px-2.5 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Search</button>
                <button onClick={() => handleBSTAction('inorder')} className="px-2.5 py-1 bg-[#8FB5AD] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Inorder</button>
                <button onClick={() => handleBSTAction('levelorder')} className="px-2.5 py-1 bg-[#F7EFE1] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Level Order</button>
              </div>
            )}

            {/* Binary Heap Operations */}
            {algorithm.id === 'binary-heaps' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Val"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleHeapAction('insert')} className="px-2.5 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Insert</button>
                <button onClick={() => handleHeapAction('extract')} className="px-2.5 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Extract Root</button>
                <button onClick={() => handleHeapAction('peek')} className="px-2.5 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Peek</button>
                <button
                  onClick={() => {
                    setHeapDS(createInitialHeap(heapDS.type === 'max' ? 'min' : 'max'));
                    setActionCounter((c) => c + 1);
                  }}
                  className="px-2.5 py-1 bg-[#2B2620] text-white rounded-lg text-xs font-bold cursor-pointer"
                >
                  Type: {heapDS.type === 'max' ? 'Max Heap' : 'Min Heap'}
                </button>
              </div>
            )}

            {/* AVL Tree Operations */}
            {algorithm.id === 'avl-trees' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="number"
                  placeholder="Val"
                  value={dsInputValue}
                  onChange={(e) => setDsInputValue(e.target.value)}
                  className="w-16 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleAVLAction('insert')} className="px-2.5 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Insert</button>
                <button onClick={() => handleAVLAction('search')} className="px-2.5 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Search</button>
              </div>
            )}

            {/* Trie Operations */}
            {algorithm.id === 'tries' && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  type="text"
                  placeholder="Word/Prefix"
                  value={hashKey}
                  onChange={(e) => setHashKey(e.target.value)}
                  className="w-28 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-1 text-xs font-mono font-bold"
                />
                <button onClick={() => handleTrieAction('insert')} className="px-2.5 py-1 bg-[#9CCBA2] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Insert Word</button>
                <button onClick={() => handleTrieAction('searchWord')} className="px-2.5 py-1 bg-[#DFA43C] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Search Word</button>
                <button onClick={() => handleTrieAction('autocomplete')} className="px-2.5 py-1 bg-[#8FB5AD] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Autocomplete</button>
                <button onClick={() => handleTrieAction('delete')} className="px-2.5 py-1 bg-[#D97B54] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer">Delete</button>
              </div>
            )}

            {/* Graph Presets */}
            {algorithm.category === 'graphs' && (
              <div className="flex flex-wrap items-center gap-2">
                <button
                  onClick={() => setGraphData(getSampleGraph())}
                  className="px-2.5 py-1 bg-[#F7EFE1] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer"
                >
                  Standard Graph
                </button>
                <button
                  onClick={() => setGraphData(getDisconnectedSampleGraph())}
                  className="px-2.5 py-1 bg-[#F7EFE1] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer"
                >
                  Disconnected Graph
                </button>
                <button
                  onClick={() => setGraphData(getNegativeWeightsSampleGraph())}
                  className="px-2.5 py-1 bg-[#F7EFE1] border border-[#2B2620] rounded-lg text-xs font-bold cursor-pointer"
                >
                  Negative Weights Graph
                </button>
              </div>
            )}

            {/* Recursion & DP Sliders / Controls */}
            {algorithm.id === 'factorial' && (
              <div className="flex items-center gap-2 text-xs font-bold">
                <span>n: {factorialN}</span>
                <input
                  type="range"
                  min="1"
                  max="6"
                  value={factorialN}
                  onChange={(e) => setFactorialN(Number(e.target.value))}
                  className="w-24 cursor-pointer"
                />
              </div>
            )}

            {algorithm.id === 'fibonacci' && (
              <div className="flex flex-wrap items-center gap-3 text-xs font-bold">
                <div className="flex items-center gap-2">
                  <span>N: {fibN}</span>
                  <input
                    type="range"
                    min="2"
                    max="8"
                    value={fibN}
                    onChange={(e) => setFibN(Number(e.target.value))}
                    className="w-20 cursor-pointer"
                  />
                </div>
                <div className="flex items-center gap-1">
                  <span>Mode:</span>
                  {(['tabulated', 'memoized', 'recursive'] as const).map((m) => (
                    <button
                      key={m}
                      onClick={() => setFibMode(m)}
                      className={`px-2 py-0.5 rounded-md border text-[11px] font-bold ${
                        fibMode === m ? 'bg-[#2B2620] text-white' : 'bg-white text-gray-700'
                      }`}
                    >
                      {m}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {algorithm.id === 'tower-of-hanoi' && (
              <div className="flex items-center gap-2 text-xs font-bold">
                <span>Disks: {hanoiDisks}</span>
                <input
                  type="range"
                  min="1"
                  max="4"
                  value={hanoiDisks}
                  onChange={(e) => setHanoiDisks(Number(e.target.value))}
                  className="w-24 cursor-pointer"
                />
              </div>
            )}

            {algorithm.id === 'n-queens' && (
              <div className="flex items-center gap-2 text-xs font-bold">
                <span>Board N: {nQueensSize}</span>
                <input
                  type="range"
                  min="4"
                  max="6"
                  value={nQueensSize}
                  onChange={(e) => setNQueensSize(Number(e.target.value))}
                  className="w-20 cursor-pointer"
                />
              </div>
            )}

            {algorithm.id === 'lcs' && (
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  value={lcsS1}
                  onChange={(e) => setLcsS1(e.target.value)}
                  className="w-20 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-0.5 text-xs font-mono font-bold"
                />
                <input
                  type="text"
                  value={lcsS2}
                  onChange={(e) => setLcsS2(e.target.value)}
                  className="w-20 bg-[#F7EFE1] border border-[#2B2620] rounded-lg px-2 py-0.5 text-xs font-mono font-bold"
                />
              </div>
            )}
          </div>

          {/* Visualization Canvas Box */}
          <div className="bg-white border border-[#E8DCC4] rounded-[28px] p-6 shadow-[0_12px_28px_-8px_rgba(43,38,32,0.25)] flex flex-col justify-between min-h-[380px]">
            {/* Top Canvas Header Bar: Operation Counter */}
            <div className="flex items-center justify-between pb-3 border-b-2 border-gray-100 mb-4">
              <span className="text-xs font-black uppercase text-gray-500 tracking-wider">
                Execution Canvas
              </span>
              <OperationCounter counters={currentStep?.counters} />
            </div>

            <ErrorBoundary fallbackTitle="Canvas Rendering Interrupted">
              {(() => {
                const vizState: any = currentStep?.state;
                if (!vizState) {
                  return (
                    <div className="flex-1 flex flex-col items-center justify-center min-h-[220px] text-gray-400 font-bold text-sm">
                      <span>Loading visualization steps...</span>
                    </div>
                  );
                }

                return (
                  <div className="flex-1 flex items-center justify-center w-full">
                    {/* Sorting & Searching */}
                    {(algorithm.category === 'sorting' || algorithm.category === 'searching') && (
                      <ArrayVisualizer
                        elements={vizState.array || []}
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
                    )}

                    {/* Array DS */}
                    {algorithm.id === 'arrays' && (
                      <div className="flex items-center gap-3 overflow-x-auto p-4">
                        {(vizState.cells || []).map((cell: any) => (
                          <div
                            key={cell.index}
                            className={`w-14 h-16 rounded-2xl border border-[#E8DCC4] flex flex-col items-center justify-between py-2 shadow-[0_3px_10px_rgba(43,38,32,0.16)] ${
                              cell.state === 'active' || cell.state === 'inserted'
                                ? 'bg-[#9CCBA2]'
                                : cell.state === 'deleted'
                                ? 'bg-[#D97B54] line-through'
                                : 'bg-white'
                            }`}
                          >
                            <span className="font-mono font-black text-sm">{cell.value}</span>
                            <span className="text-[10px] text-gray-500 font-bold">[{cell.index}]</span>
                          </div>
                        ))}
                      </div>
                    )}

                    {/* Linked Lists */}
                    {algorithm.id === 'linked-lists' && (
                      <LinkedListVisualizer
                        nodes={vizState.nodes || []}
                        isDoubly={vizState.isDoubly}
                        activeNodeId={vizState.activeNodeId}
                      />
                    )}

                    {/* Stack */}
                    {algorithm.id === 'stacks' && (
                      <StackVisualizer
                        items={vizState.items || []}
                        maxCapacity={vizState.maxCapacity ?? 8}
                      />
                    )}

                    {/* Queue */}
                    {algorithm.id === 'queues' && (
                      <QueueVisualizer
                        slots={vizState.slots || []}
                        front={vizState.front ?? 0}
                        rear={vizState.rear ?? 0}
                        size={vizState.size ?? 0}
                        capacity={vizState.capacity ?? 6}
                        isCircular={vizState.isCircular}
                      />
                    )}

                    {/* Hash Table */}
                    {algorithm.id === 'hash-tables' && (
                      <HashTableVisualizer
                        buckets={vizState.buckets || []}
                        collisionStrategy={vizState.collisionStrategy || 'chaining'}
                        activeHash={vizState.activeHash}
                      />
                    )}

                    {/* BST */}
                    {algorithm.id === 'binary-search-tree' && (
                      <TreeVisualizer
                        root={vizState.root || null}
                        traversalOutput={vizState.traversalOutput}
                      />
                    )}

                    {/* Binary Heap */}
                    {algorithm.id === 'binary-heaps' && (
                      <HeapDualVisualizer
                        array={vizState.array || []}
                        type={vizState.type || 'max'}
                        activeIndices={vizState.activeIndices}
                        comparingIndices={vizState.comparingIndices}
                      />
                    )}

                    {/* AVL Tree */}
                    {algorithm.id === 'avl-trees' && (
                      <TreeVisualizer
                        root={vizState.root || null}
                        showAVLStats={true}
                      />
                    )}

                    {/* Trie */}
                    {algorithm.id === 'tries' && (
                      <TrieVisualizer
                        root={vizState.root || null}
                        wordsList={vizState.wordsList}
                        searchResults={vizState.searchResults}
                      />
                    )}

                    {/* Graph Algorithms */}
                    {algorithm.category === 'graphs' && (
                      <GraphVisualizer
                        graph={vizState.graph}
                        distances={vizState.distances}
                      />
                    )}

                    {/* Recursion (Factorial, Fibonacci, Hanoi) */}
                    {algorithm.id === 'factorial' && (
                      <RecursionVisualizer callStack={vizState.callStack || []} />
                    )}
                    {algorithm.id === 'fibonacci' && (
                      <RecursionVisualizer
                        memoTable={vizState.memoTable}
                        dpArray={vizState.dpArray}
                      />
                    )}
                    {algorithm.id === 'tower-of-hanoi' && (
                      <RecursionVisualizer hanoiPegs={vizState.pegs} />
                    )}

                    {/* N-Queens */}
                    {algorithm.id === 'n-queens' && (
                      <ChessboardVisualizer
                        n={vizState.n ?? 4}
                        board={vizState.board || []}
                        currentRow={vizState.currentRow}
                        currentCol={vizState.currentCol}
                        isConflict={vizState.isConflict}
                        solutionsFound={vizState.solutionsFound}
                      />
                    )}

                    {/* 0/1 Knapsack */}
                    {algorithm.id === 'knapsack' && (
                      <DPTableVisualizer
                        table={vizState.table || []}
                        rowLabels={['0', 'Book(1)', 'Camera(2)', 'Laptop(3)', 'Tablet(4)']}
                        colLabels={Array.from({ length: (vizState.capacity ?? 4) + 1 }, (_, i) => String(i))}
                        currentRow={vizState.currentRow}
                        currentCol={vizState.currentCol}
                        activeDependencies={vizState.activeDependencies}
                        optimalValue={vizState.optimalValue}
                        selectedItems={vizState.selectedItems}
                      />
                    )}

                    {/* LCS */}
                    {algorithm.id === 'lcs' && (
                      <DPTableVisualizer
                        table={vizState.table || []}
                        rowLabels={['Ø', ...(vizState.s1 ? vizState.s1.split('') : [])]}
                        colLabels={['Ø', ...(vizState.s2 ? vizState.s2.split('') : [])]}
                        currentRow={vizState.currentRow}
                        currentCol={vizState.currentCol}
                        activeDependencies={vizState.activeDependencies}
                        reconstructedResult={vizState.lcsResult}
                      />
                    )}
                  </div>
                );
              })()}
            </ErrorBoundary>
          </div>

          {/* Playback Controls & Timeline */}
          <div className="space-y-3">
            <ExecutionTimeline
              currentStepIndex={playback.currentStepIndex}
              totalSteps={playback.totalSteps}
              description={currentStep?.description || 'Execution State'}
              onScrub={playback.goToStep}
            />

            <PlaybackControls
              isPlaying={playback.isPlaying}
              isCompleted={playback.isCompleted}
              currentStepIndex={playback.currentStepIndex}
              totalSteps={playback.totalSteps}
              speed={playback.speed}
              onPlay={playback.play}
              onPause={playback.pause}
              onStepForward={playback.stepForward}
              onStepBackward={playback.stepBackward}
              onReset={playback.reset}
              onReplay={playback.replay}
              onSpeedChange={playback.setSpeed}
            />
          </div>

          {/* Lower Split: Code Highlighting & Complexity Panel */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-stretch">
            <div className="h-[280px]">
              <CodePanel
                code={algorithm.code}
                activeLine={currentStep?.codeLine || 1}
              />
            </div>

            <div className="flex flex-col justify-between">
              <ComplexityPanel complexity={algorithm.complexity} />
            </div>
          </div>
        </div>
      )}

      {/* Deep Dive Learning View */}
      {activeTab === 'learn' && (
        <LearningPanel algorithm={algorithm} />
      )}

      {/* Practice Mode View */}
      {activeTab === 'practice' && (
        <PracticeMode
          questions={currentQuestions}
          userScore={userScore}
          onScoreUpdate={onScoreUpdate}
        />
      )}
    </div>
  );
};
