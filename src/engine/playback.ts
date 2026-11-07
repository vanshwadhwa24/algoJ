import { useState, useEffect, useRef, useCallback } from 'react';
import { ExecutionStep, PlaybackSpeed } from '../types/execution';

export interface UsePlaybackOptions<T> {
  steps: ExecutionStep<T>[];
  initialSpeed?: PlaybackSpeed;
  onComplete?: () => void;
}

export function usePlayback<T>({
  steps,
  initialSpeed = 1,
  onComplete,
}: UsePlaybackOptions<T>) {
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [speed, setSpeed] = useState<PlaybackSpeed>(initialSpeed);
  const timerRef = useRef<number | null>(null);

  // When steps array changes (e.g., input updated), reset step index safely
  useEffect(() => {
    setCurrentStepIndex(0);
    setIsPlaying(false);
  }, [steps]);

  const totalSteps = steps.length;
  const currentStep = steps[currentStepIndex] || steps[0] || null;
  const isCompleted = totalSteps > 0 && currentStepIndex === totalSteps - 1;

  const pause = useCallback(() => {
    setIsPlaying(false);
    if (timerRef.current !== null) {
      window.clearTimeout(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  const stepForward = useCallback(() => {
    setCurrentStepIndex((prev) => {
      if (prev < totalSteps - 1) {
        return prev + 1;
      }
      setIsPlaying(false);
      return prev;
    });
  }, [totalSteps]);

  const stepBackward = useCallback(() => {
    pause();
    setCurrentStepIndex((prev) => Math.max(0, prev - 1));
  }, [pause]);

  const goToStep = useCallback(
    (index: number) => {
      pause();
      const clamped = Math.max(0, Math.min(totalSteps - 1, index));
      setCurrentStepIndex(clamped);
    },
    [pause, totalSteps]
  );

  const reset = useCallback(() => {
    pause();
    setCurrentStepIndex(0);
  }, [pause]);

  const play = useCallback(() => {
    if (totalSteps <= 1) return;
    if (currentStepIndex >= totalSteps - 1) {
      setCurrentStepIndex(0);
    }
    setIsPlaying(true);
  }, [totalSteps, currentStepIndex]);

  const replay = useCallback(() => {
    pause();
    setCurrentStepIndex(0);
    setTimeout(() => {
      setIsPlaying(true);
    }, 50);
  }, [pause]);

  // Playback timer loop
  useEffect(() => {
    if (!isPlaying) {
      if (timerRef.current !== null) {
        window.clearTimeout(timerRef.current);
        timerRef.current = null;
      }
      return;
    }

    if (currentStepIndex >= totalSteps - 1) {
      setIsPlaying(false);
      onComplete?.();
      return;
    }

    // Base interval is 700ms, scaled by speed
    const delay = Math.max(80, Math.round(750 / speed));

    timerRef.current = window.setTimeout(() => {
      setCurrentStepIndex((prev) => {
        const next = prev + 1;
        if (next >= totalSteps - 1) {
          setIsPlaying(false);
          onComplete?.();
        }
        return next;
      });
    }, delay);

    return () => {
      if (timerRef.current !== null) {
        window.clearTimeout(timerRef.current);
        timerRef.current = null;
      }
    };
  }, [isPlaying, currentStepIndex, totalSteps, speed, onComplete]);

  return {
    currentStepIndex,
    currentStep,
    totalSteps,
    isPlaying,
    speed,
    isCompleted,
    play,
    pause,
    stepForward,
    stepBackward,
    reset,
    replay,
    goToStep,
    setSpeed,
  };
}
