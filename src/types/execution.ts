export interface ExecutionStep<T = any> {
  stepIndex: number;
  totalSteps?: number;
  operationType: string;
  description: string;
  codeLine: number;
  state: T;
  counters?: Record<string, number>;
  metadata?: Record<string, any>;
}

export type PlaybackSpeed = 0.25 | 0.5 | 1 | 1.5 | 2 | 4;

export interface PlaybackState {
  currentStepIndex: number;
  totalSteps: number;
  isPlaying: boolean;
  speed: PlaybackSpeed;
  isCompleted: boolean;
}

export interface ComplexityInfo {
  timeBest: string;
  timeAverage: string;
  timeWorst: string;
  spaceAuxiliary: string;
  explanation: string;
}

export interface CodeDefinition {
  language: string;
  lines: string[];
}

export type DSACategory =
  | 'sorting'
  | 'searching'
  | 'linear'
  | 'trees-heaps'
  | 'graphs'
  | 'recursion'
  | 'dynamic-programming';

export interface AlgorithmMetadata {
  id: string;
  name: string;
  category: DSACategory;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  description: string;
  tags: string[];
  complexity: ComplexityInfo;
  code: CodeDefinition;
  learning: {
    intuition: string;
    howItWorks: string[];
    edgeCases: string[];
    advantages: string[];
    limitations: string[];
  };
  defaultInput?: any;
}
