import React, { useState, useMemo } from 'react';
import { Search, Play, Filter } from 'lucide-react';
import { ALGORITHMS_CATALOG } from '../data/algorithmsCatalog';
import { BrutalistCard } from '../components/common/BrutalistCard';
import { PillBadge } from '../components/common/PillBadge';

interface LibraryViewProps {
  onSelectAlgorithm: (id: string) => void;
}

export const LibraryView: React.FC<LibraryViewProps> = ({ onSelectAlgorithm }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('all');
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>('all');

  const categories: { id: string; label: string }[] = [
    { id: 'all', label: 'All Categories' },
    { id: 'sorting', label: 'Sorting' },
    { id: 'searching', label: 'Searching' },
    { id: 'linear', label: 'Linear Structures' },
    { id: 'trees-heaps', label: 'Trees & Heaps' },
    { id: 'graphs', label: 'Graphs' },
    { id: 'recursion', label: 'Recursion' },
    { id: 'dynamic-programming', label: 'Dynamic Programming' },
  ];

  const difficulties = ['all', 'Easy', 'Medium', 'Hard'];

  const filteredAlgorithms = useMemo(() => {
    return ALGORITHMS_CATALOG.filter((algo) => {
      const matchesSearch =
        algo.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        algo.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
        algo.tags.some((t) => t.toLowerCase().includes(searchQuery.toLowerCase()));

      const matchesCategory =
        selectedCategory === 'all' || algo.category === selectedCategory;

      const matchesDifficulty =
        selectedDifficulty === 'all' || algo.difficulty === selectedDifficulty;

      return matchesSearch && matchesCategory && matchesDifficulty;
    });
  }, [searchQuery, selectedCategory, selectedDifficulty]);

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 md:px-8 py-6">
      {/* Search & Header Section */}
      <div className="bg-white border-2 border-[#2B2620] rounded-[28px] p-6 shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] space-y-4">
        <div>
          <h1 className="text-3xl font-black text-[#2B2620]">Algorithm Library</h1>
          <p className="text-xs sm:text-sm font-bold text-gray-600 mt-1">
            Browse through {ALGORITHMS_CATALOG.length} fully functional, interactive algorithms and data structures.
          </p>
        </div>

        {/* Search Bar */}
        <div className="relative">
          <Search className="w-5 h-5 absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-500" />
          <input
            type="text"
            placeholder="Search by algorithm name, tag (e.g. In-Place, O(n log n), DP), or concept..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-[#F7EFE1] border-2 border-[#2B2620] rounded-2xl pl-11 pr-4 py-3 text-sm font-bold text-[#2B2620] focus:outline-none placeholder:text-gray-400 shadow-[0_3px_10px_rgba(43,38,32,0.16)]"
          />
        </div>

        {/* Category Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar pt-1">
          {categories.map((c) => (
            <button
              key={c.id}
              onClick={() => setSelectedCategory(c.id)}
              className={`px-3.5 py-1.5 rounded-full text-xs font-black transition-all cursor-pointer whitespace-nowrap border-2 border-[#2B2620] ${
                selectedCategory === c.id
                  ? 'bg-[#2B2620] text-white shadow-[0_3px_10px_rgba(43,38,32,0.16)]'
                  : 'bg-[#F7EFE1] text-[#2B2620] hover:bg-white shadow-[0_2px_6px_rgba(43,38,32,0.18)]'
              }`}
            >
              {c.label}
            </button>
          ))}
        </div>

        {/* Difficulty Filter */}
        <div className="flex items-center gap-2 pt-1 border-t border-gray-100">
          <span className="text-xs font-extrabold text-gray-500 uppercase flex items-center gap-1">
            <Filter className="w-3.5 h-3.5" /> Difficulty:
          </span>
          <div className="flex items-center gap-1.5">
            {difficulties.map((d) => (
              <button
                key={d}
                onClick={() => setSelectedDifficulty(d)}
                className={`px-2.5 py-1 rounded-full text-[11px] font-black cursor-pointer border border-[#2B2620] ${
                  selectedDifficulty === d
                    ? 'bg-[#DFA43C] text-[#2B2620]'
                    : 'bg-white text-gray-600 hover:bg-gray-50'
                }`}
              >
                {d === 'all' ? 'All' : d}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Algorithms Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredAlgorithms.map((algo) => {
          let diffBg: 'sage' | 'mustard' | 'terra' = 'sage';
          if (algo.difficulty === 'Medium') diffBg = 'mustard';
          if (algo.difficulty === 'Hard') diffBg = 'terra';

          return (
            <BrutalistCard
              key={algo.id}
              bgColor="white"
              shadow="md"
              className="p-5 flex flex-col justify-between h-[280px] transition-all hover:-translate-y-1"
            >
              <div className="space-y-2.5">
                <div className="flex items-center justify-between gap-2">
                  <span className="text-[10px] font-black uppercase tracking-wider bg-[#F7EFE1] border border-[#2B2620] px-2.5 py-0.5 rounded-full text-gray-700">
                    {algo.category}
                  </span>
                  <PillBadge variant={diffBg} size="sm">
                    {algo.difficulty}
                  </PillBadge>
                </div>

                <div>
                  <h3 className="text-lg font-black text-[#2B2620] leading-snug">
                    {algo.name}
                  </h3>
                  <p className="text-xs font-semibold text-gray-600 line-clamp-3 mt-1 leading-relaxed">
                    {algo.description}
                  </p>
                </div>

                {/* Tags */}
                <div className="flex flex-wrap gap-1">
                  {algo.tags.map((t) => (
                    <span
                      key={t}
                      className="text-[10px] font-bold px-2 py-0.5 rounded bg-gray-100 text-gray-700"
                    >
                      {t}
                    </span>
                  ))}
                </div>
              </div>

              {/* Card Footer: Complexity and Action */}
              <div className="pt-3 border-t border-gray-100 flex items-center justify-between">
                <span className="font-mono text-xs font-black text-gray-800">
                  {algo.complexity.timeWorst}
                </span>

                <button
                  onClick={() => onSelectAlgorithm(algo.id)}
                  className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-[#9CCBA2] text-[#2B2620] text-xs font-black border-2 border-[#2B2620] shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:-translate-y-0.5 active:translate-y-0 cursor-pointer"
                >
                  <Play className="w-3.5 h-3.5 fill-current" />
                  <span>Visualize</span>
                </button>
              </div>
            </BrutalistCard>
          );
        })}
      </div>

      {filteredAlgorithms.length === 0 && (
        <div className="bg-white border-2 border-[#2B2620] rounded-[24px] p-12 text-center shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)]">
          <p className="text-gray-500 font-bold text-sm">
            No algorithms match your search criteria. Try clearing filters or search query!
          </p>
        </div>
      )}
    </div>
  );
};
