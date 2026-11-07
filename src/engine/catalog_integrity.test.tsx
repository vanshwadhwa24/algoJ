import { describe, it, expect } from 'vitest';
import React from 'react';
import { renderToString } from 'react-dom/server';
import { ALGORITHMS_CATALOG } from '../data/algorithmsCatalog';
import { PRACTICE_QUESTIONS } from '../data/practiceQuestions';
import { LearningPanel } from '../components/learning/LearningPanel';
import { CodePanel } from '../components/workspace/CodePanel';
import { ComplexityPanel } from '../components/workspace/ComplexityPanel';

describe('Catalog Integrity Suite', () => {
  it('all algorithms have complete metadata (name, code, complexity, learning)', () => {
    expect(ALGORITHMS_CATALOG.length).toBe(30);

    for (const algo of ALGORITHMS_CATALOG) {
      expect(algo.id, `Algorithm must have id`).toBeTruthy();
      expect(algo.name, `${algo.id} must have name`).toBeTruthy();
      expect(algo.category, `${algo.id} must have category`).toBeTruthy();
      expect(algo.code, `${algo.id} must have code`).toBeTruthy();
      expect(algo.complexity, `${algo.id} must have complexity`).toBeDefined();
      expect(algo.complexity.timeAverage, `${algo.id} must have timeAverage`).toBeTruthy();
      expect(algo.complexity.spaceAuxiliary, `${algo.id} must have spaceAuxiliary`).toBeTruthy();
      expect(algo.learning, `${algo.id} must have learning`).toBeDefined();
      expect(algo.learning.intuition, `${algo.id} must have intuition`).toBeTruthy();
      expect(algo.learning.howItWorks.length, `${algo.id} must have howItWorks`).toBeGreaterThan(0);
      expect(algo.learning.edgeCases.length, `${algo.id} must have edgeCases`).toBeGreaterThan(0);
      expect(algo.learning.advantages.length, `${algo.id} must have advantages`).toBeGreaterThan(0);
      expect(algo.learning.limitations.length, `${algo.id} must have limitations`).toBeGreaterThan(0);

      // Verify panels render for this algorithm
      const learningHtml = renderToString(<LearningPanel algorithm={algo} />);
      expect(learningHtml.length).toBeGreaterThan(50);

      const codeHtml = renderToString(<CodePanel code={algo.code} activeLine={1} />);
      expect(codeHtml.length).toBeGreaterThan(50);

      const compHtml = renderToString(<ComplexityPanel complexity={algo.complexity} />);
      expect(compHtml.length).toBeGreaterThan(50);
    }
  });

  it('practice questions are valid', () => {
    expect(PRACTICE_QUESTIONS.length).toBeGreaterThan(0);
    for (const q of PRACTICE_QUESTIONS) {
      expect(q.id).toBeTruthy();
      expect(q.question).toBeTruthy();
      expect(q.options.length).toBeGreaterThanOrEqual(2);
      expect(q.correctIndex).toBeGreaterThanOrEqual(0);
      expect(q.correctIndex).toBeLessThan(q.options.length);
    }
  });
});
