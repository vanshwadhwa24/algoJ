import React from 'react';
import { Activity } from 'lucide-react';

interface OperationCounterProps {
  counters?: Record<string, number>;
}

export const OperationCounter: React.FC<OperationCounterProps> = ({ counters }) => {
  if (!counters || Object.keys(counters).length === 0) return null;

  return (
    <div className="flex flex-wrap items-center gap-2">
      {Object.entries(counters).map(([key, val]) => {
        const formattedKey = key
          .replace(/([A-Z])/g, ' $1')
          .replace(/^./, (str) => str.toUpperCase());

        return (
          <div
            key={key}
            className="flex items-center gap-2 bg-[#FAF6EC] border-[2px] border-[#1A1A1A] rounded-full px-3 py-1 text-xs font-bold shadow-[2px_2px_0px_#1A1A1A]"
          >
            <Activity className="w-3.5 h-3.5 text-[#1A1A1A]" />
            <span className="text-gray-600">{formattedKey}:</span>
            <span className="font-mono font-black text-[#1A1A1A]">{val}</span>
          </div>
        );
      })}
    </div>
  );
};
