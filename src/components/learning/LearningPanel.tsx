import React from 'react';
import { Lightbulb, ListOrdered, AlertTriangle, ThumbsUp, ThumbsDown } from 'lucide-react';
import { AlgorithmMetadata } from '../../types/execution';

interface LearningPanelProps {
  algorithm: AlgorithmMetadata;
}

export const LearningPanel: React.FC<LearningPanelProps> = ({ algorithm }) => {
  const { learning } = algorithm;

  return (
    <div className="bg-white border border-[#E8DCC4] rounded-[24px] p-5 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-5">
      {/* Intuition */}
      <div className="space-y-1.5">
        <div className="flex items-center gap-2">
          <Lightbulb className="w-4 h-4 text-[#DFA43C] fill-[#DFA43C]" />
          <h4 className="font-extrabold text-sm uppercase text-[#2B2620]">Core Intuition</h4>
        </div>
        <p className="text-xs sm:text-sm font-semibold text-gray-700 leading-relaxed bg-[#F7EFE1] p-3 rounded-xl border border-[#2B2620]">
          {learning.intuition}
        </p>
      </div>

      {/* Step by Step Breakdown */}
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <ListOrdered className="w-4 h-4 text-[#2B2620]" />
          <h4 className="font-extrabold text-sm uppercase text-[#2B2620]">How It Works Step-by-Step</h4>
        </div>
        <ol className="space-y-1.5 text-xs sm:text-sm font-semibold text-gray-700">
          {learning.howItWorks.map((step, idx) => (
            <li key={idx} className="flex items-start gap-2">
              <span className="w-5 h-5 rounded-full bg-[#2B2620] text-white flex items-center justify-center text-[10px] font-black shrink-0 mt-0.5">
                {idx + 1}
              </span>
              <span>{step}</span>
            </li>
          ))}
        </ol>
      </div>

      {/* Edge Cases */}
      <div className="space-y-2">
        <div className="flex items-center gap-2">
          <AlertTriangle className="w-4 h-4 text-[#D97B54]" />
          <h4 className="font-extrabold text-sm uppercase text-[#2B2620]">Key Edge Cases</h4>
        </div>
        <div className="flex flex-wrap gap-1.5">
          {learning.edgeCases.map((ec, idx) => (
            <span
              key={idx}
              className="bg-[#F7EFE1] border border-[#2B2620] rounded-full px-3 py-1 text-xs font-bold text-[#2B2620]"
            >
              {ec}
            </span>
          ))}
        </div>
      </div>

      {/* Advantages and Limitations */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2 border-t border-gray-100">
        {/* Advantages */}
        <div className="bg-[#9CCBA2]/20 border border-[#9CCBA2] p-3 rounded-xl space-y-1.5">
          <div className="flex items-center gap-1.5 text-green-900 font-extrabold text-xs uppercase">
            <ThumbsUp className="w-3.5 h-3.5" />
            <span>Advantages</span>
          </div>
          <ul className="text-xs font-semibold text-gray-700 space-y-1 list-disc list-inside">
            {learning.advantages.map((adv, idx) => (
              <li key={idx}>{adv}</li>
            ))}
          </ul>
        </div>

        {/* Limitations */}
        <div className="bg-[#D97B54]/20 border border-[#D97B54] p-3 rounded-xl space-y-1.5">
          <div className="flex items-center gap-1.5 text-orange-950 font-extrabold text-xs uppercase">
            <ThumbsDown className="w-3.5 h-3.5" />
            <span>Limitations</span>
          </div>
          <ul className="text-xs font-semibold text-gray-700 space-y-1 list-disc list-inside">
            {learning.limitations.map((lim, idx) => (
              <li key={idx}>{lim}</li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};
