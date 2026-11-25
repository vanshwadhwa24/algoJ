CREATE TABLE IF NOT EXISTS run_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    algorithm_id TEXT NOT NULL,
    input TEXT NOT NULL,
    steps INTEGER NOT NULL,
    created_at INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS saved_input (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    algorithm_id TEXT NOT NULL,
    name TEXT NOT NULL,
    payload TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    UNIQUE (algorithm_id, name)
);
CREATE TABLE IF NOT EXISTS quiz_attempt (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    question_id TEXT NOT NULL,
    algorithm_id TEXT NOT NULL,
    correct INTEGER NOT NULL,
    created_at INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_quiz_question ON quiz_attempt (question_id);
