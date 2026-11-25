package com.algoj.persistence;

import java.util.List;

public interface RunHistoryDao {
    void record(String algorithmId, String input, int steps);

    /** Most recent first. */
    List<RunRecord> recent(int limit);

    int count();
}
