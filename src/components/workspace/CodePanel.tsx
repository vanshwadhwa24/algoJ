import React from 'react';
import { Code2 } from 'lucide-react';
import { CodeDefinition } from '../../types/execution';

interface CodePanelProps {
  code: CodeDefinition;
  activeLine: number;
}

export const CodePanel: React.FC<CodePanelProps> = ({ code, activeLine }) => {
  return (
    <div className="bg-[#2B2620] text-white border border-[#E8DCC4] rounded-[24px] p-4 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] overflow-hidden flex flex-col h-full">
      <div className="flex items-center justify-between pb-3 border-b border-gray-700 mb-3">
        <div className="flex items-center gap-2">
          <Code2 className="w-4 h-4 text-[#9CCBA2]" />
          <span className="font-bold text-sm tracking-wide text-gray-200">
            Pseudocode / Implementation
          </span>
        </div>
        <span className="text-[11px] font-mono uppercase bg-[#2A2A2A] px-2 py-0.5 rounded-full text-gray-400">
          {code.language}
        </span>
      </div>

      <div className="overflow-y-auto font-mono text-xs sm:text-[13px] leading-relaxed flex-1 space-y-0.5 pr-1">
        {code.lines.map((line, idx) => {
          const lineNum = idx + 1;
          const isActive = lineNum === activeLine;

          return (
            <div
              key={idx}
              className={`flex items-start gap-3 px-2 py-1 rounded-md transition-colors ${
                isActive
                  ? 'bg-[#9CCBA2] text-[#2B2620] font-bold border-[1.5px] border-[#2B2620]'
                  : 'text-gray-300 hover:bg-[#252525]'
              }`}
            >
              <span
                className={`select-none text-[11px] w-6 text-right shrink-0 ${
                  isActive ? 'text-[#2B2620]' : 'text-gray-500'
                }`}
              >
                {lineNum}
              </span>
              <pre className="font-mono whitespace-pre-wrap flex-1 overflow-x-auto">
                {line}
              </pre>
            </div>
          );
        })}
      </div>
    </div>
  );
};
