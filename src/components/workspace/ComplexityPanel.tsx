import React from 'react';
import { Clock, Info } from 'lucide-react';
import { ComplexityInfo } from '../../types/execution';

interface ComplexityPanelProps {
  complexity: ComplexityInfo;
}

export const ComplexityPanel: React.FC<ComplexityPanelProps> = ({ complexity }) => {
  return (
    <div className="bg-white border border-[#E8DCC4] rounded-[24px] p-4 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-3">
      <div className="flex items-center gap-2 pb-2 border-b-2 border-gray-100">
        <Clock className="w-4 h-4 text-[#2B2620]" />
        <h3 className="font-extrabold text-sm uppercase tracking-wide text-[#2B2620]">
          Complexity Analysis
        </h3>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
        {/* Best Case */}
        <div className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-gray-600 block">
            Best Case
          </span>
          <span className="text-sm font-mono font-black text-[#2B2620] mt-0.5 block">
            {complexity.timeBest}
          </span>
        </div>

        {/* Average Case */}
        <div className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-gray-600 block">
            Average Case
          </span>
          <span className="text-sm font-mono font-black text-[#2B2620] mt-0.5 block">
            {complexity.timeAverage}
          </span>
        </div>

        {/* Worst Case */}
        <div className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-gray-600 block">
            Worst Case
          </span>
          <span className="text-sm font-mono font-black text-[#2B2620] mt-0.5 block">
            {complexity.timeWorst}
          </span>
        </div>

        {/* Space Complexity */}
        <div className="bg-[#8FB5AD] border border-[#E8DCC4] rounded-xl p-2.5 text-center">
          <span className="text-[10px] font-extrabold uppercase text-[#2B2620] block">
            Space (Aux)
          </span>
          <span className="text-sm font-mono font-black text-[#2B2620] mt-0.5 block">
            {complexity.spaceAuxiliary}
          </span>
        </div>
      </div>

      {/* Explanation */}
      <div className="flex items-start gap-2 bg-[#F7EFE1] border-[1.5px] border-[#2B2620] p-2.5 rounded-xl">
        <Info className="w-4 h-4 text-[#2B2620] shrink-0 mt-0.5" />
        <p className="text-xs font-semibold text-[#2B2620] leading-relaxed">
          {complexity.explanation}
        </p>
      </div>
    </div>
  );
};
