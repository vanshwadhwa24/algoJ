package com.algoj.persistence;

public interface QuizAttemptDao {
    /** Stores an answer. Returns true if it earned points (first correct answer for this question). */
    boolean record(String questionId, String algorithmId, boolean correct);

    boolean isAnswered(String questionId);

    /** Questions the user has answered correctly at least once. */
    int correctQuestionCount();

    int attemptCount();
}
