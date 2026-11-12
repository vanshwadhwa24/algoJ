import React from 'react';
import { Crown } from 'lucide-react';

interface ChessboardVisualizerProps {
  n: number;
  board: number[]; // row -> col (-1 if empty)
  currentRow?: number;
  currentCol?: number;
  isConflict?: boolean;
  solutionsFound?: number;
}

export const ChessboardVisualizer: React.FC<ChessboardVisualizerProps> = ({
  n,
  board = [],
  currentRow = 0,
  currentCol = 0,
  isConflict = false,
  solutionsFound = 0,
}) => {
  const safeN = Math.max(1, n || 4);
  const safeBoard = board || [];

  return (
    <div className="flex flex-col items-center w-full p-4 space-y-4">
      {/* Board Statistics Badge */}
      <div className="flex items-center gap-3 bg-[#F7EFE1] border border-[#E8DCC4] px-4 py-1.5 rounded-full text-xs font-bold shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
        <span>Board: {safeN}×{safeN}</span>
        <span>•</span>
        <span>Current Cell: ({currentRow}, {currentCol})</span>
        <span>•</span>
        <span>Valid Solutions Found: <strong className="text-[#2B2620]">{solutionsFound}</strong></span>
      </div>

      {/* Chessboard Grid */}
      <div
        className="grid border border-[#E8DCC4] rounded-2xl shadow-[0_12px_28px_-8px_rgba(43,38,32,0.25)] overflow-hidden bg-white select-none"
        style={{
          gridTemplateColumns: `repeat(${safeN}, minmax(0, 1fr))`,
          width: `${Math.min(360, safeN * 56)}px`,
          height: `${Math.min(360, safeN * 56)}px`,
        }}
      >
        {Array.from({ length: safeN }).map((_, r) =>
          Array.from({ length: safeN }).map((_, c) => {
            const hasQueen = safeBoard[r] === c;
            const isCurrentTesting = r === currentRow && c === currentCol;
            const isDarkSquare = (r + c) % 2 === 1;

            let squareBg = isDarkSquare ? 'bg-[#DFA43C]' : 'bg-[#F7EFE1]';

            if (isCurrentTesting && isConflict) {
              squareBg = 'bg-red-200';
            } else if (isCurrentTesting) {
              squareBg = 'bg-[#8FB5AD]';
            } else if (hasQueen) {
              squareBg = 'bg-[#9CCBA2]';
            }

            return (
              <div
                key={`${r}-${c}`}
                className={`border border-[#2B2620]/30 flex items-center justify-center relative transition-all duration-150 ${squareBg}`}
              >
                {hasQueen && (
                  <div className="flex items-center justify-center w-8 h-8 rounded-full bg-[#2B2620] text-[#DFA43C] shadow-[2px_2px_0px_rgba(0,0,0,0.3)]">
                    <Crown className="w-5 h-5 fill-current" />
                  </div>
                )}
                {isCurrentTesting && !hasQueen && (
                  <div className="w-3 h-3 rounded-full bg-[#2B2620]/40" />
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
