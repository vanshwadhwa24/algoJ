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
            className="flex items-center gap-2 bg-[#F7EFE1] border border-[#E8DCC4] rounded-full px-3 py-1 text-xs font-bold shadow-[0_3px_10px_rgba(43,38,32,0.16)]"
          >
            <Activity className="w-3.5 h-3.5 text-[#2B2620]" />
            <span className="text-gray-600">{formattedKey}:</span>
            <span className="font-mono font-black text-[#2B2620]">{val}</span>
          </div>
        );
      })}
    </div>
  );
};
