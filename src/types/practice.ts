export interface PracticeQuestion {
  id: string;
  algorithmId: string;
  question: string;
  options: string[];
  correctIndex: number;
  explanation: string;
}

export interface UserProgress {
  completedAlgorithms: string[];
  quizScores: Record<string, number>;
  lastVisitedAlgorithm?: string;
}
