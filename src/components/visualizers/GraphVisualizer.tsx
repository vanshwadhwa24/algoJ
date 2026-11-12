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
      <div className="flex items-center gap-2 bg-[#F7EFE1] p-1 rounded-full border border-[#E8DCC4]">
        <button
          onClick={() => setViewMode('canvas')}
          className={`px-3 py-1 rounded-full text-xs font-black cursor-pointer transition-all ${
            viewMode === 'canvas' ? 'bg-[#2B2620] text-white' : 'text-[#2B2620] hover:bg-white'
          }`}
        >
          Visual Graph
        </button>
        <button
          onClick={() => setViewMode('adjList')}
          className={`px-3 py-1 rounded-full text-xs font-black cursor-pointer transition-all ${
            viewMode === 'adjList' ? 'bg-[#2B2620] text-white' : 'text-[#2B2620] hover:bg-white'
          }`}
        >
          Adjacency List
        </button>
        <button
          onClick={() => setViewMode('adjMatrix')}
          className={`px-3 py-1 rounded-full text-xs font-black cursor-pointer transition-all ${
            viewMode === 'adjMatrix' ? 'bg-[#2B2620] text-white' : 'text-[#2B2620] hover:bg-white'
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
                <path d="M 0 0 L 10 5 L 0 10 z" fill="#2B2620" />
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
                <path d="M 0 0 L 10 5 L 0 10 z" fill="#9CCBA2" />
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

              let strokeColor = '#2B2620';
              let strokeWidth = '2.5';
              let strokeDash: string | undefined = undefined;

              if (isMst) {
                strokeColor = '#10b981'; // vibrant green
                strokeWidth = '4.5';
              } else if (isTraversed) {
                strokeColor = '#D97B54'; // Orange
                strokeWidth = '3.5';
              } else if (isActive) {
                strokeColor = '#DFA43C'; // Yellow
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
                        stroke="#2B2620"
                        strokeWidth="1.5"
                      />
                      <text
                        textAnchor="middle"
                        dy=".35em"
                        fontSize="10"
                        fontWeight="900"
                        fontFamily="'JetBrains Mono', monospace"
                        fill="#2B2620"
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
              if (hl === 'visited') fill = '#9CCBA2'; // Mint
              else if (hl === 'current') fill = '#DFA43C'; // Yellow
              else if (hl === 'queued' || hl === 'active') fill = '#8FB5AD'; // Lavender

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
                    stroke="#2B2620"
                    strokeWidth="2.5"
                    className="filter drop-shadow-[0_3px_10px_rgba(43,38,32,0.16)]"
                  />
                  <text
                    textAnchor="middle"
                    dy=".35em"
                    fontSize="13"
                    fontWeight="900"
                    fontFamily="'JetBrains Mono', monospace"
                    fill="#2B2620"
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
                        fill="#F7EFE1"
                        stroke="#2B2620"
                        strokeWidth="1.2"
                      />
                      <text
                        textAnchor="middle"
                        dy=".35em"
                        fontSize="9"
                        fontWeight="black"
                        fill="#2B2620"
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
        <div className="w-full max-w-xl bg-white border border-[#E8DCC4] rounded-2xl p-4 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-2">
          <h4 className="font-black text-sm uppercase text-gray-500 mb-2">Adjacency List</h4>
          {Object.entries(adjList).map(([source, neighbors]) => (
            <div key={source} className="flex items-center gap-2 text-xs font-mono">
              <span className="w-8 h-8 rounded-full bg-[#F7EFE1] border border-[#E8DCC4] flex items-center justify-center font-black">
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
                      className="bg-white border-[1.5px] border-[#2B2620] px-2 py-1 rounded-xl shadow-[0_2px_6px_rgba(43,38,32,0.18)] font-bold"
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
        <div className="w-full max-w-xl bg-white border border-[#E8DCC4] rounded-2xl p-4 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] overflow-x-auto">
          <h4 className="font-black text-sm uppercase text-gray-500 mb-2">Adjacency Matrix</h4>
          <table className="w-full border-collapse font-mono text-xs">
            <thead>
              <tr>
                <th className="p-2 border border-gray-300 bg-[#F7EFE1]"></th>
                {labels.map((l) => (
                  <th key={l} className="p-2 border border-gray-300 bg-[#F7EFE1] font-black">
                    {l}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {labels.map((rowLabel, rIdx) => (
                <tr key={rowLabel}>
                  <td className="p-2 border border-gray-300 bg-[#F7EFE1] font-black text-center">
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
