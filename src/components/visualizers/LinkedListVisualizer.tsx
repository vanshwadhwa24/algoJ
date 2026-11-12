import React from 'react';
import { ArrowRight, ArrowLeftRight } from 'lucide-react';
import { ListNode } from '../../engine/structures/linkedList';

interface LinkedListVisualizerProps {
  nodes: ListNode[];
  isDoubly?: boolean;
  activeNodeId?: string | null;
}

export const LinkedListVisualizer: React.FC<LinkedListVisualizerProps> = ({
  nodes = [],
  isDoubly = false,
  activeNodeId = null,
}) => {
  const safeNodes = nodes || [];
  return (
    <div className="flex items-center gap-2 p-6 overflow-x-auto min-h-[220px] w-full justify-start sm:justify-center">
      {safeNodes.length === 0 ? (
        <div className="flex items-center gap-3">
          <span className="font-mono text-sm font-bold bg-[#F7EFE1] px-3 py-1.5 rounded-full border border-[#E8DCC4]">
            HEAD → NULL
          </span>
          <span className="text-gray-500 font-bold text-sm">List is empty</span>
        </div>
      ) : (
        <>
          {safeNodes.map((node, idx) => {
            const isHead = idx === 0;
            const isTail = idx === safeNodes.length - 1;
            const isActive = node.id === activeNodeId || node.highlight === 'active';
            const isNew = node.highlight === 'inserted' || node.highlight === 'new';
            const isDeleted = node.highlight === 'deleted';

            let bgColor = 'bg-white';
            if (isNew) bgColor = 'bg-[#9CCBA2]'; // Mint
            else if (isActive) bgColor = 'bg-[#DFA43C]'; // Yellow
            else if (isDeleted) bgColor = 'bg-[#D97B54] line-through opacity-70'; // Orange

            return (
              <div key={node.id} className="flex items-center gap-2 shrink-0">
                {/* Node Box */}
                <div className="flex flex-col items-center">
                  {/* Head / Tail Labels */}
                  <div className="h-6 flex items-center gap-1 mb-1">
                    {isHead && (
                      <span className="bg-[#2B2620] text-white text-[10px] font-black px-2 py-0.5 rounded-full border border-[#2B2620]">
                        HEAD
                      </span>
                    )}
                    {isTail && (
                      <span className="bg-[#8FB5AD] text-[#2B2620] text-[10px] font-black px-2 py-0.5 rounded-full border border-[#2B2620]">
                        TAIL
                      </span>
                    )}
                  </div>

                  {/* Node container: [ DATA | NEXT ] */}
                  <div
                    className={`flex items-center border border-[#E8DCC4] ${bgColor} rounded-2xl shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] overflow-hidden transition-all duration-200 select-none`}
                  >
                    {isDoubly && (
                      <div className="bg-[#F7EFE1] px-2 py-3 border-r-[2px] border-[#2B2620] font-mono text-[10px] font-black text-gray-500">
                        prev
                      </div>
                    )}
                    <div className="px-4 py-3 font-mono font-black text-base text-[#2B2620] min-w-[48px] text-center">
                      {node.value}
                    </div>
                    <div className="bg-[#F7EFE1] px-2.5 py-3 border-l-[2px] border-[#2B2620] font-mono text-[10px] font-black text-gray-500">
                      next
                    </div>
                  </div>

                  <span className="text-[10px] font-bold text-gray-500 mt-1">
                    Node #{idx}
                  </span>
                </div>

                {/* Pointer Connector */}
                <div className="flex items-center justify-center text-[#2B2620] px-1">
                  {isDoubly ? (
                    <ArrowLeftRight className="w-6 h-6 stroke-[2.5]" />
                  ) : (
                    <ArrowRight className="w-6 h-6 stroke-[2.5]" />
                  )}
                </div>
              </div>
            );
          })}

          {/* Terminal NULL */}
          <div className="flex flex-col items-center shrink-0">
            <div className="h-6 mb-1"></div>
            <div className="bg-[#F7EFE1] border border-[#E8DCC4] rounded-xl px-3 py-3 font-mono font-black text-xs text-gray-600 shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
              NULL
            </div>
            <span className="text-[10px] font-bold text-gray-400 mt-1">end</span>
          </div>
        </>
      )}
    </div>
  );
};
