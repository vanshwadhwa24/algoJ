import React, { useState } from 'react';
import confetti from 'canvas-confetti';
import { CheckCircle2, XCircle, Sparkles, HelpCircle } from 'lucide-react';
import { PracticeQuestion } from '../../types/practice';

interface PracticeModeProps {
  questions: PracticeQuestion[];
  onScoreUpdate: (score: number) => void;
  userScore: number;
}

export const PracticeMode: React.FC<PracticeModeProps> = ({
  questions,
  onScoreUpdate,
  userScore,
}) => {
  const [selectedAnswers, setSelectedAnswers] = useState<Record<string, number>>({});
  const [submitted, setSubmitted] = useState<Record<string, boolean>>({});

  if (questions.length === 0) {
    return (
      <div className="bg-white border-[2.5px] border-[#1A1A1A] rounded-[24px] p-6 shadow-[4px_4px_0px_#1A1A1A] text-center text-gray-500 font-bold">
        No practice questions available for this algorithm. Check the Algorithm Library!
      </div>
    );
  }

  const handleSelectOption = (questionId: string, optionIdx: number) => {
    if (submitted[questionId]) return;
    setSelectedAnswers((prev) => ({ ...prev, [questionId]: optionIdx }));
  };

  const handleSubmit = (q: PracticeQuestion) => {
    const selectedIdx = selectedAnswers[q.id];
    if (selectedIdx === undefined) return;

    setSubmitted((prev) => ({ ...prev, [q.id]: true }));

    if (selectedIdx === q.correctIndex) {
      // Trigger confetti celebration!
      confetti({
        particleCount: 45,
        spread: 60,
        origin: { y: 0.7 },
        colors: ['#8FD6B0', '#F5D876', '#C9B8F5', '#F5A868'],
      });
      onScoreUpdate(userScore + 10);
    }
  };

  return (
    <div className="bg-white border-[2.5px] border-[#1A1A1A] rounded-[24px] p-5 shadow-[4px_4px_0px_#1A1A1A] space-y-6">
      <div className="flex items-center justify-between border-b pb-3 border-gray-200">
        <div className="flex items-center gap-2">
          <HelpCircle className="w-5 h-5 text-[#1A1A1A]" />
          <h3 className="font-extrabold text-base text-[#1A1A1A]">
            Practice & Knowledge Check
          </h3>
        </div>
        <div className="flex items-center gap-1.5 bg-[#FAF6EC] border-[2px] border-[#1A1A1A] px-3 py-1 rounded-full text-xs font-black shadow-[2px_2px_0px_#1A1A1A]">
          <Sparkles className="w-3.5 h-3.5 text-[#F5A868]" />
          <span>{userScore} Total Points</span>
        </div>
      </div>

      <div className="space-y-6">
        {questions.map((q, qIndex) => {
          const isAnswered = submitted[q.id];
          const userChoice = selectedAnswers[q.id];
          const isCorrect = userChoice === q.correctIndex;

          return (
            <div
              key={q.id}
              className="p-4 rounded-2xl border-[2px] border-[#1A1A1A] bg-[#FAF6EC] shadow-[2px_2px_0px_#1A1A1A] space-y-3"
            >
              <div className="font-bold text-sm text-[#1A1A1A]">
                <span className="text-gray-500 mr-1.5">Q{qIndex + 1}:</span>
                {q.question}
              </div>

              {/* Options */}
              <div className="space-y-2">
                {q.options.map((opt, optIdx) => {
                  const isSelected = userChoice === optIdx;
                  let optStyle = 'bg-white hover:bg-gray-50 text-[#1A1A1A] border-gray-300';

                  if (isAnswered) {
                    if (optIdx === q.correctIndex) {
                      optStyle = 'bg-[#8FD6B0] text-[#1A1A1A] font-black border-[#1A1A1A]';
                    } else if (isSelected && !isCorrect) {
                      optStyle = 'bg-[#F5A868] text-[#1A1A1A] line-through border-[#1A1A1A]';
                    }
                  } else if (isSelected) {
                    optStyle = 'bg-[#F5D876] text-[#1A1A1A] font-bold border-[#1A1A1A]';
                  }

                  return (
                    <button
                      key={optIdx}
                      onClick={() => handleSelectOption(q.id, optIdx)}
                      disabled={isAnswered}
                      className={`w-full text-left px-3.5 py-2.5 rounded-xl border-[2px] text-xs font-semibold transition-all cursor-pointer flex items-center justify-between ${optStyle}`}
                    >
                      <span>{opt}</span>
                      {isAnswered && optIdx === q.correctIndex && (
                        <CheckCircle2 className="w-4 h-4 text-green-800 shrink-0" />
                      )}
                      {isAnswered && isSelected && !isCorrect && (
                        <XCircle className="w-4 h-4 text-red-800 shrink-0" />
                      )}
                    </button>
                  );
                })}
              </div>

              {/* Action and feedback */}
              <div className="pt-2 flex flex-col gap-2">
                {!isAnswered ? (
                  <button
                    onClick={() => handleSubmit(q)}
                    disabled={userChoice === undefined}
                    className="self-end px-4 py-1.5 rounded-full bg-[#1A1A1A] text-white text-xs font-bold border-[2px] border-[#1A1A1A] shadow-[2px_2px_0px_#8FD6B0] hover:-translate-y-0.5 active:translate-y-0 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                  >
                    Check Answer
                  </button>
                ) : (
                  <div
                    className={`p-3 rounded-xl border-[1.5px] border-[#1A1A1A] text-xs font-semibold leading-relaxed ${
                      isCorrect ? 'bg-[#8FD6B0]/25 text-green-950' : 'bg-[#F5A868]/25 text-orange-950'
                    }`}
                  >
                    <strong className="block mb-1">
                      {isCorrect ? 'Correct! (+10 pts)' : 'Incorrect.'}
                    </strong>
                    {q.explanation}
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
