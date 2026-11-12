import React from 'react';

interface DPTableVisualizerProps {
  table: number[][];
  rowLabels: string[];
  colLabels: string[];
  currentRow?: number;
  currentCol?: number;
  activeDependencies?: [number, number][];
  reconstructedResult?: string;
  selectedItems?: string[];
  optimalValue?: number;
}

export const DPTableVisualizer: React.FC<DPTableVisualizerProps> = ({
  table,
  rowLabels = [],
  colLabels = [],
  currentRow = 0,
  currentCol = 0,
  activeDependencies = [],
  reconstructedResult,
  selectedItems,
  optimalValue,
}) => {
  if (!table || !table.length) {
    return (
      <div className="flex items-center justify-center h-64 text-gray-400 font-bold text-sm">
        DP Table is Empty
      </div>
    );
  }

  const safeRowLabels = rowLabels || [];
  const safeColLabels = colLabels || [];

  return (
    <div className="flex flex-col items-center w-full p-4 space-y-4">
      {/* Solution Banner */}
      {(optimalValue !== undefined || reconstructedResult || selectedItems) && (
        <div className="flex flex-wrap items-center gap-3 bg-[#F7EFE1] border border-[#E8DCC4] px-4 py-2 rounded-2xl shadow-[0_3px_10px_rgba(43,38,32,0.16)] text-xs font-bold">
          {optimalValue !== undefined && (
            <span>Optimal Value: <strong className="text-green-700 font-mono text-sm">{optimalValue}</strong></span>
          )}
          {selectedItems && selectedItems.length > 0 && (
            <span>Items Selected: <strong className="text-[#2B2620]">[{selectedItems.join(', ')}]</strong></span>
          )}
          {reconstructedResult && (
            <span>LCS String: <strong className="text-purple-700 font-mono text-sm">"{reconstructedResult}"</strong></span>
          )}
        </div>
      )}

      {/* 2D DP Table Grid */}
      <div className="w-full overflow-x-auto max-h-[340px] flex justify-center">
        <table className="border-collapse font-mono text-xs border border-[#E8DCC4] bg-white rounded-xl shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)]">
          <thead>
            <tr>
              <th className="p-2 border border-gray-300 bg-[#F7EFE1] font-bold text-gray-500">
                i \ j
              </th>
              {safeColLabels.map((col, cIdx) => (
                <th
                  key={cIdx}
                  className={`p-2 border border-gray-300 font-black text-center min-w-[38px] ${
                    cIdx === currentCol ? 'bg-[#DFA43C]' : 'bg-[#F7EFE1]'
                  }`}
                >
                  {col}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {table.map((row, rIdx) => (
              <tr key={rIdx}>
                <td
                  className={`p-2 border border-gray-300 font-black text-center ${
                    rIdx === currentRow ? 'bg-[#DFA43C]' : 'bg-[#F7EFE1]'
                  }`}
                >
                  {safeRowLabels[rIdx] || rIdx}
                </td>
                {row.map((val, cIdx) => {
                  const isCurrent = rIdx === currentRow && cIdx === currentCol;
                  const isDependency = activeDependencies.some(
                    ([depR, depC]) => depR === rIdx && depC === cIdx
                  );

                  let cellBg = 'bg-white';
                  if (isCurrent) cellBg = 'bg-[#9CCBA2] font-black text-sm'; // Mint active
                  else if (isDependency) cellBg = 'bg-[#8FB5AD] font-black'; // Lavender dependency

                  return (
                    <td
                      key={cIdx}
                      className={`p-2 border border-gray-300 text-center select-none transition-colors ${cellBg}`}
                    >
                      {val}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
