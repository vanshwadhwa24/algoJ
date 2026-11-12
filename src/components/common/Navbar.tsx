import React from 'react';
import {
  Compass,
  BookOpen,
  PlayCircle,
  GitCompare,
  CheckCircle2,
  Sparkles,
} from 'lucide-react';

export type NavTab = 'home' | 'library' | 'visualizer' | 'comparison' | 'practice';

interface NavbarProps {
  activeTab: NavTab;
  onSelectTab: (tab: NavTab) => void;
  quizScore?: number;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  onSelectTab,
  quizScore = 0,
}) => {
  const tabs: { id: NavTab; label: string; icon: React.ReactNode }[] = [
    { id: 'home', label: 'Dashboard', icon: <Compass className="w-4 h-4" /> },
    { id: 'library', label: 'Algorithms', icon: <BookOpen className="w-4 h-4" /> },
    { id: 'visualizer', label: 'Visualizer', icon: <PlayCircle className="w-4 h-4" /> },
    { id: 'comparison', label: 'Compare', icon: <GitCompare className="w-4 h-4" /> },
    { id: 'practice', label: 'Practice', icon: <CheckCircle2 className="w-4 h-4" /> },
  ];

  return (
    <header className="sticky top-4 z-50 px-4 md:px-8 max-w-7xl mx-auto w-full">
      <div className="bg-white text-[#2B2620] p-2 md:p-2.5 rounded-full shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)] flex items-center justify-between gap-2">
        {/* Brand Logo */}
        <button
          onClick={() => onSelectTab('home')}
          className="flex items-center gap-2 pl-3 pr-2 py-1 cursor-pointer focus:outline-none"
        >
          <div className="w-8 h-8 rounded-full bg-[#DFA43C] flex items-center justify-center font-black text-[#2B2620] text-base">
            α
          </div>
          <span className="font-extrabold text-lg tracking-tight hidden sm:inline text-[#2B2620]">
            Algo<span className="text-[#C25A35]">J</span>
          </span>
        </button>

        {/* Center Pill Nav Items */}
        <nav className="flex items-center gap-1 sm:gap-1.5 overflow-x-auto no-scrollbar py-0.5">
          {tabs.map((tab) => {
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => onSelectTab(tab.id)}
                className={`flex items-center gap-1.5 px-3.5 sm:px-5 py-1.5 rounded-full text-xs sm:text-sm font-bold transition-all cursor-pointer select-none whitespace-nowrap ${
                  isActive
                    ? 'bg-[#C25A35] text-white shadow-[0_3px_10px_rgba(43,38,32,0.16)]'
                    : 'text-[#6B6154] hover:text-[#2B2620] hover:bg-[#F7EFE1]'
                }`}
              >
                {tab.icon}
                <span>{tab.label}</span>
              </button>
            );
          })}
        </nav>

        {/* Right Action / Progress Pill */}
        <div className="flex items-center gap-2 pr-1.5">
          <button
            onClick={() => onSelectTab('practice')}
            className="flex items-center gap-1.5 bg-[#33706A] text-white font-extrabold text-xs px-3 py-1.5 rounded-full shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:-translate-y-0.5 active:translate-y-0 cursor-pointer"
            title="Practice Quiz Points"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>{quizScore} pts</span>
          </button>
        </div>
      </div>
    </header>
  );
};
