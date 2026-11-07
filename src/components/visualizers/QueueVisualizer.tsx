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
          if (slot.state === 'enqueueing') bgColor = 'bg-[#8FD6B0]'; // Mint
          else if (slot.state === 'dequeueing') bgColor = 'bg-[#F5A868]'; // Orange
          else if (isFront) bgColor = 'bg-[#F5D876]'; // Yellow
          else if (isRear) bgColor = 'bg-[#C9B8F5]'; // Lavender

          return (
            <div key={slot.index} className="flex flex-col items-center">
              {/* Pointer tags above */}
              <div className="h-6 flex items-center gap-1 mb-1">
                {isFront && (
                  <span className="bg-[#1A1A1A] text-white text-[10px] font-black px-2 py-0.5 rounded-full border border-[#1A1A1A]">
                    FRONT
                  </span>
                )}
                {isRear && (
                  <span className="bg-[#F5A868] text-[#1A1A1A] text-[10px] font-black px-2 py-0.5 rounded-full border border-[#1A1A1A]">
                    REAR
                  </span>
                )}
              </div>

              {/* Slot Box */}
              <div
                className={`w-14 h-16 sm:w-16 sm:h-20 rounded-2xl border-[2.5px] border-[#1A1A1A] ${
                  isEmpty ? 'bg-[#FAF6EC] border-dashed border-gray-400' : bgColor
                } shadow-[3px_3px_0px_#1A1A1A] flex flex-col justify-between items-center py-2 select-none transition-all`}
              >
                <span className="font-mono font-black text-sm sm:text-base text-[#1A1A1A]">
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
      <div className="flex flex-wrap items-center justify-center gap-3 bg-white border-[2px] border-[#1A1A1A] px-4 py-2 rounded-2xl shadow-[2px_2px_0px_#1A1A1A] text-xs font-bold">
        <span>Mode: <strong className="text-[#1A1A1A]">{isCircular ? 'Circular Queue' : 'Standard Queue'}</strong></span>
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
