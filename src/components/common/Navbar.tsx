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
      <div className="bg-[#1A1A1A] text-white p-2 md:p-2.5 rounded-full border-[2.5px] border-[#1A1A1A] shadow-[4px_4px_0px_#1A1A1A] flex items-center justify-between gap-2">
        {/* Brand Logo */}
        <button
          onClick={() => onSelectTab('home')}
          className="flex items-center gap-2 pl-3 pr-2 py-1 cursor-pointer focus:outline-none"
        >
          <div className="w-8 h-8 rounded-full bg-[#F5D876] border-[2px] border-white flex items-center justify-center font-black text-[#1A1A1A] text-base shadow-[1px_1px_0px_#fff]">
            α
          </div>
          <span className="font-extrabold text-lg tracking-tight hidden sm:inline text-white">
            Algo<span className="text-[#8FD6B0]">J</span>
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
                    ? 'bg-[#8FD6B0] text-[#1A1A1A] border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#1A1A1A]'
                    : 'text-gray-300 hover:text-white hover:bg-[#2A2A2A]'
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
            className="flex items-center gap-1.5 bg-[#F5A868] text-[#1A1A1A] font-extrabold text-xs px-3 py-1.5 rounded-full border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#1A1A1A] hover:-translate-y-0.5 active:translate-y-0 cursor-pointer"
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
