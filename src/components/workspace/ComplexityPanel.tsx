import React from 'react';
import { Clock, Info } from 'lucide-react';
import { ComplexityInfo } from '../../types/execution';

interface ComplexityPanelProps {
  complexity: ComplexityInfo;
}

export const ComplexityPanel: React.FC<ComplexityPanelProps> = ({ complexity }) => {
  return (
    <div className="bg-white border-[2.5px] border-[#1A1A1A] rounded-[24px] p-4 shadow-[4px_4px_0px_#1A1A1A] space-y-3">
      <div className="flex items-center gap-2 pb-2 border-b-2 border-gray-100">
        <Clock className="w-4 h-4 text-[#1A1A1A]" />
        <h3 className="font-extrabold text-sm uppercase tracking-wide text-[#1A1A1A]">
          Complexity Analysis
        </h3>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
        {/* Best Case */}
        <div className="bg-[#FAF6EC] border-[2px] border-[#1A1A1A] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-gray-600 block">
            Best Case
          </span>
          <span className="text-sm font-mono font-black text-[#1A1A1A] mt-0.5 block">
            {complexity.timeBest}
          </span>
        </div>

        {/* Average Case */}
        <div className="bg-[#FAF6EC] border-[2px] border-[#1A1A1A] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-gray-600 block">
            Average Case
          </span>
          <span className="text-sm font-mono font-black text-[#1A1A1A] mt-0.5 block">
            {complexity.timeAverage}
          </span>
        </div>

        {/* Worst Case */}
        <div className="bg-[#FAF6EC] border-[2px] border-[#1A1A1A] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-gray-600 block">
            Worst Case
          </span>
          <span className="text-sm font-mono font-black text-[#1A1A1A] mt-0.5 block">
            {complexity.timeWorst}
          </span>
        </div>

        {/* Space Complexity */}
        <div className="bg-[#C9B8F5] border-[2px] border-[#1A1A1A] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-[#1A1A1A] block">
            Space (Aux)
          </span>
          <span className="text-sm font-mono font-black text-[#1A1A1A] mt-0.5 block">
            {complexity.spaceAuxiliary}
          </span>
        </div>
      </div>

      {/* Explanation */}
      <div className="flex items-start gap-2 bg-[#FAF6EC] border-[1.5px] border-[#1A1A1A] p-2.5 rounded-xl">
        <Info className="w-4 h-4 text-[#1A1A1A] shrink-0 mt-0.5" />
        <p className="text-xs font-semibold text-[#1A1A1A] leading-relaxed">
          {complexity.explanation}
        </p>
      </div>
    </div>
  );
};
