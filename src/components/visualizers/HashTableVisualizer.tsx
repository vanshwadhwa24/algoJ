import React from 'react';
import { ArrowRight } from 'lucide-react';
import { HashBucket } from '../../engine/structures/hashTable';

interface HashTableVisualizerProps {
  buckets: HashBucket[];
  collisionStrategy: 'chaining' | 'probing';
  activeHash?: number | null;
}

export const HashTableVisualizer: React.FC<HashTableVisualizerProps> = ({
  buckets = [],
  collisionStrategy,
  activeHash,
}) => {
  const safeBuckets = buckets || [];
  return (
    <div className="flex flex-col p-4 w-full space-y-3 overflow-x-auto max-h-[380px]">
      <div className="text-xs font-black text-gray-500 uppercase tracking-wider mb-1 flex items-center justify-between">
        <span>Strategy: {collisionStrategy === 'chaining' ? 'Separate Chaining' : 'Linear Probing (Open Addressing)'}</span>
        {activeHash !== null && activeHash !== undefined && (
          <span className="bg-[#F7EFE1] border border-[#2B2620] px-2 py-0.5 rounded-full text-[#2B2620]">
            Current Hash: {activeHash}
          </span>
        )}
      </div>

      <div className="space-y-2">
        {safeBuckets.map((bucket) => {
          const isTargetBucket = bucket.index === activeHash;
          let bucketBg = 'bg-[#F7EFE1]';
          if (bucket.state === 'found') bucketBg = 'bg-[#9CCBA2]';
          else if (bucket.state === 'collision') bucketBg = 'bg-[#D97B54]';
          else if (bucket.state === 'active') bucketBg = 'bg-[#DFA43C]';

          return (
            <div
              key={bucket.index}
              className={`flex items-center gap-3 p-2 rounded-2xl border border-[#E8DCC4] ${
                isTargetBucket ? 'bg-amber-50 shadow-[0_4px_12px_rgba(43,38,32,0.18)]' : 'bg-white'
              }`}
            >
              {/* Bucket Index badge */}
              <div
                className={`w-12 h-10 rounded-xl border border-[#E8DCC4] ${bucketBg} flex items-center justify-center font-mono font-black text-xs shrink-0 shadow-[0_2px_6px_rgba(43,38,32,0.18)]`}
              >
                [{bucket.index}]
              </div>

              {/* Bucket Contents */}
              <div className="flex items-center gap-2 overflow-x-auto flex-1">
                {bucket.entries.length === 0 ? (
                  <span className="text-xs text-gray-400 font-bold italic">empty</span>
                ) : (
                  bucket.entries.map((entry, eIdx) => {
                    const isTombstone = entry.isDeleted;
                    return (
                      <React.Fragment key={eIdx}>
                        {eIdx > 0 && <ArrowRight className="w-3.5 h-3.5 text-gray-400 shrink-0" />}
                        <div
                          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl border-[1.5px] border-[#2B2620] text-xs font-bold font-mono shadow-[0_2px_6px_rgba(43,38,32,0.18)] ${
                            isTombstone
                              ? 'bg-red-100 text-red-600 line-through border-red-500'
                              : 'bg-white text-[#2B2620]'
                          }`}
                        >
                          {isTombstone ? (
                            <span>[DELETED TOMBSTONE]</span>
                          ) : (
                            <>
                              <span className="text-purple-700">"{entry.key}"</span>
                              <span>:</span>
                              <span className="text-green-700">{entry.value}</span>
                            </>
                          )}
                        </div>
                      </React.Fragment>
                    );
                  })
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
