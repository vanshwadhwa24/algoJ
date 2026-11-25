package com.algoj.persistence;

import java.util.List;

public interface SavedInputDao {
    /** Saves under (algorithm, name); an existing input with that name is replaced. */
    void save(String algorithmId, String name, String payload);

    List<SavedInput> forAlgorithm(String algorithmId);

    void delete(long id);
}
