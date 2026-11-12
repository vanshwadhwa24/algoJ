import React from 'react';
import { QueueSlot } from '../../engine/structures/queue';

interface QueueVisualizerProps {
  slots: QueueSlot[];
  front: number;
  rear: number;
  size: number;
  capacity: number;
  isCircular?: boolean;
}

export const QueueVisualizer: React.FC<QueueVisualizerProps> = ({
  slots = [],
  front,
  rear,
  size,
  capacity,
  isCircular = false,
}) => {
  const safeSlots = slots || [];
  return (
    <div className="flex flex-col items-center justify-center p-6 w-full space-y-6">
      {/* Slots row */}
      <div className="flex flex-wrap items-center justify-center gap-3">
        {safeSlots.map((slot) => {
          const isFront = slot.index === front && size > 0;
          const isRear = slot.index === rear && size > 0;
          const isEmpty = slot.value === null;

          let bgColor = 'bg-white';
          if (slot.state === 'enqueueing') bgColor = 'bg-[#9CCBA2]'; // Mint
          else if (slot.state === 'dequeueing') bgColor = 'bg-[#D97B54]'; // Orange
          else if (isFront) bgColor = 'bg-[#DFA43C]'; // Yellow
          else if (isRear) bgColor = 'bg-[#8FB5AD]'; // Lavender

          return (
            <div key={slot.index} className="flex flex-col items-center">
              {/* Pointer tags above */}
              <div className="h-6 flex items-center gap-1 mb-1">
                {isFront && (
                  <span className="bg-[#2B2620] text-white text-[10px] font-black px-2 py-0.5 rounded-full border border-[#2B2620]">
                    FRONT
                  </span>
                )}
                {isRear && (
                  <span className="bg-[#D97B54] text-[#2B2620] text-[10px] font-black px-2 py-0.5 rounded-full border border-[#2B2620]">
                    REAR
                  </span>
                )}
              </div>

              {/* Slot Box */}
              <div
                className={`w-14 h-16 sm:w-16 sm:h-20 rounded-2xl border border-[#E8DCC4] ${
                  isEmpty ? 'bg-[#F7EFE1] border-dashed border-gray-400' : bgColor
                } shadow-[0_4px_12px_rgba(43,38,32,0.18)] flex flex-col justify-between items-center py-2 select-none transition-all`}
              >
                <span className="font-mono font-black text-sm sm:text-base text-[#2B2620]">
                  {isEmpty ? '-' : slot.value}
                </span>
                <span className="text-[10px] font-bold text-gray-500">
                  [{slot.index}]
                </span>
              </div>

              <span className="text-[10px] font-bold text-gray-500 mt-1">
                slot {slot.index}
              </span>
            </div>
          );
        })}
      </div>

      {/* Mode and Status Pill */}
      <div className="flex flex-wrap items-center justify-center gap-3 bg-white border border-[#E8DCC4] px-4 py-2 rounded-2xl shadow-[0_3px_10px_rgba(43,38,32,0.16)] text-xs font-bold">
        <span>Mode: <strong className="text-[#2B2620]">{isCircular ? 'Circular Queue' : 'Standard Queue'}</strong></span>
        <span>•</span>
        <span>Size: <strong>{size} / {capacity}</strong></span>
        <span>•</span>
        <span>Front index: <strong>{front}</strong></span>
        <span>•</span>
        <span>Rear index: <strong>{rear}</strong></span>
      </div>
    </div>
  );
};
