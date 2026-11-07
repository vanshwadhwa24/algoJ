import React from 'react';
import { StackFrame } from '../../engine/recursion/recursion';

interface RecursionVisualizerProps {
  callStack?: StackFrame[];
  memoTable?: Record<number, number>;
  dpArray?: number[];
  hanoiPegs?: Record<'A' | 'B' | 'C', number[]>;
}

export const RecursionVisualizer: React.FC<RecursionVisualizerProps> = ({
  callStack,
  memoTable,
  dpArray,
  hanoiPegs,
}) => {
  // If Tower of Hanoi is active
  if (hanoiPegs) {
    return (
      <div className="flex flex-col items-center w-full p-4 space-y-6">
        <span className="text-xs font-black uppercase text-gray-500">
          Tower of Hanoi Disks & Pegs
        </span>

        <div className="grid grid-cols-3 gap-6 sm:gap-12 w-full max-w-xl">
          {(['A', 'B', 'C'] as const).map((peg) => {
            const disks = hanoiPegs[peg] || [];
            return (
              <div key={peg} className="flex flex-col items-center">
                {/* Peg Post */}
                <div className="relative w-full flex flex-col-reverse items-center h-44 border-b-[4px] border-[#1A1A1A]">
                  {/* Vertical Rod */}
                  <div className="absolute bottom-0 w-2 h-40 bg-[#1A1A1A] rounded-t-full -z-10" />

                  {/* Disks stacked bottom-to-top */}
                  {disks.map((d) => {
                    const widthPx = 30 + d * 22;
                    const colors = ['#C9B8F5', '#F5D876', '#8FD6B0', '#F5A868', '#FFFFFF'];
                    const color = colors[(d - 1) % colors.length];

                    return (
                      <div
                        key={d}
                        style={{ width: `${widthPx}px`, backgroundColor: color }}
                        className="h-7 rounded-xl border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#1A1A1A] flex items-center justify-center font-mono font-black text-xs select-none transition-all duration-200 mb-1 z-10"
                      >
                        {d}
                      </div>
                    );
                  })}
                </div>

                <div className="mt-2 font-mono font-black text-sm bg-[#FAF6EC] border-[2px] border-[#1A1A1A] px-3 py-1 rounded-full shadow-[2px_2px_0px_#1A1A1A]">
                  Peg {peg}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  // Call Stack and Memo/DP representation
  return (
    <div className="flex flex-col sm:flex-row gap-6 items-start justify-center w-full p-4">
      {/* Call Stack Panel */}
      {callStack && (
        <div className="flex-1 w-full max-w-md bg-white border-[2.5px] border-[#1A1A1A] rounded-2xl p-4 shadow-[4px_4px_0px_#1A1A1A]">
          <h4 className="font-black text-xs uppercase text-gray-500 mb-3">
            Execution Call Stack (Top = Active Frame)
          </h4>

          <div className="flex flex-col-reverse gap-2 min-h-[160px] max-h-[260px] overflow-y-auto p-2 border-[2px] border-dashed border-[#1A1A1A] rounded-xl bg-[#FAF6EC]">
            {callStack.length === 0 ? (
              <span className="m-auto text-xs font-bold text-gray-400">
                Call stack is empty
              </span>
            ) : (
              callStack.map((frame, idx) => {
                const isTop = idx === callStack.length - 1;
                return (
                  <div
                    key={frame.id}
                    className={`p-2.5 rounded-xl border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#1A1A1A] flex items-center justify-between text-xs font-mono font-black ${
                      frame.state === 'base-case'
                        ? 'bg-[#8FD6B0]'
                        : isTop
                        ? 'bg-[#F5D876]'
                        : 'bg-white'
                    }`}
                  >
                    <span>{frame.name}</span>
                    <span className="text-[11px] font-bold text-gray-600">
                      {frame.returnValue !== undefined ? `ret: ${frame.returnValue}` : 'waiting...'}
                    </span>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}

      {/* Memo Table or DP Array */}
      {memoTable && (
        <div className="flex-1 w-full max-w-md bg-white border-[2.5px] border-[#1A1A1A] rounded-2xl p-4 shadow-[4px_4px_0px_#1A1A1A]">
          <h4 className="font-black text-xs uppercase text-gray-500 mb-3">
            Memoization Cache Table
          </h4>
          <div className="grid grid-cols-3 sm:grid-cols-4 gap-2">
            {Object.entries(memoTable).map(([k, v]) => (
              <div
                key={k}
                className="bg-[#C9B8F5] border-[2px] border-[#1A1A1A] rounded-xl p-2 text-center font-mono shadow-[2px_2px_0px_#1A1A1A]"
              >
                <span className="text-[10px] text-gray-600 block">fib({k})</span>
                <span className="font-black text-sm text-[#1A1A1A]">{v}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {dpArray && (
        <div className="flex-1 w-full max-w-md bg-white border-[2.5px] border-[#1A1A1A] rounded-2xl p-4 shadow-[4px_4px_0px_#1A1A1A]">
          <h4 className="font-black text-xs uppercase text-gray-500 mb-3">
            Tabulation 1D Array dp[0..n]
          </h4>
          <div className="flex flex-wrap gap-2">
            {dpArray.map((v, i) => (
              <div
                key={i}
                className="bg-[#8FD6B0] border-[2px] border-[#1A1A1A] rounded-xl px-3 py-1.5 text-center font-mono shadow-[2px_2px_0px_#1A1A1A]"
              >
                <span className="text-[10px] text-gray-600 block">[{i}]</span>
                <span className="font-black text-sm text-[#1A1A1A]">{v}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
