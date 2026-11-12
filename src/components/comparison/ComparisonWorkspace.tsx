import React, { useState, useEffect } from 'react';
import { Play, Pause, RotateCcw, GitCompare } from 'lucide-react';
import { ALGORITHMS_CATALOG } from '../../data/algorithmsCatalog';
import { ExecutionStep } from '../../types/execution';
import { getSortingFallbackSteps } from '../../engine/fallbackEngine';
import { ArrayVisualizer } from '../visualizers/ArrayVisualizer';
import { usePlayback } from '../../engine/playback';
import { runAlgorithmOnJavaBackend } from '../../api/algoService';

export const ComparisonWorkspace: React.FC = () => {
  const [algoIdA, setAlgoIdA] = useState('bubble-sort');
  const [algoIdB, setAlgoIdB] = useState('quick-sort');
  const [inputStr, setInputStr] = useState('45, 12, 85, 32, 89, 39, 69, 44, 42, 1, 10');
  const [activeArray, setActiveArray] = useState<number[]>([45, 12, 85, 32, 89, 39, 69, 44, 42, 1, 10]);
  const [isJavaBackend, setIsJavaBackend] = useState<boolean>(true);

  const algoA = ALGORITHMS_CATALOG.find((a) => a.id === algoIdA) || ALGORITHMS_CATALOG[0];
  const algoB = ALGORITHMS_CATALOG.find((a) => a.id === algoIdB) || ALGORITHMS_CATALOG[4];

  const [stepsA, setStepsA] = useState<ExecutionStep[]>(() => getSortingFallbackSteps(algoIdA, activeArray));
  const [stepsB, setStepsB] = useState<ExecutionStep[]>(() => getSortingFallbackSteps(algoIdB, activeArray));

  useEffect(() => {
    let active = true;
    async function loadTraces() {
      const [resA, resB] = await Promise.all([
        runAlgorithmOnJavaBackend({ algorithmId: algoIdA, array: activeArray }),
        runAlgorithmOnJavaBackend({ algorithmId: algoIdB, array: activeArray }),
      ]);
      if (active) {
        if (resA && resA.length > 0) setStepsA(resA);
        else setStepsA(getSortingFallbackSteps(algoIdA, activeArray));

        if (resB && resB.length > 0) setStepsB(resB);
        else setStepsB(getSortingFallbackSteps(algoIdB, activeArray));

        setIsJavaBackend(!!(resA && resB));
      }
    }
    loadTraces();
    return () => {
      active = false;
    };
  }, [algoIdA, algoIdB, activeArray]);

  const playbackA = usePlayback({ steps: stepsA, initialSpeed: 2 });
  const playbackB = usePlayback({ steps: stepsB, initialSpeed: 2 });

  const handleApplyInput = () => {
    const parsed = inputStr
      .split(',')
      .map((s) => Number(s.trim()))
      .filter((n) => !isNaN(n));
    if (parsed.length > 0) {
      setActiveArray(parsed);
    }
  };

  const handleGenerateRandom = () => {
    const randomArr = Array.from({ length: 10 }, () => Math.floor(Math.random() * 90 + 10));
    setInputStr(randomArr.join(', '));
    setActiveArray(randomArr);
  };

  const isBothPlaying = playbackA.isPlaying || playbackB.isPlaying;

  const handlePlayBoth = () => {
    playbackA.play();
    playbackB.play();
  };

  const handlePauseBoth = () => {
    playbackA.pause();
    playbackB.pause();
  };

  const handleResetBoth = () => {
    playbackA.reset();
    playbackB.reset();
  };

  const stateA = playbackA.currentStep?.state as any;
  const stateB = playbackB.currentStep?.state as any;

  return (
    <div className="space-y-6 w-full">
      {/* Top Header & Inputs */}
      <div className="bg-white border border-[#E8DCC4] rounded-[24px] p-5 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <GitCompare className="w-5 h-5 text-[#2B2620]" />
            <h2 className="text-xl font-black text-[#2B2620]">
              Algorithm Comparison Arena
            </h2>
            {isJavaBackend && (
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-bold bg-[#9CCBA2] text-[#2B2620] border-[1.5px] border-[#2B2620] shadow-[0_2px_6px_rgba(43,38,32,0.18)]">
                <span className="w-2 h-2 rounded-full bg-[#107C41] animate-pulse"></span>
                Java Backend Active
              </span>
            )}
          </div>

          {/* Master Controls */}
          <div className="flex items-center gap-2">
            {isBothPlaying ? (
              <button
                onClick={handlePauseBoth}
                className="px-5 py-2 rounded-full bg-[#DFA43C] border border-[#E8DCC4] font-black text-xs shadow-[0_3px_10px_rgba(43,38,32,0.16)] flex items-center gap-1.5 cursor-pointer"
              >
                <Pause className="w-3.5 h-3.5 fill-current" />
                <span>Pause Both</span>
              </button>
            ) : (
              <button
                onClick={handlePlayBoth}
                className="px-5 py-2 rounded-full bg-[#9CCBA2] border border-[#E8DCC4] font-black text-xs shadow-[0_3px_10px_rgba(43,38,32,0.16)] flex items-center gap-1.5 cursor-pointer"
              >
                <Play className="w-3.5 h-3.5 fill-current" />
                <span>Run Side-by-Side</span>
              </button>
            )}

            <button
              onClick={handleResetBoth}
              className="p-2 rounded-full bg-[#F7EFE1] border border-[#E8DCC4] shadow-[0_3px_10px_rgba(43,38,32,0.16)] cursor-pointer"
              title="Reset both"
            >
              <RotateCcw className="w-4 h-4 text-[#2B2620]" />
            </button>
          </div>
        </div>

        {/* Input Controls */}
        <div className="flex flex-wrap items-center gap-3 pt-2 border-t border-gray-100">
          <label className="text-xs font-black text-gray-600">Shared Input Array:</label>
          <input
            type="text"
            value={inputStr}
            onChange={(e) => setInputStr(e.target.value)}
            className="flex-1 min-w-[200px] bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl px-3 py-1.5 text-xs font-mono font-bold text-[#2B2620]"
          />
          <button
            onClick={handleApplyInput}
            className="px-4 py-1.5 bg-[#2B2620] text-white text-xs font-bold rounded-xl border border-[#E8DCC4] shadow-[0_3px_10px_rgba(43,38,32,0.16)] cursor-pointer"
          >
            Apply
          </button>
          <button
            onClick={handleGenerateRandom}
            className="px-4 py-1.5 bg-[#8FB5AD] text-[#2B2620] text-xs font-bold rounded-xl border border-[#E8DCC4] shadow-[0_3px_10px_rgba(43,38,32,0.16)] cursor-pointer"
          >
            Randomize
          </button>
        </div>
      </div>

      {/* Side-by-Side Arena */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Slot A */}
        <div className="bg-white border border-[#E8DCC4] rounded-[24px] p-5 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-4">
          <div className="flex items-center justify-between pb-3 border-b-2 border-gray-100">
            <div className="flex items-center gap-2">
              <span className="w-6 h-6 rounded-full bg-[#DFA43C] border border-[#2B2620] flex items-center justify-center font-black text-xs">
                A
              </span>
              <select
                value={algoIdA}
                onChange={(e) => setAlgoIdA(e.target.value)}
                className="font-black text-sm bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl px-3 py-1 text-[#2B2620] focus:outline-none cursor-pointer"
              >
                <option value="bubble-sort">Bubble Sort</option>
                <option value="selection-sort">Selection Sort</option>
                <option value="insertion-sort">Insertion Sort</option>
                <option value="merge-sort">Merge Sort</option>
                <option value="quick-sort">Quick Sort</option>
                <option value="heap-sort">Heap Sort</option>
              </select>
            </div>

            <span className="text-xs font-mono font-black bg-[#F7EFE1] border border-[#2B2620] px-2.5 py-1 rounded-full">
              Worst: {algoA.complexity.timeWorst}
            </span>
          </div>

          {/* Visualizer canvas */}
          <div className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-2xl p-2 min-h-[240px] flex items-center justify-center">
            {stateA && (
              <ArrayVisualizer
                elements={stateA.array}
                comparingIndices={stateA.comparingIndices}
                swappingIndices={stateA.swappingIndices}
                pivotIndex={stateA.pivotIndex}
                sortedIndices={stateA.sortedIndices}
              />
            )}
          </div>

          {/* Metrics A */}
          <div className="grid grid-cols-3 gap-2">
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-2 text-center">
              <span className="text-[10px] font-bold text-gray-500 block">Comparisons</span>
              <span className="text-sm font-black font-mono text-[#2B2620]">
                {stateA?.comparisons ?? 0}
              </span>
            </div>
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-2 text-center">
              <span className="text-[10px] font-bold text-gray-500 block">Swaps</span>
              <span className="text-sm font-black font-mono text-[#2B2620]">
                {stateA?.swaps ?? 0}
              </span>
            </div>
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-2 text-center">
              <span className="text-[10px] font-bold text-gray-500 block">Step</span>
              <span className="text-sm font-black font-mono text-[#2B2620]">
                {playbackA.currentStepIndex + 1}/{playbackA.totalSteps}
              </span>
            </div>
          </div>
        </div>

        {/* Slot B */}
        <div className="bg-white border border-[#E8DCC4] rounded-[24px] p-5 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-4">
          <div className="flex items-center justify-between pb-3 border-b-2 border-gray-100">
            <div className="flex items-center gap-2">
              <span className="w-6 h-6 rounded-full bg-[#9CCBA2] border border-[#2B2620] flex items-center justify-center font-black text-xs">
                B
              </span>
              <select
                value={algoIdB}
                onChange={(e) => setAlgoIdB(e.target.value)}
                className="font-black text-sm bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl px-3 py-1 text-[#2B2620] focus:outline-none cursor-pointer"
              >
                <option value="bubble-sort">Bubble Sort</option>
                <option value="selection-sort">Selection Sort</option>
                <option value="insertion-sort">Insertion Sort</option>
                <option value="merge-sort">Merge Sort</option>
                <option value="quick-sort">Quick Sort</option>
                <option value="heap-sort">Heap Sort</option>
              </select>
            </div>

            <span className="text-xs font-mono font-black bg-[#F7EFE1] border border-[#2B2620] px-2.5 py-1 rounded-full">
              Worst: {algoB.complexity.timeWorst}
            </span>
          </div>

          {/* Visualizer canvas */}
          <div className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-2xl p-2 min-h-[240px] flex items-center justify-center">
            {stateB && (
              <ArrayVisualizer
                elements={stateB.array}
                comparingIndices={stateB.comparingIndices}
                swappingIndices={stateB.swappingIndices}
                pivotIndex={stateB.pivotIndex}
                sortedIndices={stateB.sortedIndices}
              />
            )}
          </div>

          {/* Metrics B */}
          <div className="grid grid-cols-3 gap-2">
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-2 text-center">
              <span className="text-[10px] font-bold text-gray-500 block">Comparisons</span>
              <span className="text-sm font-black font-mono text-[#2B2620]">
                {stateB?.comparisons ?? 0}
              </span>
            </div>
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-2 text-center">
              <span className="text-[10px] font-bold text-gray-500 block">Swaps</span>
              <span className="text-sm font-black font-mono text-[#2B2620]">
                {stateB?.swaps ?? 0}
              </span>
            </div>
            <div className="bg-[#F7EFE1] border border-[#2B2620] rounded-xl p-2 text-center">
              <span className="text-[10px] font-bold text-gray-500 block">Step</span>
              <span className="text-sm font-black font-mono text-[#2B2620]">
                {playbackB.currentStepIndex + 1}/{playbackB.totalSteps}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
