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
      <div className="flex items-center gap-3 bg-[#FAF6EC] border-[2px] border-[#1A1A1A] px-4 py-1.5 rounded-full text-xs font-bold shadow-[2px_2px_0px_#1A1A1A]">
        <span>Board: {safeN}×{safeN}</span>
        <span>•</span>
        <span>Current Cell: ({currentRow}, {currentCol})</span>
        <span>•</span>
        <span>Valid Solutions Found: <strong className="text-[#1A1A1A]">{solutionsFound}</strong></span>
      </div>

      {/* Chessboard Grid */}
      <div
        className="grid border-[3px] border-[#1A1A1A] rounded-2xl shadow-[6px_6px_0px_#1A1A1A] overflow-hidden bg-white select-none"
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

            let squareBg = isDarkSquare ? 'bg-[#F5D876]' : 'bg-[#FAF6EC]';

            if (isCurrentTesting && isConflict) {
              squareBg = 'bg-red-200';
            } else if (isCurrentTesting) {
              squareBg = 'bg-[#C9B8F5]';
            } else if (hasQueen) {
              squareBg = 'bg-[#8FD6B0]';
            }

            return (
              <div
                key={`${r}-${c}`}
                className={`border border-[#1A1A1A]/30 flex items-center justify-center relative transition-all duration-150 ${squareBg}`}
              >
                {hasQueen && (
                  <div className="flex items-center justify-center w-8 h-8 rounded-full bg-[#1A1A1A] text-[#F5D876] shadow-[2px_2px_0px_rgba(0,0,0,0.3)]">
                    <Crown className="w-5 h-5 fill-current" />
                  </div>
                )}
                {isCurrentTesting && !hasQueen && (
                  <div className="w-3 h-3 rounded-full bg-[#1A1A1A]/40" />
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
