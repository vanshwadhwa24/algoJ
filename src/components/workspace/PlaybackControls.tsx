import React from 'react';
import { Play, Pause, SkipBack, SkipForward, RotateCcw, RotateCw } from 'lucide-react';
import { PlaybackSpeed } from '../../types/execution';

interface PlaybackControlsProps {
  isPlaying: boolean;
  isCompleted: boolean;
  currentStepIndex: number;
  totalSteps: number;
  speed: PlaybackSpeed;
  onPlay: () => void;
  onPause: () => void;
  onStepForward: () => void;
  onStepBackward: () => void;
  onReset: () => void;
  onReplay: () => void;
  onSpeedChange: (speed: PlaybackSpeed) => void;
}

export const PlaybackControls: React.FC<PlaybackControlsProps> = ({
  isPlaying,
  isCompleted,
  currentStepIndex,
  totalSteps,
  speed,
  onPlay,
  onPause,
  onStepForward,
  onStepBackward,
  onReset,
  onReplay,
  onSpeedChange,
}) => {
  const speeds: PlaybackSpeed[] = [0.5, 1, 1.5, 2, 4];

  return (
    <div className="flex flex-wrap items-center justify-between gap-4 p-4 bg-white border border-[#E8DCC4] rounded-[24px] shadow-[0_8px_20px_-6px_rgba(43,38,32,0.22)]">
      {/* Primary Playback Action Buttons */}
      <div className="flex items-center gap-2 sm:gap-3">
        {/* Reset */}
        <button
          onClick={onReset}
          disabled={currentStepIndex === 0 && !isPlaying}
          className="w-10 h-10 rounded-full bg-[#F7EFE1] border border-[#E8DCC4] flex items-center justify-center font-bold text-[#2B2620] shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
          title="Reset to Step 0"
        >
          <RotateCcw className="w-4 h-4" />
        </button>

        {/* Step Backward */}
        <button
          onClick={onStepBackward}
          disabled={currentStepIndex === 0 || isPlaying}
          className="w-10 h-10 rounded-full bg-white border border-[#E8DCC4] flex items-center justify-center font-bold text-[#2B2620] shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
          title="Step Backward (Previous State)"
        >
          <SkipBack className="w-4 h-4" />
        </button>

        {/* Play / Pause Primary Button */}
        {isPlaying ? (
          <button
            onClick={onPause}
            className="px-5 h-11 rounded-full bg-[#DFA43C] border border-[#E8DCC4] flex items-center gap-2 font-black text-[#2B2620] text-sm shadow-[0_4px_12px_rgba(43,38,32,0.18)] hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none cursor-pointer"
          >
            <Pause className="w-4 h-4 fill-current" />
            <span>Pause</span>
          </button>
        ) : (
          <button
            onClick={onPlay}
            disabled={totalSteps <= 1}
            className="px-5 h-11 rounded-full bg-[#9CCBA2] border border-[#E8DCC4] flex items-center gap-2 font-black text-[#2B2620] text-sm shadow-[0_4px_12px_rgba(43,38,32,0.18)] hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
          >
            <Play className="w-4 h-4 fill-current" />
            <span>{isCompleted ? 'Restart' : 'Play'}</span>
          </button>
        )}

        {/* Step Forward */}
        <button
          onClick={onStepForward}
          disabled={currentStepIndex >= totalSteps - 1 || isPlaying}
          className="w-10 h-10 rounded-full bg-white border border-[#E8DCC4] flex items-center justify-center font-bold text-[#2B2620] shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
          title="Step Forward (Next State)"
        >
          <SkipForward className="w-4 h-4" />
        </button>

        {/* Replay */}
        <button
          onClick={onReplay}
          className="w-10 h-10 rounded-full bg-[#8FB5AD] border border-[#E8DCC4] flex items-center justify-center font-bold text-[#2B2620] shadow-[0_3px_10px_rgba(43,38,32,0.16)] hover:-translate-y-0.5 active:translate-y-0.5 active:shadow-none cursor-pointer"
          title="Replay from start"
        >
          <RotateCw className="w-4 h-4" />
        </button>
      </div>

      {/* Speed Controls Pill Bar */}
      <div className="flex items-center gap-1.5 bg-[#F7EFE1] p-1 rounded-full border border-[#E8DCC4]">
        <span className="text-xs font-bold px-2 text-[#2B2620]">Speed:</span>
        {speeds.map((s) => {
          const isSelected = speed === s;
          return (
            <button
              key={s}
              onClick={() => onSpeedChange(s)}
              className={`text-xs font-extrabold px-2.5 py-1 rounded-full transition-all cursor-pointer ${
                isSelected
                  ? 'bg-[#2B2620] text-white'
                  : 'text-[#2B2620] hover:bg-white'
              }`}
            >
              {s}x
            </button>
          );
        })}
      </div>
    </div>
  );
};
