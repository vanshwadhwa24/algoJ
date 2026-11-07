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
        <div className="flex flex-wrap items-center gap-3 bg-[#FAF6EC] border-[2px] border-[#1A1A1A] px-4 py-2 rounded-2xl shadow-[2px_2px_0px_#1A1A1A] text-xs font-bold">
          {optimalValue !== undefined && (
            <span>Optimal Value: <strong className="text-green-700 font-mono text-sm">{optimalValue}</strong></span>
          )}
          {selectedItems && selectedItems.length > 0 && (
            <span>Items Selected: <strong className="text-[#1A1A1A]">[{selectedItems.join(', ')}]</strong></span>
          )}
          {reconstructedResult && (
            <span>LCS String: <strong className="text-purple-700 font-mono text-sm">"{reconstructedResult}"</strong></span>
          )}
        </div>
      )}

      {/* 2D DP Table Grid */}
      <div className="w-full overflow-x-auto max-h-[340px] flex justify-center">
        <table className="border-collapse font-mono text-xs border-[2.5px] border-[#1A1A1A] bg-white rounded-xl shadow-[4px_4px_0px_#1A1A1A]">
          <thead>
            <tr>
              <th className="p-2 border border-gray-300 bg-[#FAF6EC] font-bold text-gray-500">
                i \ j
              </th>
              {safeColLabels.map((col, cIdx) => (
                <th
                  key={cIdx}
                  className={`p-2 border border-gray-300 font-black text-center min-w-[38px] ${
                    cIdx === currentCol ? 'bg-[#F5D876]' : 'bg-[#FAF6EC]'
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
                    rIdx === currentRow ? 'bg-[#F5D876]' : 'bg-[#FAF6EC]'
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
                  if (isCurrent) cellBg = 'bg-[#8FD6B0] font-black text-sm'; // Mint active
                  else if (isDependency) cellBg = 'bg-[#C9B8F5] font-black'; // Lavender dependency

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
