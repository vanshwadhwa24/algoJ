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
          <span className="font-mono text-sm font-bold bg-[#FAF6EC] px-3 py-1.5 rounded-full border-[2px] border-[#1A1A1A]">
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
            if (isNew) bgColor = 'bg-[#8FD6B0]'; // Mint
            else if (isActive) bgColor = 'bg-[#F5D876]'; // Yellow
            else if (isDeleted) bgColor = 'bg-[#F5A868] line-through opacity-70'; // Orange

            return (
              <div key={node.id} className="flex items-center gap-2 shrink-0">
                {/* Node Box */}
                <div className="flex flex-col items-center">
                  {/* Head / Tail Labels */}
                  <div className="h-6 flex items-center gap-1 mb-1">
                    {isHead && (
                      <span className="bg-[#1A1A1A] text-white text-[10px] font-black px-2 py-0.5 rounded-full border border-[#1A1A1A]">
                        HEAD
                      </span>
                    )}
                    {isTail && (
                      <span className="bg-[#C9B8F5] text-[#1A1A1A] text-[10px] font-black px-2 py-0.5 rounded-full border border-[#1A1A1A]">
                        TAIL
                      </span>
                    )}
                  </div>

                  {/* Node container: [ DATA | NEXT ] */}
                  <div
                    className={`flex items-center border-[2.5px] border-[#1A1A1A] ${bgColor} rounded-2xl shadow-[4px_4px_0px_#1A1A1A] overflow-hidden transition-all duration-200 select-none`}
                  >
                    {isDoubly && (
                      <div className="bg-[#FAF6EC] px-2 py-3 border-r-[2px] border-[#1A1A1A] font-mono text-[10px] font-black text-gray-500">
                        prev
                      </div>
                    )}
                    <div className="px-4 py-3 font-mono font-black text-base text-[#1A1A1A] min-w-[48px] text-center">
                      {node.value}
                    </div>
                    <div className="bg-[#FAF6EC] px-2.5 py-3 border-l-[2px] border-[#1A1A1A] font-mono text-[10px] font-black text-gray-500">
                      next
                    </div>
                  </div>

                  <span className="text-[10px] font-bold text-gray-500 mt-1">
                    Node #{idx}
                  </span>
                </div>

                {/* Pointer Connector */}
                <div className="flex items-center justify-center text-[#1A1A1A] px-1">
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
            <div className="bg-[#FAF6EC] border-[2px] border-[#1A1A1A] rounded-xl px-3 py-3 font-mono font-black text-xs text-gray-600 shadow-[2px_2px_0px_#1A1A1A]">
              NULL
            </div>
            <span className="text-[10px] font-bold text-gray-400 mt-1">end</span>
          </div>
        </>
      )}
    </div>
  );
};
