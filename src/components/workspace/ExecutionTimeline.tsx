import React from 'react';

interface ExecutionTimelineProps {
  currentStepIndex: number;
  totalSteps: number;
  description: string;
  onScrub: (index: number) => void;
}

export const ExecutionTimeline: React.FC<ExecutionTimelineProps> = ({
  currentStepIndex,
  totalSteps,
  description,
  onScrub,
}) => {
  const progressPercent =
    totalSteps > 1 ? (currentStepIndex / (totalSteps - 1)) * 100 : 0;

  return (
    <div className="bg-white border-[2.5px] border-[#1A1A1A] rounded-[24px] p-4 shadow-[4px_4px_0px_#1A1A1A] space-y-3">
      <div className="flex items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <span className="bg-[#FAF6EC] border-[2px] border-[#1A1A1A] px-3 py-1 rounded-full text-xs font-black text-[#1A1A1A]">
            Step {totalSteps > 0 ? currentStepIndex + 1 : 0} of {totalSteps}
          </span>
          <span className="text-xs font-bold text-gray-600">
            {Math.round(progressPercent)}%
          </span>
        </div>

        {/* Operation description */}
        <p className="text-sm font-semibold text-[#1A1A1A] truncate max-w-[60%] sm:max-w-[70%]">
          {description}
        </p>
      </div>

      {/* Scrubbable Range Input */}
      <div className="relative flex items-center">
        <input
          type="range"
          min={0}
          max={Math.max(0, totalSteps - 1)}
          value={currentStepIndex}
          onChange={(e) => onScrub(Number(e.target.value))}
          disabled={totalSteps <= 1}
          className="w-full h-3 bg-[#FAF6EC] rounded-full appearance-none cursor-pointer border-[2px] border-[#1A1A1A] accent-[#1A1A1A] focus:outline-none"
        />
      </div>
    </div>
  );
};
