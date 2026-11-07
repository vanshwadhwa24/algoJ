import React, { useState } from 'react';
import { GraphData, getAdjacencyList, getAdjacencyMatrix } from '../../engine/structures/graph';

interface GraphVisualizerProps {
  graph: GraphData;
  onSelectVertex?: (id: string) => void;
  distances?: Record<string, number>;
}

export const GraphVisualizer: React.FC<GraphVisualizerProps> = ({
  graph,
  onSelectVertex,
  distances,
}) => {
  const [viewMode, setViewMode] = useState<'canvas' | 'adjList' | 'adjMatrix'>('canvas');

  if (!graph || !graph.vertices || !graph.edges) {
    return (
      <div className="flex items-center justify-center h-64 text-gray-400 font-bold text-sm">
        No Graph Data Available
      </div>
    );
  }

  const adjList = getAdjacencyList(graph);
  const { labels, matrix } = getAdjacencyMatrix(graph);

  return (
    <div className="flex flex-col items-center w-full space-y-4">
      {/* Top Mode Selector Tabs */}
      <div className="flex items-center gap-2 bg-[#FAF6EC] p-1 rounded-full border-[2px] border-[#1A1A1A]">
        <button
          onClick={() => setViewMode('canvas')}
          className={`px-3 py-1 rounded-full text-xs font-black cursor-pointer transition-all ${
            viewMode === 'canvas' ? 'bg-[#1A1A1A] text-white' : 'text-[#1A1A1A] hover:bg-white'
          }`}
        >
          Visual Graph
        </button>
        <button
          onClick={() => setViewMode('adjList')}
          className={`px-3 py-1 rounded-full text-xs font-black cursor-pointer transition-all ${
            viewMode === 'adjList' ? 'bg-[#1A1A1A] text-white' : 'text-[#1A1A1A] hover:bg-white'
          }`}
        >
          Adjacency List
        </button>
        <button
          onClick={() => setViewMode('adjMatrix')}
          className={`px-3 py-1 rounded-full text-xs font-black cursor-pointer transition-all ${
            viewMode === 'adjMatrix' ? 'bg-[#1A1A1A] text-white' : 'text-[#1A1A1A] hover:bg-white'
          }`}
        >
          Adjacency Matrix
        </button>
      </div>

      {/* Main View Area */}
      {viewMode === 'canvas' && (
        <div className="w-full overflow-x-auto flex justify-center">
          <svg viewBox="50 30 700 280" className="w-full max-w-2xl h-[280px] select-none">
            <defs>
              <marker
                id="arrow"
                viewBox="0 0 10 10"
                refX="24"
                refY="5"
                markerWidth="6"
                markerHeight="6"
                orient="auto-start-reverse"
              >
                <path d="M 0 0 L 10 5 L 0 10 z" fill="#1A1A1A" />
              </marker>
              <marker
                id="arrow-mst"
                viewBox="0 0 10 10"
                refX="24"
                refY="5"
                markerWidth="6"
                markerHeight="6"
                orient="auto-start-reverse"
              >
                <path d="M 0 0 L 10 5 L 0 10 z" fill="#8FD6B0" />
              </marker>
            </defs>

            {/* Edges */}
            {graph.edges.map((e) => {
              const u = graph.vertices.find((v) => v.id === e.source);
              const v = graph.vertices.find((v) => v.id === e.target);
              if (!u || !v) return null;

              const isMst = e.state === 'mst';
              const isTraversed = e.state === 'traversed' || e.state === 'relaxed';
              const isRejected = e.state === 'rejected';
              const isActive = e.state === 'active';

              let strokeColor = '#1A1A1A';
              let strokeWidth = '2.5';
              let strokeDash: string | undefined = undefined;

              if (isMst) {
                strokeColor = '#10b981'; // vibrant green
                strokeWidth = '4.5';
              } else if (isTraversed) {
                strokeColor = '#F5A868'; // Orange
                strokeWidth = '3.5';
              } else if (isActive) {
                strokeColor = '#F5D876'; // Yellow
                strokeWidth = '4';
              } else if (isRejected) {
                strokeColor = '#dc2626'; // Red
                strokeDash = '4,4';
              }

              const midX = (u.x + v.x) / 2;
              const midY = (u.y + v.y) / 2;

              return (
                <g key={e.id}>
                  <line
                    x1={u.x}
                    y1={u.y}
                    x2={v.x}
                    y2={v.y}
                    stroke={strokeColor}
                    strokeWidth={strokeWidth}
                    strokeDasharray={strokeDash}
                    strokeLinecap="round"
                    markerEnd={e.directed ? (isMst ? 'url(#arrow-mst)' : 'url(#arrow)') : undefined}
                  />

                  {/* Weight Badge */}
                  {graph.isWeighted && (
                    <g transform={`translate(${midX}, ${midY})`}>
                      <rect
                        x="-14"
                        y="-10"
                        width="28"
                        height="20"
                        rx="10"
                        fill="#FFFFFF"
                        stroke="#1A1A1A"
                        strokeWidth="1.5"
                      />
                      <text
                        textAnchor="middle"
                        dy=".35em"
                        fontSize="10"
                        fontWeight="900"
                        fontFamily="'JetBrains Mono', monospace"
                        fill="#1A1A1A"
                      >
                        {e.weight}
                      </text>
                    </g>
                  )}
                </g>
              );
            })}

            {/* Vertices */}
            {graph.vertices.map((v) => {
              const hl = v.state;
              let fill = '#FFFFFF';
              if (hl === 'visited') fill = '#8FD6B0'; // Mint
              else if (hl === 'current') fill = '#F5D876'; // Yellow
              else if (hl === 'queued' || hl === 'active') fill = '#C9B8F5'; // Lavender

              const dist = distances?.[v.id];

              return (
                <g
                  key={v.id}
                  transform={`translate(${v.x}, ${v.y})`}
                  className="cursor-pointer"
                  onClick={() => onSelectVertex?.(v.id)}
                >
                  <circle
                    r="22"
                    fill={fill}
                    stroke="#1A1A1A"
                    strokeWidth="2.5"
                    className="filter drop-shadow-[2px_2px_0px_#1A1A1A]"
                  />
                  <text
                    textAnchor="middle"
                    dy=".35em"
                    fontSize="13"
                    fontWeight="900"
                    fontFamily="'JetBrains Mono', monospace"
                    fill="#1A1A1A"
                  >
                    {v.label}
                  </text>

                  {/* Distance Badge below if available */}
                  {dist !== undefined && (
                    <g transform="translate(0, 32)">
                      <rect
                        x="-20"
                        y="-8"
                        width="40"
                        height="16"
                        rx="8"
                        fill="#FAF6EC"
                        stroke="#1A1A1A"
                        strokeWidth="1.2"
                      />
                      <text
                        textAnchor="middle"
                        dy=".35em"
                        fontSize="9"
                        fontWeight="black"
                        fill="#1A1A1A"
                      >
                        d:{dist === Infinity ? '∞' : dist}
                      </text>
                    </g>
                  )}
                </g>
              );
            })}
          </svg>
        </div>
      )}

      {/* Adjacency List View */}
      {viewMode === 'adjList' && (
        <div className="w-full max-w-xl bg-white border-[2.5px] border-[#1A1A1A] rounded-2xl p-4 shadow-[4px_4px_0px_#1A1A1A] space-y-2">
          <h4 className="font-black text-sm uppercase text-gray-500 mb-2">Adjacency List</h4>
          {Object.entries(adjList).map(([source, neighbors]) => (
            <div key={source} className="flex items-center gap-2 text-xs font-mono">
              <span className="w-8 h-8 rounded-full bg-[#FAF6EC] border-[2px] border-[#1A1A1A] flex items-center justify-center font-black">
                {source}
              </span>
              <span className="text-gray-400">→</span>
              <div className="flex items-center gap-2 flex-wrap">
                {neighbors.length === 0 ? (
                  <span className="text-gray-400 italic">None</span>
                ) : (
                  neighbors.map((n, i) => (
                    <span
                      key={i}
                      className="bg-white border-[1.5px] border-[#1A1A1A] px-2 py-1 rounded-xl shadow-[1px_1px_0px_#1A1A1A] font-bold"
                    >
                      {n.target} (w={n.weight})
                    </span>
                  ))
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Adjacency Matrix View */}
      {viewMode === 'adjMatrix' && (
        <div className="w-full max-w-xl bg-white border-[2.5px] border-[#1A1A1A] rounded-2xl p-4 shadow-[4px_4px_0px_#1A1A1A] overflow-x-auto">
          <h4 className="font-black text-sm uppercase text-gray-500 mb-2">Adjacency Matrix</h4>
          <table className="w-full border-collapse font-mono text-xs">
            <thead>
              <tr>
                <th className="p-2 border border-gray-300 bg-[#FAF6EC]"></th>
                {labels.map((l) => (
                  <th key={l} className="p-2 border border-gray-300 bg-[#FAF6EC] font-black">
                    {l}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {labels.map((rowLabel, rIdx) => (
                <tr key={rowLabel}>
                  <td className="p-2 border border-gray-300 bg-[#FAF6EC] font-black text-center">
                    {rowLabel}
                  </td>
                  {matrix[rIdx].map((cell, cIdx) => (
                    <td
                      key={cIdx}
                      className={`p-2 border border-gray-300 text-center font-bold ${
                        cell !== null ? 'bg-amber-50 font-black' : 'text-gray-400'
                      }`}
                    >
                      {cell === null ? '0' : cell}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
