import React from 'react';
import { ArrowLeft } from 'lucide-react';
import { StackItem } from '../../engine/structures/stack';

interface StackVisualizerProps {
  items: StackItem[];
  maxCapacity: number;
}

export const StackVisualizer: React.FC<StackVisualizerProps> = ({
  items = [],
  maxCapacity,
}) => {
  const safeItems = items || [];
  return (
    <div className="flex flex-col items-center justify-center p-6 w-full min-h-[300px]">
      <div className="flex items-end gap-6">
        {/* Stack Container with 3-sided border (open top) */}
        <div className="w-48 sm:w-56 border-b-[4px] border-l-[4px] border-r-[4px] border-[#2B2620] rounded-b-2xl p-2.5 flex flex-col-reverse gap-2 bg-[#F7EFE1] shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] min-h-[220px]">
          {safeItems.map((item, idx) => {
            const isTop = idx === safeItems.length - 1;
            let bgColor = 'bg-white';
            if (item.highlight === 'pushing') bgColor = 'bg-[#9CCBA2]'; // Mint
            else if (item.highlight === 'popping') bgColor = 'bg-[#D97B54]'; // Orange
            else if (isTop) bgColor = 'bg-[#DFA43C]'; // Yellow

            return (
              <div
                key={item.id}
                className={`w-full py-2.5 px-4 rounded-xl border border-[#E8DCC4] ${bgColor} shadow-[0_3px_10px_rgba(43,38,32,0.16)] flex items-center justify-between font-mono font-black text-sm select-none transition-all`}
              >
                <span>{item.value}</span>
                <span className="text-[10px] text-gray-500 font-bold">
                  [index {idx}]
                </span>
              </div>
            );
          })}

          {safeItems.length === 0 && (
            <div className="flex-1 flex items-center justify-center text-xs font-bold text-gray-400 py-12">
              Stack is Empty
            </div>
          )}
        </div>

        {/* Pointer indicator for TOP */}
        <div className="flex flex-col justify-end pb-4 space-y-4">
          <div className="flex items-center gap-2">
            <ArrowLeft className="w-5 h-5 text-[#2B2620] stroke-[3]" />
            <div className="bg-[#2B2620] text-white px-3 py-1 rounded-full text-xs font-black shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
              TOP: {items.length > 0 ? items[items.length - 1].value : 'null'}
            </div>
          </div>

          <div className="bg-white border border-[#E8DCC4] rounded-xl p-2 text-xs font-bold shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
            <div>Size: {items.length} / {maxCapacity}</div>
            <div className="text-[10px] text-gray-500">LIFO Policy</div>
          </div>
        </div>
      </div>
    </div>
  );
};
