import React from 'react';
import { SortElement } from '../../engine/sorting/types';
import { SearchElement } from '../../engine/searching/types';

interface ArrayVisualizerProps {
  elements: (SortElement | SearchElement)[];
  comparingIndices?: number[];
  swappingIndices?: number[];
  pivotIndex?: number | null;
  sortedIndices?: number[];
  lowIndex?: number | null;
  highIndex?: number | null;
  midIndex?: number | null;
  currentIndex?: number | null;
  foundIndex?: number | null;
}

export const ArrayVisualizer: React.FC<ArrayVisualizerProps> = ({
  elements,
  comparingIndices = [],
  swappingIndices = [],
  pivotIndex = null,
  sortedIndices = [],
  lowIndex = null,
  highIndex = null,
  midIndex = null,
  currentIndex = null,
  foundIndex = null,
}) => {
  if (!elements || elements.length === 0) {
    return (
      <div className="flex items-center justify-center h-64 text-gray-500 font-bold">
        Empty Array
      </div>
    );
  }

  // Find min and max value for height scaling
  const values = elements.map((e) => e.value);
  const minVal = Math.min(...values, 0);
  const maxVal = Math.max(...values, 100);
  const range = maxVal - minVal || 1;

  return (
    <div className="flex flex-col items-center justify-end h-72 sm:h-80 w-full p-4 overflow-x-auto">
      {/* Bars container */}
      <div className="flex items-end justify-center gap-2 sm:gap-3 w-full h-56">
        {elements.map((el, idx) => {
          const val = el.value;
          // Normalized height between 24px and 190px
          const barHeight = Math.max(28, Math.round(((val - minVal) / range) * 160 + 28));

          // Determine color & status
          let bgColor = 'bg-[#FBF3E3]';
          let borderColor = 'border-[#E3D3B4]';
          let textColor = 'text-[#2B2620]';
          let labelBadge: string | null = null;

          if (idx === foundIndex || el.state === 'found') {
            bgColor = 'bg-[#9CCBA2]'; // Mint green
            labelBadge = 'FOUND';
          } else if (idx === pivotIndex || el.state === 'pivot') {
            bgColor = 'bg-[#8FB5AD]'; // Lavender
            labelBadge = 'PIVOT';
          } else if (idx === midIndex || el.state === 'mid') {
            bgColor = 'bg-[#D97B54]'; // Orange
            labelBadge = 'MID';
          } else if (swappingIndices.includes(idx) || el.state === 'swapping') {
            bgColor = 'bg-[#D97B54]'; // Orange
            labelBadge = 'SWAP';
          } else if (comparingIndices.includes(idx) || el.state === 'comparing' || idx === currentIndex) {
            bgColor = 'bg-[#DFA43C]'; // Yellow
            labelBadge = 'COMPARE';
          } else if (sortedIndices.includes(idx) || el.state === 'sorted') {
            bgColor = 'bg-[#9CCBA2]'; // Mint
          } else if (el.state === 'eliminated') {
            bgColor = 'bg-gray-200 opacity-40';
          }

          // Special pointer labels
          const pointers: string[] = [];
          if (idx === lowIndex) pointers.push('LOW');
          if (idx === highIndex) pointers.push('HIGH');

          return (
            <div key={el.id || idx} className="flex flex-col items-center flex-1 max-w-[56px] min-w-[32px]">
              {/* Optional role badge above bar; adjacent labels stagger into two lanes so they never collide */}
              <div className={`h-10 flex ${idx % 2 === 1 ? 'items-start' : 'items-end'} justify-center text-[10px] font-black tracking-wider uppercase mb-1`}>
                {labelBadge && (
                  <span className="bg-[#2B2620] text-white px-1.5 py-0.5 rounded text-[9px]">
                    {labelBadge}
                  </span>
                )}
              </div>

              {/* The Visual Bar */}
              <div
                style={{ height: `${barHeight}px` }}
                className={`w-full rounded-2xl border-2 ${borderColor} ${bgColor} ${textColor} shadow-[0_4px_12px_rgba(43,38,32,0.18)] flex flex-col justify-between items-center py-2 transition-all duration-200 select-none`}
              >
                <span className="font-mono font-black text-xs sm:text-sm">
                  {val}
                </span>
                <span className="text-[10px] font-bold opacity-60">
                  #{idx}
                </span>
              </div>

              {/* Pointer tags below index */}
              <div className="h-5 mt-1 flex flex-wrap gap-0.5 justify-center">
                {pointers.map((p) => (
                  <span
                    key={p}
                    className="text-[9px] font-black px-1 rounded bg-[#DFA43C] border border-[#2B2620]"
                  >
                    {p}
                  </span>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
