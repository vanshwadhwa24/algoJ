import React from 'react';
import { HeapElement } from '../../engine/structures/heap';

interface HeapDualVisualizerProps {
  array: HeapElement[];
  type: 'min' | 'max';
  activeIndices?: number[];
  comparingIndices?: number[];
}

export const HeapDualVisualizer: React.FC<HeapDualVisualizerProps> = ({
  array = [],
  type,
  activeIndices = [],
  comparingIndices = [],
}) => {
  const safeArray = array || [];
  // Compute tree coordinates for nodes based on level-order index i
  // Level 0 (root): index 0, Level 1: 1, 2, Level 2: 3, 4, 5, 6, etc.
  const nodeCoords = [
    { x: 300, y: 40 }, // 0
    { x: 180, y: 110 }, // 1
    { x: 420, y: 110 }, // 2
    { x: 120, y: 180 }, // 3
    { x: 240, y: 180 }, // 4
    { x: 360, y: 180 }, // 5
    { x: 480, y: 180 }, // 6
  ];

  return (
    <div className="flex flex-col items-center w-full space-y-6">
      {/* 1. Synchronized Tree View */}
      <div className="w-full flex flex-col items-center">
        <span className="text-[11px] font-extrabold uppercase text-gray-500 mb-1">
          Binary Heap Tree View ({type === 'min' ? 'Min' : 'Max'} Property: parent {type === 'min' ? '≤' : '≥'} children)
        </span>

        <svg viewBox="80 15 440 195" className="w-full max-w-xl h-[190px] select-none">
          {/* Edges from parent Math.floor((i-1)/2) to child i */}
          {safeArray.map((_, i) => {
            if (i === 0 || i >= nodeCoords.length) return null;
            const parentIdx = Math.floor((i - 1) / 2);
            const parentCoord = nodeCoords[parentIdx];
            const childCoord = nodeCoords[i];

            return (
              <line
                key={`edge-${i}`}
                x1={parentCoord.x}
                y1={parentCoord.y}
                x2={childCoord.x}
                y2={childCoord.y}
                stroke="#1A1A1A"
                strokeWidth="2.5"
                strokeLinecap="round"
              />
            );
          })}

          {/* Tree Nodes */}
          {safeArray.map((el, i) => {
            if (i >= nodeCoords.length) return null;
            const coord = nodeCoords[i];
            const isComparing = comparingIndices.includes(i) || el.state === 'comparing';
            const isActive = activeIndices.includes(i);
            const isViolation = el.state === 'violation';
            const isPlaced = el.state === 'placed';

            let fill = '#FFFFFF';
            if (isViolation) fill = '#F5A868'; // Orange
            else if (isComparing) fill = '#F5D876'; // Yellow
            else if (isPlaced) fill = '#8FD6B0'; // Mint
            else if (isActive) fill = '#C9B8F5'; // Lavender

            return (
              <g key={`tree-node-${i}`} transform={`translate(${coord.x}, ${coord.y})`}>
                <circle
                  r="20"
                  fill={fill}
                  stroke="#1A1A1A"
                  strokeWidth="2.5"
                  className="filter drop-shadow-[2px_2px_0px_#1A1A1A]"
                />
                <text
                  textAnchor="middle"
                  dy=".35em"
                  fontSize="12"
                  fontWeight="900"
                  fontFamily="'JetBrains Mono', monospace"
                  fill="#1A1A1A"
                >
                  {el.value}
                </text>
                <text
                  textAnchor="middle"
                  dy="2.5em"
                  fontSize="9"
                  fontWeight="bold"
                  fill="#666"
                >
                  [{i}]
                </text>
              </g>
            );
          })}
        </svg>
      </div>

      {/* 2. Synchronized Array View */}
      <div className="w-full flex flex-col items-center">
        <span className="text-[11px] font-extrabold uppercase text-gray-500 mb-1">
          Synchronized Array Representation
        </span>

        <div className="flex flex-wrap items-center justify-center gap-2">
          {safeArray.map((el, idx) => {
            const isComparing = comparingIndices.includes(idx) || el.state === 'comparing';
            const isViolation = el.state === 'violation';
            const isPlaced = el.state === 'placed';

            let bgColor = 'bg-white';
            if (isViolation) bgColor = 'bg-[#F5A868]'; // Orange
            else if (isComparing) bgColor = 'bg-[#F5D876]'; // Yellow
            else if (isPlaced) bgColor = 'bg-[#8FD6B0]'; // Mint

            return (
              <div key={`arr-slot-${idx}`} className="flex flex-col items-center">
                <div
                  className={`w-12 h-14 rounded-2xl border-[2.5px] border-[#1A1A1A] ${bgColor} shadow-[2px_2px_0px_#1A1A1A] flex flex-col justify-between items-center py-1.5 select-none transition-all`}
                >
                  <span className="font-mono font-black text-sm">{el.value}</span>
                  <span className="text-[9px] font-bold text-gray-500">[{idx}]</span>
                </div>
                {idx === 0 && (
                  <span className="text-[9px] font-black text-[#1A1A1A] mt-0.5">
                    ROOT
                  </span>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
