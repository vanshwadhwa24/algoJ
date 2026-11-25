package com.algoj.persistence;

import java.time.Instant;

/** A named input the user saved for an algorithm, so it can be loaded again later. */
public record SavedInput(long id, String algorithmId, String name, String payload, Instant createdAt) {}
