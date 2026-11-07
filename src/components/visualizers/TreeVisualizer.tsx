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
        <div className="flex items-center gap-2 mb-2 p-2 bg-[#FAF6EC] border-[2px] border-[#1A1A1A] rounded-2xl text-xs font-bold shadow-[2px_2px_0px_#1A1A1A]">
          <span className="text-gray-500">Traversal:</span>
          <div className="flex items-center gap-1.5 flex-wrap">
            {traversalOutput.map((val, idx) => (
              <span
                key={idx}
                className="bg-[#8FD6B0] border border-[#1A1A1A] px-2 py-0.5 rounded-full font-mono font-black"
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
              stroke="#1A1A1A"
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
            if (hl === 'found' || hl === 'inserted' || hl === 'traversed') fill = '#8FD6B0'; // Mint
            else if (hl === 'active') fill = '#F5D876'; // Yellow
            else if (hl === 'imbalanced' || hl === 'deleted') fill = '#F5A868'; // Orange

            const avlNode = node as AVLNode;
            const hasBF = showAVLStats && avlNode.balanceFactor !== undefined;

            return (
              <g key={node.id} transform={`translate(${x}, ${y})`}>
                {/* Node Circle */}
                <circle
                  r="22"
                  fill={fill}
                  stroke="#1A1A1A"
                  strokeWidth="2.5"
                  className="filter drop-shadow-[2px_2px_0px_#1A1A1A]"
                />
                {/* Value Text */}
                <text
                  textAnchor="middle"
                  dy=".35em"
                  fontSize="13"
                  fontWeight="900"
                  fontFamily="'JetBrains Mono', monospace"
                  fill="#1A1A1A"
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
                      fill="#C9B8F5"
                      stroke="#1A1A1A"
                      strokeWidth="1.2"
                    />
                    <text
                      textAnchor="middle"
                      dy=".35em"
                      fontSize="9"
                      fontWeight="bold"
                      fill="#1A1A1A"
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
