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
        <div className="w-48 sm:w-56 border-b-[4px] border-l-[4px] border-r-[4px] border-[#1A1A1A] rounded-b-2xl p-2.5 flex flex-col-reverse gap-2 bg-[#FAF6EC] shadow-[4px_4px_0px_#1A1A1A] min-h-[220px]">
          {safeItems.map((item, idx) => {
            const isTop = idx === safeItems.length - 1;
            let bgColor = 'bg-white';
            if (item.highlight === 'pushing') bgColor = 'bg-[#8FD6B0]'; // Mint
            else if (item.highlight === 'popping') bgColor = 'bg-[#F5A868]'; // Orange
            else if (isTop) bgColor = 'bg-[#F5D876]'; // Yellow

            return (
              <div
                key={item.id}
                className={`w-full py-2.5 px-4 rounded-xl border-[2px] border-[#1A1A1A] ${bgColor} shadow-[2px_2px_0px_#1A1A1A] flex items-center justify-between font-mono font-black text-sm select-none transition-all`}
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
            <ArrowLeft className="w-5 h-5 text-[#1A1A1A] stroke-[3]" />
            <div className="bg-[#1A1A1A] text-white px-3 py-1 rounded-full text-xs font-black shadow-[2px_2px_0px_#8FD6B0]">
              TOP: {items.length > 0 ? items[items.length - 1].value : 'null'}
            </div>
          </div>

          <div className="bg-white border-[2px] border-[#1A1A1A] rounded-xl p-2 text-xs font-bold shadow-[2px_2px_0px_#1A1A1A]">
            <div>Size: {items.length} / {maxCapacity}</div>
            <div className="text-[10px] text-gray-500">LIFO Policy</div>
          </div>
        </div>
      </div>
    </div>
  );
};
