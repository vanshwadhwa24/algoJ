import React from 'react';
import {
  Play,
  ArrowRight,
  Sparkles,
  Layers,
  Search,
  Network,
  Cpu,
  Binary,
  GitBranch,
} from 'lucide-react';
import { Starburst } from '../components/common/Starburst';
import { BrutalistCard } from '../components/common/BrutalistCard';
import { ALGORITHMS_CATALOG } from '../data/algorithmsCatalog';
import { NavTab } from '../components/common/Navbar';

interface HomeViewProps {
  onNavigate: (tab: NavTab, algorithmId?: string) => void;
}

export const HomeView: React.FC<HomeViewProps> = ({ onNavigate }) => {
  const categories = [
    {
      id: 'sorting',
      title: 'Sorting Algorithms',
      count: 6,
      color: 'yellow' as const,
      icon: <Layers className="w-5 h-5" />,
      desc: 'Bubble, Selection, Insertion, Merge, Quick, and Heap sort with comparison & swap tracking.',
    },
    {
      id: 'searching',
      title: 'Searching Algorithms',
      count: 2,
      color: 'mint' as const,
      icon: <Search className="w-5 h-5" />,
      desc: 'Linear and Binary Search with eliminated interval highlights and sorted validation.',
    },
    {
      id: 'linear',
      title: 'Linear Data Structures',
      count: 5,
      color: 'lavender' as const,
      icon: <Binary className="w-5 h-5" />,
      desc: 'Arrays, Singly/Doubly Linked Lists, Stacks, Queues, and Hash Tables with collision probing.',
    },
    {
      id: 'trees-heaps',
      title: 'Trees & Heaps',
      count: 4,
      color: 'orange' as const,
      icon: <GitBranch className="w-5 h-5" />,
      desc: 'BST, Binary Heaps with dual synchronized views, self-balancing AVL trees, and Tries.',
    },
    {
      id: 'graphs',
      title: 'Graph Algorithms',
      count: 5,
      color: 'mint' as const,
      icon: <Network className="w-5 h-5" />,
      desc: 'BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, Kruskal, and Prim spanning trees.',
    },
    {
      id: 'recursion',
      title: 'Recursion & DP',
      count: 6,
      color: 'yellow' as const,
      icon: <Cpu className="w-5 h-5" />,
      desc: 'Factorial call stacks, Fibonacci 3 paradigms, Tower of Hanoi, N-Queens, 0/1 Knapsack, and LCS.',
    },
  ];

  return (
    <div className="space-y-12 max-w-7xl mx-auto px-4 md:px-8 py-6">
      {/* Neo-brutalist Hero Section */}
      <section className="relative overflow-hidden bg-white border-[3px] border-[#1A1A1A] rounded-[32px] p-6 sm:p-10 md:p-14 shadow-[8px_8px_0px_#1A1A1A]">
        {/* Starburst accents */}
        <div className="absolute -top-6 -right-6">
          <Starburst size={110} color="#F5D876" className="rotate-12" />
        </div>
        <div className="absolute -bottom-8 -left-8 opacity-80">
          <Starburst size={90} color="#C9B8F5" className="-rotate-12" />
        </div>

        <div className="relative z-10 max-w-3xl space-y-6">
          {/* Pill Badge Tag */}
          <div className="inline-flex items-center gap-2 bg-[#8FD6B0] border-[2px] border-[#1A1A1A] rounded-full px-4 py-1.5 shadow-[2px_2px_0px_#1A1A1A]">
            <Sparkles className="w-4 h-4 text-[#1A1A1A]" />
            <span className="font-extrabold text-xs sm:text-sm text-[#1A1A1A] tracking-wide uppercase">
              Deterministic Stepping • Zero Backend Required
            </span>
          </div>

          {/* Oversized Headline */}
          <h1 className="text-4xl sm:text-6xl md:text-7xl font-black text-[#1A1A1A] tracking-tight leading-[1.05]">
            Master Algorithms <br />
            <span className="bg-[#F5D876] px-2 rounded-xl border-[2px] border-[#1A1A1A] inline-block shadow-[3px_3px_0px_#1A1A1A]">
              Step by Step.
            </span>
          </h1>

          {/* Tagline */}
          <p className="text-base sm:text-xl font-bold text-gray-700 leading-relaxed max-w-2xl">
            A bold neo-brutalist educational platform where every sorting pass, tree rotation, and dynamic programming cell is rendered with genuine state execution and reversible time-travel stepping.
          </p>

          {/* Action Buttons */}
          <div className="flex flex-wrap items-center gap-4 pt-2">
            <button
              onClick={() => onNavigate('visualizer', 'bubble-sort')}
              className="px-7 py-3.5 rounded-full bg-[#1A1A1A] text-white font-black text-sm sm:text-base border-[2.5px] border-[#1A1A1A] shadow-[4px_4px_0px_#8FD6B0] hover:-translate-y-1 active:translate-y-0.5 active:shadow-none transition-all flex items-center gap-2 cursor-pointer"
            >
              <Play className="w-4 h-4 fill-current text-[#8FD6B0]" />
              <span>Launch Visualizer</span>
            </button>

            <button
              onClick={() => onNavigate('library')}
              className="px-7 py-3.5 rounded-full bg-[#FAF6EC] text-[#1A1A1A] font-black text-sm sm:text-base border-[2.5px] border-[#1A1A1A] shadow-[4px_4px_0px_#1A1A1A] hover:-translate-y-1 active:translate-y-0.5 active:shadow-none transition-all flex items-center gap-2 cursor-pointer"
            >
              <span>Explore Library</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </section>

      {/* Featured Statistics Dashboard Bar */}
      <section className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <BrutalistCard bgColor="yellow" shadow="md" className="p-4 text-center">
          <span className="text-3xl sm:text-4xl font-black font-mono block">28+</span>
          <span className="text-xs font-black uppercase text-[#1A1A1A] tracking-wider">
            Real DSA Engines
          </span>
        </BrutalistCard>

        <BrutalistCard bgColor="mint" shadow="md" className="p-4 text-center">
          <span className="text-3xl sm:text-4xl font-black font-mono block">100%</span>
          <span className="text-xs font-black uppercase text-[#1A1A1A] tracking-wider">
            Reversible History
          </span>
        </BrutalistCard>

        <BrutalistCard bgColor="lavender" shadow="md" className="p-4 text-center">
          <span className="text-3xl sm:text-4xl font-black font-mono block">O(1)</span>
          <span className="text-xs font-black uppercase text-[#1A1A1A] tracking-wider">
            Local Persistence
          </span>
        </BrutalistCard>

        <BrutalistCard bgColor="orange" shadow="md" className="p-4 text-center">
          <span className="text-3xl sm:text-4xl font-black font-mono block">125</span>
          <span className="text-xs font-black uppercase text-[#1A1A1A] tracking-wider">
            Verified Unit Tests
          </span>
        </BrutalistCard>
      </section>

      {/* Categories Grid */}
      <section className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl sm:text-3xl font-black text-[#1A1A1A]">
              Curated DSA Categories
            </h2>
            <p className="text-sm font-bold text-gray-600 mt-0.5">
              Select any topic to begin exploring interactive step execution.
            </p>
          </div>
          <button
            onClick={() => onNavigate('library')}
            className="text-xs sm:text-sm font-black underline text-[#1A1A1A] hover:text-gray-600 cursor-pointer"
          >
            View All ({ALGORITHMS_CATALOG.length})
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {categories.map((cat) => (
            <BrutalistCard
              key={cat.id}
              bgColor={cat.color}
              shadow="md"
              className="p-6 flex flex-col justify-between h-56 transition-all hover:-translate-y-1"
              onClick={() => onNavigate('library')}
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div className="w-10 h-10 rounded-full bg-white border-[2px] border-[#1A1A1A] flex items-center justify-center shadow-[2px_2px_0px_#1A1A1A]">
                    {cat.icon}
                  </div>
                  <span className="bg-[#1A1A1A] text-white text-[11px] font-black px-2.5 py-1 rounded-full">
                    {cat.count} Topics
                  </span>
                </div>
                <h3 className="text-lg font-black text-[#1A1A1A] mb-1">
                  {cat.title}
                </h3>
                <p className="text-xs font-semibold text-gray-800 leading-relaxed">
                  {cat.desc}
                </p>
              </div>

              <div className="flex items-center gap-1.5 text-xs font-black pt-2 text-[#1A1A1A]">
                <span>Explore Category</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </div>
            </BrutalistCard>
          ))}
        </div>
      </section>

      {/* Comparison & Practice Callout */}
      <section className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-[#FAF6EC] border-[2.5px] border-[#1A1A1A] rounded-[28px] p-6 shadow-[4px_4px_0px_#1A1A1A] flex flex-col justify-between">
          <div className="space-y-2">
            <span className="bg-[#F5D876] text-[#1A1A1A] text-[10px] font-black px-2.5 py-0.5 rounded-full border border-[#1A1A1A] uppercase">
              Side-by-Side Arena
            </span>
            <h3 className="text-xl font-black text-[#1A1A1A]">
              Compare Two Algorithms In Real-Time
            </h3>
            <p className="text-xs font-semibold text-gray-700 leading-relaxed">
              Feed the exact same unsorted array to Bubble Sort and Quick Sort simultaneously. Watch comparisons, swaps, and asymptotic efficiency unfold side-by-side.
            </p>
          </div>
          <button
            onClick={() => onNavigate('comparison')}
            className="mt-4 self-start px-5 py-2.5 rounded-full bg-[#1A1A1A] text-white text-xs font-black border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#F5D876] hover:-translate-y-0.5 active:translate-y-0 cursor-pointer"
          >
            Launch Comparison Mode
          </button>
        </div>

        <div className="bg-[#FAF6EC] border-[2.5px] border-[#1A1A1A] rounded-[28px] p-6 shadow-[4px_4px_0px_#1A1A1A] flex flex-col justify-between">
          <div className="space-y-2">
            <span className="bg-[#8FD6B0] text-[#1A1A1A] text-[10px] font-black px-2.5 py-0.5 rounded-full border border-[#1A1A1A] uppercase">
              Interactive Quiz
            </span>
            <h3 className="text-xl font-black text-[#1A1A1A]">
              Test Your Conceptual Understanding
            </h3>
            <p className="text-xs font-semibold text-gray-700 leading-relaxed">
              Answer targeted questions on time complexities, recurrence relations, tree rotations, and edge cases. Score points and track progress in your browser.
            </p>
          </div>
          <button
            onClick={() => onNavigate('practice')}
            className="mt-4 self-start px-5 py-2.5 rounded-full bg-[#8FD6B0] text-[#1A1A1A] text-xs font-black border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#1A1A1A] hover:-translate-y-0.5 active:translate-y-0 cursor-pointer"
          >
            Start Practice Quiz
          </button>
        </div>
      </section>
    </div>
  );
};
