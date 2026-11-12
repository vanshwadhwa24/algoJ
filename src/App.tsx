import React, { useState } from 'react';
import { Navbar, NavTab } from './components/common/Navbar';
import { HomeView } from './views/HomeView';
import { LibraryView } from './views/LibraryView';
import { VisualizerView } from './views/VisualizerView';
import { ComparisonWorkspace } from './components/comparison/ComparisonWorkspace';
import { PracticeMode } from './components/learning/PracticeMode';
import { PRACTICE_QUESTIONS } from './data/practiceQuestions';
import { ErrorBoundary } from './components/common/ErrorBoundary';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<NavTab>('home');
  const [selectedAlgorithmId, setSelectedAlgorithmId] = useState<string>('bubble-sort');

  // Load quiz score from localStorage
  const [quizScore, setQuizScore] = useState<number>(() => {
    try {
      const saved = localStorage.getItem('algoj_quiz_score');
      return saved ? Number(saved) : 0;
    } catch {
      return 0;
    }
  });

  const handleScoreUpdate = (newScore: number) => {
    setQuizScore(newScore);
    try {
      localStorage.setItem('algoj_quiz_score', String(newScore));
    } catch {
      // ignore
    }
  };

  const handleNavigate = (tab: NavTab, algoId?: string) => {
    setActiveTab(tab);
    if (algoId) {
      setSelectedAlgorithmId(algoId);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="min-h-screen bg-[#F7EFE1] text-[#2B2620] flex flex-col font-sans selection:bg-[#DFA43C]">
      {/* Top Navbar */}
      <Navbar
        activeTab={activeTab}
        onSelectTab={setActiveTab}
        quizScore={quizScore}
      />

      {/* Main Content Area */}
      <main className="flex-1 w-full pb-16">
        <ErrorBoundary fallbackTitle="View Rendering Interrupted">
          {activeTab === 'home' && (
            <HomeView onNavigate={handleNavigate} />
          )}

          {activeTab === 'library' && (
            <LibraryView
              onSelectAlgorithm={(id) => handleNavigate('visualizer', id)}
            />
          )}

          {activeTab === 'visualizer' && (
            <VisualizerView
              algorithmId={selectedAlgorithmId}
              onSelectAlgorithm={setSelectedAlgorithmId}
              userScore={quizScore}
              onScoreUpdate={handleScoreUpdate}
            />
          )}

          {activeTab === 'comparison' && (
            <div className="max-w-7xl mx-auto px-4 md:px-8 py-6">
              <ComparisonWorkspace />
            </div>
          )}

          {activeTab === 'practice' && (
            <div className="max-w-4xl mx-auto px-4 md:px-8 py-6">
              <PracticeMode
                questions={PRACTICE_QUESTIONS}
                userScore={quizScore}
                onScoreUpdate={handleScoreUpdate}
              />
            </div>
          )}
        </ErrorBoundary>
      </main>

      {/* Educational Neo-Brutalist Footer */}
      <footer className="border-t-[3px] border-[#2B2620] bg-white py-10 px-4 md:px-8 mt-auto">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-[#9CCBA2] border-2 border-[#2B2620] flex items-center justify-center font-black text-sm shadow-[0_3px_10px_rgba(43,38,32,0.16)]">
              α
            </div>
            <div>
              <span className="font-extrabold text-base tracking-tight text-[#2B2620]">
                Algo<span className="text-[#9CCBA2]">J</span> Visualizer
              </span>
              <p className="text-xs font-bold text-gray-500">
                Interactive Warm Geometric Data Structures & Algorithms Suite
              </p>
            </div>
          </div>

          <div className="flex flex-wrap items-center gap-3 text-xs font-bold">
            <button
              onClick={() => handleNavigate('home')}
              className="hover:underline cursor-pointer"
            >
              Dashboard
            </button>
            <span>•</span>
            <button
              onClick={() => handleNavigate('library')}
              className="hover:underline cursor-pointer"
            >
              Library
            </button>
            <span>•</span>
            <button
              onClick={() => handleNavigate('visualizer')}
              className="hover:underline cursor-pointer"
            >
              Visualizer
            </button>
            <span>•</span>
            <button
              onClick={() => handleNavigate('comparison')}
              className="hover:underline cursor-pointer"
            >
              Comparison
            </button>
            <span>•</span>
            <button
              onClick={() => handleNavigate('practice')}
              className="hover:underline cursor-pointer"
            >
              Quiz Practice
            </button>
          </div>

          <div className="text-xs font-mono font-bold text-gray-500">
            Client-Side Only • Vitest Verified
          </div>
        </div>
      </footer>
    </div>
  );
};

export default App;
