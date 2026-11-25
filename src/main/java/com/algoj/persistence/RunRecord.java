package com.algoj.persistence;

import java.time.Instant;

/** One saved visualization run. */
public record RunRecord(long id, String algorithmId, String input, int steps, Instant createdAt) {}
