import React from 'react';
import { BSTNode } from '../../engine/structures/bst';
import { AVLNode } from '../../engine/structures/avl';

interface TreeVisualizerProps {
  root: BSTNode | AVLNode | null;
  showAVLStats?: boolean;
  traversalOutput?: number[];
}

export const TreeVisualizer: React.FC<TreeVisualizerProps> = ({
  root,
  showAVLStats = false,
  traversalOutput,
}) => {
  if (!root) {
    return (
      <div className="flex items-center justify-center h-72 text-gray-400 font-bold text-sm">
        Tree is Empty
      </div>
    );
  }

  // Flatten nodes and edges for SVG
  const nodesList: (BSTNode | AVLNode)[] = [];
  const edgesList: { x1: number; y1: number; x2: number; y2: number }[] = [];

  function traverse(n: BSTNode | AVLNode | null) {
    if (!n) return;
    nodesList.push(n);

    if (n.left && n.x !== undefined && n.y !== undefined && n.left.x !== undefined && n.left.y !== undefined) {
      edgesList.push({ x1: n.x, y1: n.y, x2: n.left.x, y2: n.left.y });
      traverse(n.left);
    }
    if (n.right && n.x !== undefined && n.y !== undefined && n.right.x !== undefined && n.right.y !== undefined) {
      edgesList.push({ x1: n.x, y1: n.y, x2: n.right.x, y2: n.right.y });
      traverse(n.right);
    }
  }

  traverse(root);

  return (
    <div className="flex flex-col items-center w-full">
      {/* Traversal Output Pill Stream */}
      {traversalOutput && traversalOutput.length > 0 && (
        <div className="flex items-center gap-2 mb-2 p-2 bg-[#F7EFE1] border border-[#E8DCC4] rounded-2xl text-xs font-bold shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
          <span className="text-gray-500">Traversal:</span>
          <div className="flex items-center gap-1.5 flex-wrap">
            {traversalOutput.map((val, idx) => (
              <span
                key={idx}
                className="bg-[#9CCBA2] border border-[#2B2620] px-2 py-0.5 rounded-full font-mono font-black"
              >
                {val}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* SVG Canvas for Tree */}
      <div className="w-full overflow-x-auto flex justify-center min-h-[300px]">
        <svg
          viewBox="100 20 600 320"
          className="w-full max-w-2xl h-[300px] select-none"
        >
          {/* Edges */}
          {edgesList.map((edge, idx) => (
            <line
              key={idx}
              x1={edge.x1}
              y1={edge.y1}
              x2={edge.x2}
              y2={edge.y2}
              stroke="#2B2620"
              strokeWidth="2.5"
              strokeLinecap="round"
            />
          ))}

          {/* Nodes */}
          {nodesList.map((node) => {
            const x = node.x ?? 400;
            const y = node.y ?? 60;
            const hl = node.highlight;

            let fill = '#FFFFFF';
            if (hl === 'found' || hl === 'inserted' || hl === 'traversed') fill = '#9CCBA2'; // Mint
            else if (hl === 'active') fill = '#DFA43C'; // Yellow
            else if (hl === 'imbalanced' || hl === 'deleted') fill = '#D97B54'; // Orange

            const avlNode = node as AVLNode;
            const hasBF = showAVLStats && avlNode.balanceFactor !== undefined;

            return (
              <g key={node.id} transform={`translate(${x}, ${y})`}>
                {/* Node Circle */}
                <circle
                  r="22"
                  fill={fill}
                  stroke="#2B2620"
                  strokeWidth="2.5"
                  className="filter drop-shadow-[0_3px_10px_rgba(43,38,32,0.16)]"
                />
                {/* Value Text */}
                <text
                  textAnchor="middle"
                  dy=".35em"
                  fontSize="13"
                  fontWeight="900"
                  fontFamily="'JetBrains Mono', monospace"
                  fill="#2B2620"
                >
                  {node.value}
                </text>

                {/* Optional AVL Balance Factor and Height Pill */}
                {hasBF && (
                  <g transform="translate(0, 32)">
                    <rect
                      x="-22"
                      y="-7"
                      width="44"
                      height="15"
                      rx="7"
                      fill="#8FB5AD"
                      stroke="#2B2620"
                      strokeWidth="1.2"
                    />
                    <text
                      textAnchor="middle"
                      dy=".35em"
                      fontSize="9"
                      fontWeight="bold"
                      fill="#2B2620"
                    >
                      BF:{avlNode.balanceFactor}
                    </text>
                  </g>
                )}
              </g>
            );
          })}
        </svg>
      </div>
    </div>
  );
};
