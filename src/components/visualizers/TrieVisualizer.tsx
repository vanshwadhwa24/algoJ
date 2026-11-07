import React from 'react';
import { TrieNode } from '../../engine/structures/trie';

interface TrieVisualizerProps {
  root: TrieNode;
  wordsList?: string[];
  searchResults?: string[];
}

export const TrieVisualizer: React.FC<TrieVisualizerProps> = ({
  root,
  wordsList = [],
  searchResults = [],
}) => {
  if (!root || !root.children) {
    return (
      <div className="flex items-center justify-center h-64 text-gray-400 font-bold text-sm">
        Trie is Empty
      </div>
    );
  }

  // Recursive renderer for Trie tree representation
  function renderTrieNode(node: TrieNode, path = ''): React.ReactNode {
    if (!node || !node.children) return null;
    const childrenKeys = Object.keys(node.children).sort();
    const isRoot = node.id === 'root';

    return (
      <div key={node.id} className="flex flex-col items-center">
        {/* Node Circle */}
        <div
          className={`flex items-center justify-center border-[2.5px] border-[#1A1A1A] rounded-full shadow-[2px_2px_0px_#1A1A1A] select-none transition-all ${
            isRoot
              ? 'w-12 h-12 bg-[#FAF6EC] font-black text-xs'
              : node.highlight === 'found'
              ? 'w-11 h-11 bg-[#8FD6B0] text-[#1A1A1A] font-black text-sm'
              : node.highlight === 'active'
              ? 'w-11 h-11 bg-[#F5D876] text-[#1A1A1A] font-black text-sm'
              : 'w-11 h-11 bg-white text-[#1A1A1A] font-black text-sm'
          }`}
        >
          {isRoot ? 'ROOT' : node.char.toUpperCase()}
        </div>

        {/* End of Word Terminal Indicator */}
        {node.isEndOfWord && (
          <span className="bg-[#1A1A1A] text-white text-[9px] font-black px-1.5 py-0.5 rounded-full mt-1 border border-[#1A1A1A]">
            WORD
          </span>
        )}

        {/* Children Row */}
        {childrenKeys.length > 0 && (
          <div className="flex items-start gap-4 sm:gap-6 mt-4 pt-3 border-t-[2px] border-dashed border-[#1A1A1A]">
            {childrenKeys.map((ch) => renderTrieNode(node.children[ch], path + ch))}
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center w-full p-4 space-y-4">
      {/* Current Stored Words List */}
      <div className="flex flex-wrap items-center gap-2 bg-[#FAF6EC] border-[2px] border-[#1A1A1A] p-2.5 rounded-2xl text-xs font-bold shadow-[2px_2px_0px_#1A1A1A]">
        <span className="text-gray-500">Stored Words:</span>
        {wordsList.map((w) => (
          <span
            key={w}
            className="bg-white border border-[#1A1A1A] px-2.5 py-1 rounded-full font-mono text-[#1A1A1A]"
          >
            "{w}"
          </span>
        ))}
      </div>

      {/* Autocomplete / Search Output */}
      {searchResults.length > 0 && (
        <div className="flex flex-wrap items-center gap-2 bg-[#8FD6B0] border-[2px] border-[#1A1A1A] p-2.5 rounded-2xl text-xs font-black shadow-[2px_2px_0px_#1A1A1A]">
          <span>Completions Found:</span>
          {searchResults.map((w) => (
            <span
              key={w}
              className="bg-[#1A1A1A] text-white px-2 py-0.5 rounded-full font-mono"
            >
              {w}
            </span>
          ))}
        </div>
      )}

      {/* Trie Tree Graph */}
      <div className="w-full overflow-x-auto flex justify-center py-4">
        {renderTrieNode(root)}
      </div>
    </div>
  );
};
